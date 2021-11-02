package com.bd.deliverytiger.app.ui.add_order.voucher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.endpoint.OtherApiInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.terms.TermsModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoucherInformationBottomSheet : BottomSheetDialogFragment() {

    private lateinit var titleTV: TextView
    private lateinit var conditionTV: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var agreeBtn: MaterialButton
    private lateinit var disagreeBtn: MaterialButton
    var onTermsAgreed: ((isAgreed: Boolean) -> Unit)? = null

    private lateinit var otherApiInterface: OtherApiInterface
    private var behaviour: BottomSheetBehavior<View>? = null

    companion object {
        fun newInstance(): VoucherInformationBottomSheet = VoucherInformationBottomSheet().apply { }
        val tag = VoucherInformationBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voucher_information_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTV = view.findViewById(R.id.terms_sheet_title_tv)
        conditionTV = view.findViewById(R.id.terms_sheet_condition_text)
        progressBar = view.findViewById(R.id.terms_sheet_progress)
        agreeBtn = view.findViewById(R.id.terms_sheet_condition_agree)
        disagreeBtn = view.findViewById(R.id.terms_sheet_condition_disagree)

        otherApiInterface =
            RetrofitSingleton.getInstance(requireContext()).create(OtherApiInterface::class.java)
        loadTerms()

        agreeBtn.setOnClickListener {
            onTermsAgreed?.invoke(true)
            dismiss()
        }

        disagreeBtn.setOnClickListener {
            onTermsAgreed?.invoke(false)
            dismiss()
        }
    }

    private fun loadTerms() {

        progressBar.visibility = View.VISIBLE
        otherApiInterface.loadTerms().enqueue(object : Callback<GenericResponse<TermsModel>> {
            override fun onFailure(call: Call<GenericResponse<TermsModel>>, t: Throwable) {

                if (isAdded) {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onResponse(
                call: Call<GenericResponse<TermsModel>>,
                response: Response<GenericResponse<TermsModel>>
            ) {

                if (isAdded) {
                    progressBar.visibility = View.GONE
                }
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.model != null) {
                        val model = response.body()!!.model
                        titleTV.text = "ভাউচার ডিসকাউন্ট পাওয়ার নিয়মাবলি"
                        conditionTV.text = HtmlCompat.fromHtml(
                            model.voucherTermsConditions ?: "VoucherTerms and Conditions",
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        agreeBtn.visibility = View.VISIBLE
                        disagreeBtn.visibility = View.VISIBLE

                        behaviour?.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }

        })
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