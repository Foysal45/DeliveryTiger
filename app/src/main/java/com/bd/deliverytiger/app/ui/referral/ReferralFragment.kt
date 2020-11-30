package com.bd.deliverytiger.app.ui.referral

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentReferralBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import timber.log.Timber

@SuppressLint("SetTextI18n")
class ReferralFragment() : Fragment() {

    private var binding: FragmentReferralBinding? = null
    private val viewModel: ReferralViewModel by inject()

    private var refereeOrder: Int = 0

    companion object {
        fun newInstance(): ReferralFragment = ReferralFragment().apply {}
        val tag: String = ReferralFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        // রেফার করে ফ্রি ডেলিভারি
        (activity as HomeActivity).setToolbarTitle(getString(R.string.referral))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentReferralBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchRefereeInfo().observe(viewLifecycleOwner, Observer { model ->
            refereeOrder = model.refereeOrder
            binding?.referCount1?.text = "${DigitConverter.toBanglaDigit(refereeOrder)}টি ফ্রি"
        })

        viewModel.fetchReferrerInfo().observe(viewLifecycleOwner, Observer { model ->
            val referrerOrder = model.referrerOrder
            binding?.referCount?.text = DigitConverter.toBanglaDigit(referrerOrder)
            binding?.msg2?.text = "(প্রতি সফল রেফারের জন্য ${DigitConverter.toBanglaDigit(referrerOrder)} টি)"
        })

        binding?.referBtn?.setOnClickListener {
            buildDynamicLink()
        }

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
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
        })
    }


    private fun buildDynamicLink() {

        val dialog = progressDialog("অপেক্ষা করুন, শেয়ার লিংক তৈরি হচ্ছে")
        dialog.show()

        val referralUrl = "https://deliverytiger.com.bd/sign-up?referrer=${SessionManager.mobile}"
        val uri = Uri.parse(referralUrl)
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = uri
            domainUriPrefix = AppConstant.DOMAIN_URL_PREFIX
            androidParameters {
                minimumVersion = 21
                fallbackUrl = uri
            }
            googleAnalyticsParameters {
                source = "AndroidApp"
            }
            socialMetaTagParameters {
                title = "ডেলিভারি টাইগার"
                description = "বাংলাদেশের প্রথম অনলাইন কুরিয়ার এবং পার্সেল ডেলিভারি মার্কেটপ্লেস"
                imageUrl = Uri.parse("https://static.ajkerdeal.com/images/dt/logo_dt_150.png")
            }
            navigationInfoParameters {
                forcedRedirectEnabled = true
            }
            buildShortDynamicLink().addOnSuccessListener { shortDynamicLink ->
                dialog.dismiss()
                val shortLink = shortDynamicLink.shortLink
                val flowchartLink = shortDynamicLink.previewLink //flowchart link is a debugging URL
                Timber.d("createDynamicLink shortLink: $shortLink flowchartLink: $flowchartLink")

                generateShareContent(shortLink.toString())

            }.addOnFailureListener {
                dialog.dismiss()
                Timber.d(it)
                context?.toast("কোথাও কোনো সমস্যা হচ্ছে")
            }
        }

        //generateShareContent(dynamicLink.uri.toString())
        //dialog.dismiss()
    }

    private fun generateShareContent(url: String) {
        val msg = "সারাদেশে ৫ টি ফ্রি ডেলিভারির সুযোগ নিন!\n\nনিচের লিংকে ক্লিক করে এখনই ডেলিভারি টাইগারে রেজিস্ট্রেশন করুন।\n$url\n\nঅফারটি আগামী ৩০ দিনের জন্য প্রযোজ্য।"
        shareContent(msg)
    }

    private fun shareContent(msg: String) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, msg)
        }.also {
            startActivity(it)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}