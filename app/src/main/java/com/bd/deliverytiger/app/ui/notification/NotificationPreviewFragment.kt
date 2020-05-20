package com.bd.deliverytiger.app.ui.notification

import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.notification.FCMNotification
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bumptech.glide.Glide

class NotificationPreviewFragment : Fragment() {

    private lateinit var titleTV: TextView
    private lateinit var descriptionTV: TextView
    private lateinit var bigTextTV: TextView
    private lateinit var bigImageTV: ImageView

    var model: FCMNotification? = null

    companion object {
        fun newInstance(model: FCMNotification?): NotificationPreviewFragment = NotificationPreviewFragment().apply {
            this.model = model
        }

        val fragmentTag: String = NotificationPreviewFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTV = view.findViewById(R.id.title)
        descriptionTV = view.findViewById(R.id.description)
        bigTextTV = view.findViewById(R.id.bigText)
        bigImageTV = view.findViewById(R.id.bigImage)

        titleTV.text = toHTML(model?.title ?: "")
        descriptionTV.text = toHTML(model?.description ?: "")

        if (!model?.bigText.isNullOrEmpty()) {
            bigTextTV.visibility = View.VISIBLE
            bigTextTV.text = toHTML(model?.bigText ?: "")
        }

        if (!model?.imageLink.isNullOrEmpty()) {
            bigImageTV.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(model?.imageLink)
                .into(bigImageTV)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("নোটিফিকেশন")
    }

    private fun toHTML(text: String): Spanned {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

}
