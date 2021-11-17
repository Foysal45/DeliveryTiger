package com.bd.deliverytiger.app.ui.accounts_mail_format

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentAccountsMailFormatBottomsheetBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

class AccountsMailFormatBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentAccountsMailFormatBottomsheetBinding? = null

    companion object {

        fun newInstance(): AccountsMailFormatBottomSheet = AccountsMailFormatBottomSheet().apply {}
        val tag: String = AccountsMailFormatBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentAccountsMailFormatBottomsheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding?.top?.text = "ব্যাংক ট্রান্সফার এক্টিভেট করতে হলে আপনার নিম্নোক্ত তথ্য সহ ইমেইল করুন"
        binding?.email?.text = HtmlCompat.fromHtml("<font color='#00844A'>payment@deliverytiger.com.bd</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding?.body?.text = HtmlCompat.fromHtml(
                    "<p>একাউন্ট নামঃ</p>\n" +
                    "<p>একাউন্ট নম্বরঃ</p>\n" +
                    "<p>ব্যাংক নামঃ</p>\n" +
                    "<p>ব্রাঞ্চ নামঃ</p>\n" +
                    "<p>ব্যাংকে প্রদেয় মোবাইল নম্বরঃ</p>\n", HtmlCompat.FROM_HTML_MODE_LEGACY
        )

    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        val metrics = resources.displayMetrics
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    BottomSheetBehavior.from(bottomSheet).peekHeight = metrics.heightPixels
                }
            }
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            BottomSheetBehavior.from(bottomSheet).isHideable = false
            BottomSheetBehavior.from(bottomSheet)
                .addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(p0: View, p1: Float) {
                        BottomSheetBehavior.from(bottomSheet).state =
                            BottomSheetBehavior.STATE_EXPANDED
                    }

                    override fun onStateChanged(p0: View, p1: Int) {
                        /*if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                        }*/
                    }
                })
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}