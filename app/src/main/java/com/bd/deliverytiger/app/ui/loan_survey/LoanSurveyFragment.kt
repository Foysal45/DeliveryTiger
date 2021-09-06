package com.bd.deliverytiger.app.ui.loan_survey

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.loan_survey.LoanSurveyRequestBody
import com.bd.deliverytiger.app.databinding.FragmentLoanSurveyBinding
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import org.koin.android.ext.android.inject
import timber.log.Timber

class LoanSurveyFragment : Fragment() {

    private var binding: FragmentLoanSurveyBinding? = null
    private val viewModel: LoanSurveryViewModel by inject()

    private var merchantGender = ""

    private var loanRange = ""
    private var monthlyTransaction = ""

    private var hasBankAccount = false
    private var hasPhysicalShop = false
    private var hasTradeLicence = false

    private var imagePath: String = ""
    private var imagePickFlag = 0
    private var imageTradeLicencePath: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLoanSurveyBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (SessionManager.isSurveyComplete) {
            warning()
        }
        initClickListener()
    }

    private fun initClickListener() {
        binding?.applyLoanBtn?.setOnClickListener {
            if (verify()) {

                if (imagePickFlag == 1) {
                    val requestBody = LoanSurveyRequestBody(SessionManager.courierUserId, merchantGender,
                        "https://static.ajkerdeal.com/delivery_tiger/trade_license/trade_${SessionManager.courierUserId}.jpg",
                        loanRange, monthlyTransaction, hasBankAccount, hasPhysicalShop
                    )
                    Timber.d("requestBody 1 $requestBody")
                    uploadImage(
                        "trade_${SessionManager.courierUserId}.jpg",
                        "delivery_tiger/trade_license",
                        imageTradeLicencePath, requestBody
                    )
                } else {
                    val requestBody = LoanSurveyRequestBody(SessionManager.courierUserId, merchantGender,
                        "",
                        loanRange, monthlyTransaction, hasBankAccount, hasPhysicalShop
                    )
                    Timber.d("requestBody 2 $requestBody")
                    submitLoanSurveyData(requestBody)
                }
            }
        }

        //region merchantGender
        binding?.merchantGenderRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.merchantGenderMale -> {
                    merchantGender = "male"
                }
                R.id.merchantGenderFemale -> {
                    merchantGender = "female"
                }
                R.id.merchantGenderOthers -> {
                    merchantGender = "others"
                }
            }
        }
        //endregion

        //region merchantHasBankAccount
        binding?.merchantHasBankAccountRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.merchantHasBankAccountYes -> {
                    hasBankAccount = true
                }
                R.id.merchantHasBankAccountNo -> {
                    hasBankAccount = false
                }
            }
        }
        //endregion

        //region merchantPhysicalShopExists
        binding?.merchantPhysicalShopExistsRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.merchantPhysicalShopExistsYes -> {
                    hasPhysicalShop = true
                }
                R.id.merchantPhysicalShopExistsNo -> {
                    hasPhysicalShop = false
                }
            }
        }
        //endregion

        //region merchantTradeLicence
        binding?.merchantHasTradeLicenceRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.merchantHasTradeLicenceYes -> {
                    hasTradeLicence = true
                    imagePickFlag = 1
                    binding?.merchantTradeLicenceLayout?.isVisible = true
                }
                R.id.merchantHasTradeLicenceNo -> {
                    hasTradeLicence = false
                    imagePickFlag = 0
                    binding?.merchantTradeLicenceLayout?.isVisible = false
                }
            }
        }
        //endregion

        binding?.merchantTradeLicenceLayout?.setOnClickListener {
            pickImage()
        }

    }

    private fun pickImage() {
        ImagePicker.with(this)
            //.compress(200)
            .crop(1.5f, 2f)
            .createIntent { intent ->
                startImagePickerResult.launch(intent)
            }
    }

    private fun verify(): Boolean {

        if (merchantGender.isEmpty()) {
            context?.toast("Select Gender")
            return false
        }

        loanRange = binding?.loanRangeET?.text.toString()
        if (loanRange.isEmpty()) {
            context?.toast("Please enter loan range")
            return false
        }

        monthlyTransaction = binding?.monthlyTransactionET?.text.toString()
        if (monthlyTransaction.isEmpty()) {
            context?.toast("Please enter Monthly Transaction")
            return false
        }

        if (imagePickFlag == 1) {
            if (imageTradeLicencePath.isEmpty()) {
                context?.toast("ট্রেড লাইসেন্স এর ছবি অ্যাড করুন")
                return false
            }
        }


        return true
    }

    private val startImagePickerResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data!!

            when (imagePickFlag) {
                1 -> {
                    binding?.imageTradeLicenceAddIV?.isVisible = false
                    binding?.imageTradeLicencePickedIV?.isVisible = true

                    imagePath = uri.path ?: ""
                    binding?.imageTradeLicencePickedIV?.let { view ->
                        Glide.with(requireContext())
                            .load(imagePath)
                            .apply(RequestOptions().placeholder(R.drawable.ic_banner_place))
                            .into(view)
                    }
                    imageTradeLicencePath = imagePath
                    //imagePickFlag = 0
                }
                2 -> {
                    /*binding?.imagePayslipAddIV?.isVisible = false
                    binding?.imagePayslipPickedIV?.isVisible = true

                    imagePath = uri.path ?: ""
                    binding?.imagePayslipPickedIV?.let { view ->
                        Glide.with(requireContext())
                            .load(imagePath)
                            .apply(RequestOptions().placeholder(R.drawable.ic_banner_place))
                            .into(view)
                    }
                    imagePayslipPath = imagePath*/
                    imagePickFlag = 0
                }
                3 -> {
                    //binding?.imageIdAddIV?.isVisible = false
                    //binding?.imageIdPickedIV?.isVisible = true

                    imagePath = uri.path ?: ""
                    /*binding?.imageIdPickedIV?.let { view ->
                        Glide.with(requireContext())
                            .load(imagePath)
                            .apply(RequestOptions().placeholder(R.drawable.ic_banner_place))
                            .into(view)
                    }
                    imageIdPath = imagePath*/
                    imagePickFlag = 0
                }
                else -> {
                    imagePickFlag = 0
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            context?.toast(ImagePicker.getError(data))
            imagePickFlag = 0
        }
    }

    private fun uploadImage(fileName: String, imagePath: String, fileUrl: String, requestBody: LoanSurveyRequestBody) {

        val progressDialog = progressDialog()
        progressDialog.show()

        Timber.d("requestBody $fileName, $imagePath, $fileUrl")
        viewModel.imageUploadForFile(requireContext(), fileName, imagePath, fileUrl).observe( viewLifecycleOwner, Observer { model ->
            progressDialog.dismiss()
            showAlert()
            if (model) {
                submitLoanSurveyData(requestBody)
                context?.toast("Uploaded successfully")

            } else {
                context?.toast("Uploaded Failed")
            }
        })
    }

    private fun submitLoanSurveyData(requestBody: LoanSurveyRequestBody) {
        viewModel.submitLoanSurvey(requestBody).observe(viewLifecycleOwner, Observer {
            if (it == true) {
                SessionManager.isSurveyComplete = true
            }
        })
    }

    private fun showAlert() {
        val titleText = "নির্দেশনা"
        val descriptionText = "সার্ভেটি পূরণ করার জন্যে ধন্যবাদ।"
        alert(titleText, descriptionText, false) {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }.show()
    }

    private fun warning() {
        val titleText = "নির্দেশনা"
        val descriptionText = "আপনি ইতিপূর্বে একবার সার্ভেটি পূরণ করেছেন, আপনি কি আবার পূরণ করতে ইচ্ছুক?"
        alert(titleText, descriptionText, true,"ঠিক আছে", "ক্যানসেল") {
            if (it == AlertDialog.BUTTON_NEGATIVE) {
                findNavController().popBackStack()
            }
        }.show()
    }
}