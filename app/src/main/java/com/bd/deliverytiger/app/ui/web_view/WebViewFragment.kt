package com.bd.deliverytiger.app.ui.web_view


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.ui.home.HomeActivity
import org.koin.android.ext.android.inject

class WebViewFragment : Fragment() {

    private val TAG = "WebViewFragment"
    private lateinit var mContext: Context

    private lateinit var webView: WebView
    private var progressBar: ProgressBar? = null

    private var webTitle: String = ""
    private var loadUrl: String = ""

    private val repository: AppRepository by inject()

    companion object {
        fun newInstance(url: String, title: String): WebViewFragment = WebViewFragment().apply {
            this.loadUrl = url
            this.webTitle = title
        }
        val tag = WebViewFragment::class.java.name
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.webView)
        progressBar = view.findViewById(R.id.custom_progress_bar)

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowFileAccess = true
        webSettings.setSupportZoom(false)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        //webSettings.loadWithOverviewMode = true
        //webSettings.useWideViewPort = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.clearCache(true)
        webView.clearHistory()
        webView.isHorizontalScrollBarEnabled = false
        webView.addJavascriptInterface(WebAppInterface(mContext,repository,arguments), "Android")
        webView.webViewClient = Callback()

        //webViewTitle.text = webTitle // set toolbar title
        webView.loadUrl(loadUrl)

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(webTitle)
    }

    inner class Callback : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar?.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar?.visibility = View.GONE
        }

        @SuppressWarnings("deprecation")
        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            progressBar?.visibility = View.GONE
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            //super.onReceivedSslError(view, handler, error);
            val builder = AlertDialog.Builder(mContext)
            var message = "SSL Certificate error."
            when (error.primaryError) {
                SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                SslError.SSL_EXPIRED -> message = "The certificate has expired."
                SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
            }
            message += " Do you want to continue anyway?"

            builder.setTitle("SSL Certificate Error")
            builder.setMessage(message)
            builder.setPositiveButton("continue") { dialog, which -> handler.proceed() }
            builder.setNegativeButton("cancel") { dialog, which -> handler.cancel() }
            val dialog = builder.create()
            dialog.show()
        }
    }

}
