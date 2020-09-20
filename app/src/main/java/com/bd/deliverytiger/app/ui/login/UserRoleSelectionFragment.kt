package com.bd.deliverytiger.app.ui.login
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.AppConstant
import com.bd.deliverytiger.app.utils.SessionManager

class UserRoleSelectionFragment : Fragment() {

    private lateinit var layoutUserMerchant: LinearLayout
    private lateinit var layoutUserCustomer: LinearLayout
    private lateinit var webView: WebView

    companion object {
        fun newInstance(): UserRoleSelectionFragment = UserRoleSelectionFragment()
        val tag: String = UserRoleSelectionFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_role_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutUserCustomer = view.findViewById(R.id.layout_user_customer)
        layoutUserMerchant = view.findViewById(R.id.layout_user_merchant)
        webView = view.findViewById(R.id.webView)

        initWebView()

        layoutUserMerchant.setOnClickListener {
            goToUserRoleMerchant()
        }

        layoutUserCustomer.setOnClickListener {
            goToOrderTrackingFragment()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            //loadWithOverviewMode = true
            //useWideViewPort = true
        }
        with(webView) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            clearHistory()
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            //addJavascriptInterface(WebAppInterface(requireContext(), repository, bundle), "Android")
            //webViewClient = Callback()
            //clearCache(true)
        }

        webView.loadUrl(AppConstant.CHARGE_CALCULATOR)
    }

    private fun goToUserRoleMerchant(){

        if (SessionManager.isLogin) {
            val intentMerchantLogin = Intent(activity, HomeActivity::class.java)
            activity?.startActivity(intentMerchantLogin)
            activity?.finish()
        } else {
            addLoginFragment()
        }
    }

    private fun goToOrderTrackingFragment(){

        val fragment: OrderTrackingFragment = OrderTrackingFragment.newInstance("")
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, OrderTrackingFragment.tag)
        ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()
    }

    private fun addLoginFragment(){

        val fragment = LoginFragment.newInstance(false)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, LoginFragment.tag)
        ft?.addToBackStack(LoginFragment.tag)
        ft?.commit()
    }
}
