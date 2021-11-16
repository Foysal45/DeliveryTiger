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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AccountsMailFormatBottomSheet : BottomSheetDialogFragment() {
    private lateinit var textBody: TextView
    private lateinit var textHeader: TextView
    private lateinit var progressBar: ProgressBar

    private var behaviour: BottomSheetBehavior<View>? = null

    companion object {
        fun newInstance(): AccountsMailFormatBottomSheet = AccountsMailFormatBottomSheet().apply { }
        val tag = AccountsMailFormatBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_accounts_mail_format_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textBody = view.findViewById(R.id.accountsMailFormatTextHeader)
        textHeader = view.findViewById(R.id.accountsMailFormatText)
        progressBar = view.findViewById(R.id.terms_sheet_progress)

        textBody.text = HtmlCompat.fromHtml(
            "<p>ব্যাংক ট্রান্সফার এক্টিভেট করতে হলে আপনার নিম্নোক্ত তথ্য সহ ইমেইল করুন</p>\n" +
                    "<p>এখানে - payment@deliverytiger.com.bd </p>\n" +
                    "<p>একাউন্ট নামঃ</p>\n" +
                    "<p>একাউন্ট নম্বরঃ</p>\n" +
                    "<p>ব্যাংক নামঃ</p>\n" +
                    "<p>ব্রাঞ্চ নামঃ</p>\n" +
                    "<p>ব্যাংকে প্রদেয় মোবাইল নম্বরঃ</p>\n", HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        behaviour?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet: FrameLayout? =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            behaviour = BottomSheetBehavior.from(bottomSheet)
        }
    }
}