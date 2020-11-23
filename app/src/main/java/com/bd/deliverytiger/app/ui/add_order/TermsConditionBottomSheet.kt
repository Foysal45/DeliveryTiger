package com.bd.deliverytiger.app.ui.add_order


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
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

/**
 * A simple [Fragment] subclass.
 */
class TermsConditionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var conditionTV: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var agreeBtn: MaterialButton
    private lateinit var disagreeBtn: MaterialButton
    var onTermsAgreed: ((isAgreed: Boolean) -> Unit)? = null

    private lateinit var otherApiInterface: OtherApiInterface
    private var behaviour: BottomSheetBehavior<View>? = null

    companion object{
        fun newInstance(): TermsConditionBottomSheet = TermsConditionBottomSheet().apply {  }
        val tag = TermsConditionBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_condition_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conditionTV = view.findViewById(R.id.terms_sheet_condition_text)
        progressBar = view.findViewById(R.id.terms_sheet_progress)
        agreeBtn = view.findViewById(R.id.terms_sheet_condition_agree)
        disagreeBtn = view.findViewById(R.id.terms_sheet_condition_disagree)

        otherApiInterface = RetrofitSingleton.getInstance(requireContext()).create(OtherApiInterface::class.java)
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

                if (isAdded){
                    progressBar.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call<GenericResponse<TermsModel>>, response: Response<GenericResponse<TermsModel>>) {

                if (isAdded){
                    progressBar.visibility = View.GONE
                }
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.model != null){
                        val model = response.body()!!.model
                        conditionTV.text = HtmlCompat.fromHtml(model.termsConditions ?: "Terms and Conditions", HtmlCompat.FROM_HTML_MODE_LEGACY)

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
        //dialog?.setCanceledOnTouchOutside(false)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            behaviour = BottomSheetBehavior.from(bottomSheet)
        }

        //val metrics = resources.displayMetrics
       /* BottomSheetBehavior.from(bottomSheet)?.peekHeight = metrics.heightPixels / 2
        BottomSheetBehavior.from(bottomSheet)?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        BottomSheetBehavior.from(bottomSheet)?.skipCollapsed = true
        BottomSheetBehavior.from(bottomSheet)?.isHideable = false
        BottomSheetBehavior.from(bottomSheet)?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                if (isAdded) {

                }
            }

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                    BottomSheetBehavior.from(bottomSheet)?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }
        })*/
    }

}
