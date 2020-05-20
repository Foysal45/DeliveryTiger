package com.bd.deliverytiger.app.ui.dialog

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bd.deliverytiger.app.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class PopupDialog : DialogFragment() {

    private var imageUrl: String? = null

    companion object {
        fun newInstance(imageUrl: String?): PopupDialog = PopupDialog().apply {
            this.imageUrl = imageUrl
        }
        val tag = PopupDialog::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.MyAlertDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_popup_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeLayout: FrameLayout = view.findViewById(R.id.closeLayout)
        val bannerIV: ImageView = view.findViewById(R.id.banner)


        Glide.with(requireContext())
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    bannerIV.setImageBitmap(resource)
                    closeLayout.visibility = View.VISIBLE
                }
            })

        closeLayout.setOnClickListener { dismiss() }

    }

}
