package com.bd.deliverytiger.app.ui.notification

import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentNotificationPreviewBinding
import com.bd.deliverytiger.app.fcm.FCMData
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bumptech.glide.Glide

class NotificationPreviewFragment : Fragment() {

    private var binding: FragmentNotificationPreviewBinding? = null
    private var model: FCMData? = null

    companion object {
        fun newInstance(): NotificationPreviewFragment = NotificationPreviewFragment().apply {}

        val fragmentTag: String = NotificationPreviewFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentNotificationPreviewBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = arguments?.getParcelable("fcmData")

        checkIfShouldGoToLoanSurvey()
       /* binding?.title?.setOnClickListener {
            checkIfShouldGoToLoanSurvey()
        }
        binding?.description?.setOnClickListener {
            checkIfShouldGoToLoanSurvey()
        }
        binding?.bigText?.setOnClickListener {
            checkIfShouldGoToLoanSurvey()
        }
        binding?.bigImage?.setOnClickListener {
            checkIfShouldGoToLoanSurvey()
        }*/


        binding?.title?.text = toHTML(model?.title ?: "")
        binding?.description?.text = toHTML(model?.body ?: "")

        if (!model?.bigText.isNullOrEmpty()) {
            binding?.bigText?.visibility = View.VISIBLE
            binding?.bigText?.text = toHTML(model?.bigText ?: "")
        }

        if (!model?.imageUrl.isNullOrEmpty()) {
            binding?.bigImage?.visibility = View.VISIBLE
            binding?.bigImage?.let { imageView ->
                Glide.with(requireContext())
                    .load(model?.imageUrl)
                    .into(imageView)
            }
        }
    }
    private fun checkIfShouldGoToLoanSurvey() {
        if (model?.body?.contains("deliverytiger.com/loan") == true) {
            findNavController().navigate(R.id.action_nav_notification_preview_to_nav_loanSurvey)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.notification))
    }

    private fun toHTML(text: String): Spanned {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
