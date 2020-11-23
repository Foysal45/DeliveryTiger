package com.bd.deliverytiger.app.ui.referral

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentReferralBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.toast
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class ReferralFragment() : Fragment() {

    private var binding: FragmentReferralBinding? = null


    companion object {
        fun newInstance(): ReferralFragment = ReferralFragment().apply {

        }

        val tag: String = ReferralFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.referral))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentReferralBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.referBtn?.setOnClickListener {
            shortDynamicLink()
        }

        /*viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    context?.toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        })*/
    }

    private fun shareContent(msg: String) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, msg)
        }.also {
            startActivity(it)
        }
    }

    private fun shortDynamicLink() {

        buildDynamicLink("https://static.ajkerdeal.com/images/dt/newlogin/tiger-logo.svg")

        /*Firebase.dynamicLinks.shortLinkAsync {
            longLink = Uri.parse()
        }.addOnSuccessListener { shortDynamicLink ->
            val shortLink = shortDynamicLink.shortLink
            val flowchartLink = shortDynamicLink.previewLink //flowchart link is a debugging URL
            Timber.d("createDynamicLink shortLink: $shortLink flowchartLink: $flowchartLink")
        }.addOnFailureListener {
            Timber.d(it)
            context?.toast("কোথাও কোনো সমস্যা হচ্ছে")
        }*/

    }

    private fun buildDynamicLink(image: String?) {

        val uri = Uri.parse("https://deliverytiger.com.bd/sign-up")

        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = uri
            domainUriPrefix = "https://deliverytiger.page.link"
            socialMetaTagParameters {
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle("ডেলিভারি টাইগার")
                    .setDescription("রেজিস্ট্রেশন করুন এখনই")
                    .setImageUrl(Uri.parse(image))
                    .build()
            }
            androidParameters {
                DynamicLink.AndroidParameters.Builder().setFallbackUrl(uri).build()
            }
            googleAnalyticsParameters {
                DynamicLink.GoogleAnalyticsParameters.Builder().setSource("AndroidApp").build()
            }
            buildShortDynamicLink().addOnSuccessListener {shortDynamicLink ->
                val shortLink = shortDynamicLink.shortLink
                val flowchartLink = shortDynamicLink.previewLink //flowchart link is a debugging URL
                Timber.d("createDynamicLink shortLink: $shortLink flowchartLink: $flowchartLink")

                shareContent(shortLink.toString())

            }.addOnFailureListener {
                Timber.d(it)
                context?.toast("কোথাও কোনো সমস্যা হচ্ছে")
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}