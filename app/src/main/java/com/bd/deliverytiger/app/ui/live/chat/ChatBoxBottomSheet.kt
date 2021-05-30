package com.bd.deliverytiger.app.ui.live.chat

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentChatBoxBinding
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

class ChatBoxBottomSheet(): BottomSheetDialogFragment() {

    private var binding: FragmentChatBoxBinding? = null
    var onItemClicked: ((message: String) -> Unit)? = null

    companion object {
        fun newInstance(): ChatBoxBottomSheet = ChatBoxBottomSheet().apply {

        }
        val tag: String = ChatBoxBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogThemeTransparent)
    }

    override fun onStart() {
        super.onStart()

        val dialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        //val metrics = resources.displayMetrics

        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parent?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
                isHideable = false

            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        hideKeyboard()
        super.onCancel(dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return FragmentChatBoxBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.messageET?.requestFocus()

        binding?.sendBtn?.setOnClickListener {

            val msg = binding?.messageET?.text?.toString()?.trim() ?: ""
            if (msg.isNotEmpty()) {
                hideKeyboard()
                onItemClicked?.invoke(msg)
            }
        }
    }

}