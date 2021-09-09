package com.bd.deliverytiger.app.ui.loan_survey

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.loan_survey.CourierModel
import com.bd.deliverytiger.app.api.model.loan_survey.LoanSurveyRequestBody
import com.bd.deliverytiger.app.api.model.loan_survey.SelectedCourierModel
import com.bd.deliverytiger.app.databinding.FragmentLoanSurveyBinding
import com.bd.deliverytiger.app.ui.dana.user_details_info.LoanSurveyAdapter
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.github.dhaval2404.imagepicker.ImagePicker
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class LoanSurveyFragment : Fragment() {

    private var binding: FragmentLoanSurveyBinding? = null
    private val viewModel: LoanSurveryViewModel by inject()

    private val dataAdapter = LoanSurveyAdapter()

    private var courierList: MutableList<CourierModel> = mutableListOf()
    private var selectedCourierList: MutableList<SelectedCourierModel> = mutableListOf()

    private var merchantGender = ""

    private var loanRange = ""
    private var monthlyTransaction = ""

    private var totalMonthlyCOD = ""
    private var guarantorName = ""
    private var guarantorNumber = ""

    private var hasBankAccount = false
    private var hasPhysicalShop = false
    private var hasTradeLicence = false
    private var hasGuarantor = false

    private var imagePath: String = ""
    private var imagePickFlag = 0
    private var imageTradeLicencePath: String = ""


    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.loan_survey))
    }

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
        fetchBanner()
        init()
        fetchCourierList()
        initClickListener()
    }

    private fun init() {
        binding?.recyclerViewOtherServices?.let { recyclerView ->
            recyclerView.apply {
                layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
                adapter = dataAdapter
                itemAnimator = null
            }
        }
    }

    private fun initClickListener() {

        dataAdapter.onItemClicked = { model, position ->
            //context?.toast(model)
            dataAdapter.multipleSelection(model, position)
        }


        binding?.applyLoanBtn?.setOnClickListener {
            if (verify()) {

                val requestBody = LoanSurveyRequestBody(
                    SessionManager.courierUserId, merchantGender,
                    "",
                    loanRange, monthlyTransaction, hasBankAccount, hasPhysicalShop,
                    totalMonthlyCOD, guarantorName, guarantorNumber
                )
                if (imagePickFlag == 1) {
                    requestBody.apply {
                        tradeLicenseImageUrl = "https://static.ajkerdeal.com/delivery_tiger/trade_license/trade_${SessionManager.courierUserId}.jpg"
                    }
                    Timber.d("requestBody 1 $requestBody")

                    uploadImage(
                        "trade_${SessionManager.courierUserId}.jpg",
                        "delivery_tiger/trade_license",
                        imageTradeLicencePath, requestBody
                    )
                } else {
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

        //region merchantHasGuarantor
        binding?.merchantHasGuarantorRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.merchantHasGuarantorYes -> {
                    hasGuarantor = true
                    binding?.merchantGuarantorLayout?.isVisible = true
                }
                R.id.merchantHasGuarantorNo -> {
                    hasGuarantor = false
                    binding?.merchantGuarantorLayout?.isVisible = false
                }
            }
        }
        //endregion

    }

    private fun fetchCourierList() {
        courierList.clear()

        viewModel.fetchCourierList().observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                courierList.addAll(list)
                dataAdapter.initLoad(courierList)
            }
        })
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

        totalMonthlyCOD = binding?.totalCODFromOtherServicesET?.text.toString()
        if (totalMonthlyCOD.isEmpty()) {
            context?.toast("Please enter Total Monthly COD")
            return false
        }

        if (dataAdapter.getSelectedItemModelList().isEmpty()) {
            context?.toast("Please select other courier service you use")
            return false
        }

        if (binding?.merchantHasBankAccountRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("Please select a bank account option")
            return false
        }

        if (binding?.merchantHasTradeLicenceRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("Please select a trade license option")
            return false
        }

        if (binding?.merchantHasGuarantorRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("Please select a guarantor option")
            return false
        }

        if (imagePickFlag == 1) {
            if (imageTradeLicencePath.isEmpty()) {
                context?.toast("ট্রেড লাইসেন্স এর ছবি অ্যাড করুন")
                return false
            }
        }

        guarantorName = binding?.merchantGuarantorNameET?.text.toString()
        guarantorNumber = binding?.merchantGuarantorNumberET?.text.toString()
        if (hasGuarantor) {
            if (guarantorName.isEmpty()) {
                context?.toast("Please Fill Guarantor Name")
                return false
            } else if (guarantorNumber.isEmpty() || guarantorNumber.length != 11) {
                context?.toast("Please Fill Guarantor Number")
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
        viewModel.submitLoanSurvey(requestBody).observe(viewLifecycleOwner, Observer { model ->
            SessionManager.isSurveyComplete = true
            val tempLoanSurveyId = model.loanSurveyId

            Timber.d("requestBody 3 ${model.loanSurveyId}")

            selectedCourierList.clear()
            for (item in dataAdapter.getSelectedItemModelList()) {
                item.apply {
                    loanSurveyId = tempLoanSurveyId
                }
                selectedCourierList.add(item)
            }

            Timber.d("requestBody 3 $selectedCourierList")
            viewModel.submitCourierList(selectedCourierList)
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

    private fun fetchBanner() {
        val options = RequestOptions()
            .placeholder(R.drawable.ic_banner_place)
            .signature(ObjectKey(Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toString()))
        binding?.bannerImage?.let { image ->
            Glide.with(image)
                .load("https://static.ajkerdeal.com/images/merchant/loan_banner.jpg")
                .apply(options)
                .into(image)
        }
    }
}