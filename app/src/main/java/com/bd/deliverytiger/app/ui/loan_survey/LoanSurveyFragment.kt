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
import androidx.recyclerview.widget.GridLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.loan_survey.CourierModel
import com.bd.deliverytiger.app.api.model.loan_survey.LoanSurveyRequestBodyV2
import com.bd.deliverytiger.app.api.model.loan_survey.SelectedCourierModel
import com.bd.deliverytiger.app.databinding.FragmentLoanSurveyBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.loan_survey.adapters.LocalUniversalAdapter
import com.bd.deliverytiger.app.utils.*
import com.bd.deliverytiger.app.utils.Validator.isValidMobileNumber
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.github.dhaval2404.imagepicker.ImagePicker
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LoanSurveyFragment : Fragment() {

    private var binding: FragmentLoanSurveyBinding? = null
    private val viewModel: LoanSurveryViewModel by inject()

    private val dataAdapter = LoanSurveyAdapter()

    private var courierList: MutableList<CourierModel> = mutableListOf()
    private var selectedCourierList: MutableList<SelectedCourierModel> = mutableListOf()

    private var merchantName = ""
    private var merchantGender = ""

    private var loanRange = ""
    private var monthlyTransaction = ""

    private var totalMonthlyCOD = ""
    private var totalMonthlyAverageSell = ""
    private var previousTakingLoanAmount = 0
    private var bankName = ""
    private var guarantorName = ""
    private var guarantorNumber = ""

    private var hasCreditCard = false
    private var hasTin = false
    private var hasBankAccount = false
    private var hasPhysicalShop = false
    private var isOwnerOfShop = false
    private var hasTradeLicence = false
    private var hasGuarantor = false
    private var hasPreviousLoan = false

    private var imagePath: String = ""
    private var imagePickFlag = 0
    private var imageTradeLicencePath: String = ""

    private var selectedEducation = ""
    private var selectedAverageBasket = ""
    private var selectedKnownMerchantDuration = ""
    private var selectedAverageOrder = ""
    private var selectedMarketPosition = ""
    private var selectedOwnerShipOfMarket = ""


    private val adapterAge = LocalUniversalAdapter()
    private val familyMemNumAdapter = LocalUniversalAdapter()
    private val locationAdapter = LocalUniversalAdapter()
    private val marriageStatusAdapter = LocalUniversalAdapter()


    private val recommendationAdapter = LocalUniversalAdapter()
    private val houseOwnerAdapter = LocalUniversalAdapter()


    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.loan_survey))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        initViews()
    }

    private fun initViews() {
        ageRecycler()
        setUpEduactionSpinner()
        familyMemNumRecycler()
        homeLocationRecycler()
        marriageStatusRecycler()
        setUpAverageBasketSpinner()
        setUpSpinnerKnownToMerchnatSpinner()
        recommendationRecycler()
        houseOwnerRecycler()
        setUpSpinnerAverageOrderSpinner()
        //setUpSpinnerMarketPositionSpinner()
    }

    private fun init() {
        binding?.recyclerViewOtherServices?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
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
        //2021-11-01T09:17:08.977Z
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        binding?.applyLoanBtn?.setOnClickListener {
            if (verify()) {
                Timber.d("dataDebud $previousTakingLoanAmount $bankName")
                val requestBody = LoanSurveyRequestBodyV2(
                    age = adapterAge.selectedItem,
                    applicationDate = sdf.format(Date().time),
                    bankName = binding?.bankNameET?.text.toString(),
                    basketvalue = selectedAverageBasket,
                    cardHolder = binding?.creditCardName?.text.toString(),
                    cardLimit = binding?.creditCardLimit?.text.toString(),
                    courierUserId = SessionManager.courierUserId,
                    eduLevel = selectedEducation,
                    famMem = familyMemNumAdapter.selectedItem,
                    gender = merchantGender,
                    guarantorMobile = guarantorNumber,
                    guarantorName = guarantorName,
                    hasCreditCard = binding?.haveAnyCreditCardRadioGroup?.checkedRadioButtonId == R.id.yes_haveAnyCreditCard_radio_button,
                    hasTin = binding?.haveAnyTIINRadioGroup?.checkedRadioButtonId == R.id.yes_haveAnyTeam_radio_button,
                    homeOwnership = houseOwnerAdapter.selectedItem,
                    interestedAmount = if (binding?.loanAMountET?.text.toString().trim().isEmpty()) 0 else binding?.loanAMountET?.text.toString().toInt(),
                    isBankAccount = hasBankAccount,
                    isLocalShop = isOwnerOfShop,
                    loanAmount = if(hasPreviousLoan) previousTakingLoanAmount else 0,
                    loanEmi = if (binding?.loanRepayRadioGroupType?.checkedRadioButtonId == R.id.loanRepayWeekly) "weekly" else "monthly",
                    loanSurveyId = 0,
                    married = marriageStatusAdapter.selectedItem,
                    merchantName = merchantName,
                    monthlyExp = totalMonthlyCOD,
                    monthlyOrder = selectedAverageOrder,
                    monthlyTotalAverageSale = if (hasPhysicalShop) totalMonthlyAverageSell.toInt() else 0,
                    monthlyTotalCodAmount = totalMonthlyCOD.toInt(),
                    recommend = recommendationAdapter.selectedItem,
                    relationMarchent = selectedKnownMerchantDuration,
                    repayType = if (binding?.loanRepayRadioGroupType?.checkedRadioButtonId == R.id.loanRepayWeekly) "weekly" else "monthly",
                    shopOwnership = selectedOwnerShipOfMarket,
                    tinNumber = binding?.teamTIINNumberET?.text.toString(),
                    tradeLicenseImageUrl = imageTradeLicencePath,
                    transactionAmount = totalMonthlyCOD.toInt()
                    /*SessionManager.courierUserId,
                    merchantName,
                    merchantGender,
                    "",
                    loanRange,
                    monthlyTransaction,
                    hasBankAccount,
                    hasPhysicalShop,
                    totalMonthlyAverageSell,
                    totalMonthlyCOD,
                    guarantorName,
                    guarantorNumber,
                    0,
                    previousTakingLoanAmount,
                    bankName*/
                )
                if (imagePickFlag == 1) {
                    requestBody.apply {
                        tradeLicenseImageUrl =
                            "https://static.ajkerdeal.com/delivery_tiger/trade_license/trade_${SessionManager.courierUserId}.jpg"
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
            binding?.merchantNameETLayout?.isVisible = true
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
                    binding?.totalMonthlyAverageSellETLayout?.isVisible = true
                }
                R.id.merchantPhysicalShopExistsNo -> {
                    hasPhysicalShop = false
                    binding?.totalMonthlyAverageSellETLayout?.isVisible = false
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

        // region merchantPreviousLoan
        binding?.merchantTakeLoanRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.merchantTakeLoanAccountYes -> {
                    hasPreviousLoan = true
                    binding?.merchantLoanAmountETLayout?.isVisible = true
                    binding?.bankNameETETLayout?.isVisible = true
                    binding?.loanRepayMonthlyLayout?.isVisible = true
                }
                R.id.merchantTakeLoanAccountNo -> {
                    hasPreviousLoan = false
                    binding?.merchantLoanAmountETLayout?.isVisible = false
                    binding?.bankNameETETLayout?.isVisible = false
                    binding?.loanRepayMonthlyLayout?.isVisible = false
                }
            }
        }
        //endregion

        binding?.OwnertypeofoownershipInBuisnessRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.InBuisnessRadioButtonOwner -> {
                    selectedOwnerShipOfMarket = "নিজের"
                    isOwnerOfShop = true
                }
                R.id.InBuisnessRadioButtonRental -> {
                    selectedOwnerShipOfMarket = "ভাড়া"
                    isOwnerOfShop = true
                }
                R.id.InBuisnessRadioButtonFamily -> {
                    selectedOwnerShipOfMarket = "পরিবারের নিজস্ব"
                    isOwnerOfShop = true
                }
            }
        }

        binding?.apply {
            haveAnyCreditCardRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.yes_haveAnyCreditCard_radio_button -> {
                        hasCreditCard = true
                        bankLayoutVisibility?.isVisible = true
                        cardLimitLayout?.isVisible = true
                        bankNameLayout?.isVisible = true
                        creditCardName?.isVisible = true
                        creditCardLimit?.isVisible = true
                    }
                    R.id.no_haveAnyCreditCard_radio_button -> {
                        hasCreditCard = false
                        bankLayoutVisibility?.isVisible = false
                        cardLimitLayout?.isVisible = false
                        bankNameLayout?.isVisible = false
                        creditCardName?.isVisible = false
                        creditCardLimit?.isVisible = false
                    }
                }
            }
        }
        binding?.apply {
            haveAnyTIINRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.yes_haveAnyTeam_radio_button -> {
                        hasTin = true
                        TIINNumberLayout.isVisible = true
                        teamTIINNumberET.isVisible = true
                    }
                    R.id.no_haveAnyTeam_radio_button -> {
                        hasTin = false
                        TIINNumberLayout.isVisible = false
                        teamTIINNumberET.isVisible = false
                    }
                }
            }
        }
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
            context?.toast("লিঙ্গ নির্বাচন করুন")
            return false
        }

        merchantName = binding?.merchantNameET?.text.toString()
        if (merchantName.isEmpty()) {
            context?.toast("আপনার নাম লিখুন")
            return false
        }

        if (adapterAge.selectedItem == "") {
            context?.toast("আপনার বয়স নির্বাচন করুন")
            return false
        }
        if (selectedEducation.isEmpty()) {
            context?.toast("শিক্ষাগত যোগ্যতা নির্বাচন করুন")
            return false
        }

        if (familyMemNumAdapter.selectedItem == "") {
            context?.toast("পরিবারের সদস্য সংখ্যা লিখুন")
            return false
        }

        if (houseOwnerAdapter.selectedItem == "") {
            context?.toast("আপনার বাসার মালিকানা তথ্য নির্বাচন করুন")
            return false
        }

        if (locationAdapter.selectedItem == "") {
            context?.toast("আপনার বাসার অবস্থান নির্বাচন করুন")
            return false
        }

        if (marriageStatusAdapter.selectedItem == "") {
            context?.toast("বৈবাহিক অবস্থা নির্বাচন করুন")
            return false
        }

        if (selectedAverageBasket.isEmpty()) {
            context?.toast("আপনার গড় বাস্কেট তথ্য নির্বাচন করুন")
            return false
        }

        if (selectedKnownMerchantDuration.isEmpty()) {
            context?.toast("আপনার মার্চেন্ট এর সাথে পরিচয়ের সময়কাল নির্বাচন করুন")
            return false
        }

        loanRange = binding?.loanRangeET?.text.toString()
        if (loanRange.isEmpty()) {
            context?.toast("আপনার কাঙ্ক্ষিত লোন রেঞ্জ লিখুন")
            return false
        }

        monthlyTransaction = binding?.monthlyTransactionET?.text.toString()
        if (monthlyTransaction.isEmpty()) {
            context?.toast("মাসিক অনলাইন ট্রানজেকশন লিখুন")
            return false
        }

        if (binding?.merchantPhysicalShopExistsRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("ফিজিকাল দোকানের তথ্য দিন")
            return false
        }

        if (hasPhysicalShop) {
            totalMonthlyAverageSell = binding?.totalMonthlyAverageSellET?.text.toString()
            if (totalMonthlyAverageSell.isEmpty()) {
                context?.toast("আপনার গড় মাসিক বিক্রির তথ্য লিখুন")
                return false
            }
        } else {
            totalMonthlyAverageSell = ""
        }

        if (binding?.OwnertypeofoownershipInBuisnessRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("আপনার ব্যবসার মালিকানা নির্বাচন করুন")
            return false
        }


        totalMonthlyCOD = binding?.totalCODFromOtherServicesET?.text.toString()
        if (totalMonthlyCOD.isEmpty()) {
            context?.toast("আপনার টোটাল মাসিক COD দিন")
            return false
        }

        if (dataAdapter.getSelectedItemModelList().isEmpty()) {
            context?.toast("অন্য যে কুরিয়ার সার্ভিস সেবা গ্রহণ করেন তা নির্বাচন করুন")
            return false
        }

        if (binding?.merchantTakeLoanRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("পূর্বের লোনের তথ্য দিন")
            return false
        }

        if (hasPreviousLoan) {
            if (binding?.loanAMountET?.text.isNullOrBlank() || binding?.loanAMountET?.text.toString().toInt() < 1
            ) {
                context?.toast("পূর্বের লোনের পরিমাণ লিখুন")
                return false
            } else {
                previousTakingLoanAmount = binding?.loanAMountET?.text.toString().toInt()
            }
            if (binding?.bankNameET?.text!!.isEmpty()) {
                context?.toast("ব্যাংকের নাম লিখুন")
                return false
            } else {
                bankName = binding?.bankNameET?.text.toString()
            }

            if (binding?.loanRepayRadioGroupType?.checkedRadioButtonId == -1) {
                context?.toast("আপনার লোন পরিশোধের ধরণ নির্বাচন করুন")
                return false
            }

        } else {
            previousTakingLoanAmount = 0
            bankName = ""
        }

        if (binding?.merchantHasBankAccountRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("পূর্বের কোনো ব্যাংক একাউন্ট তথ্য যদি থাকে তবে তা নির্বাচন করুন")
            return false
        }

        if (binding?.haveAnyCreditCardRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("আপনার কার্ডের তথ্য দিন")
            return false
        }

        if (hasCreditCard){
            if(binding?.creditCardName?.text.toString().isEmpty()){
                context?.toast("ব্যাংকের নাম লিখুন")
                return false
            }
            if (binding?.creditCardLimit?.text.toString().isEmpty()){
                context?.toast("ক্রেডিট কার্ডের লিমিট দিন")
                return false
            }
        }

        if (binding?.haveAnyTIINRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("আপনার TIIN তথ্য দিন")
            return false
        }

        if (hasTin){
            if (binding?.teamTIINNumberET?.text.toString().isEmpty()){
                context?.toast("আপনার TIIN নাম্বার দিন")
                return false
            }
        }

        if (binding?.merchantHasTradeLicenceRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("ট্রেড লাইসেন্স এর তথ্য দিন")
            return false
        }

        if (imagePickFlag == 1) {
            if (imageTradeLicencePath.isEmpty()) {
                context?.toast("ট্রেড লাইসেন্স এর ছবি অ্যাড করুন")
                return false
            }
        }

        if (binding?.merchantHasGuarantorRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("গ্যারান্টার এর তথ্য দিন")
                return false
        }

        guarantorName = binding?.merchantGuarantorNameET?.text.toString()
        guarantorNumber = binding?.merchantGuarantorNumberET?.text.toString()
        if (hasGuarantor) {
            if (guarantorName.isEmpty()) {
                context?.toast("গ্যারান্টার এর নাম লিখুন")
                return false
            } else if (guarantorNumber.isEmpty()) {
                context?.toast("গ্যারান্টার এর মোবাইল নাম্বার লিখুন")
                return false
            } else if(guarantorNumber.length != 11 || !isValidMobileNumber(guarantorNumber)){
                context?.toast("গ্যারান্টার এর সঠিক মোবাইল নাম্বার লিখুন")
                return false
            }
        }

        if (selectedAverageOrder.isEmpty()) {
            context?.toast("আপনার গড় অর্ডার এর তথ্য দিন")
            return false
        }

        /*if (selectedMarketPosition.isEmpty()) {
            context?.toast("Please Fill in your market position information")
            return false
        }*/

        if (recommendationAdapter.selectedItem == "") {
            context?.toast("সুপারিশের তথ্য দিন")
            return false
        }
        return true
    }

    private val startImagePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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

    private fun uploadImage(
        fileName: String,
        imagePath: String,
        fileUrl: String,
        requestBody: LoanSurveyRequestBodyV2
    ) {

        val progressDialog = progressDialog()
        progressDialog.show()

        Timber.d("requestBody $fileName, $imagePath, $fileUrl")
        viewModel.imageUploadForFile(requireContext(), fileName, imagePath, fileUrl)
            .observe(viewLifecycleOwner, Observer { model ->
                progressDialog.dismiss()
                if (model) {
                    submitLoanSurveyData(requestBody)
                    context?.toast("Uploaded successfully")

                } else {
                    context?.toast("Uploaded Failed")
                }
            })
    }

    private fun submitLoanSurveyData(requestBody: LoanSurveyRequestBodyV2) {
        val progressDialog = progressDialog()
        progressDialog.show()

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
            progressDialog.dismiss()
            showAlert()
        })
    }

    private fun showAlert() {
        val titleText = "নির্দেশনা"
        val descriptionText = "সার্ভেটি পূরণ করার জন্য ধন্যবাদ।"
        alert(titleText, descriptionText, false, "ঠিক আছে", "না") {
            findNavController().popBackStack()
        }.show()
    }

    private fun warning() {
        val titleText = "নির্দেশনা"
        val descriptionText =
            "আপনি ইতিপূর্বে একবার সার্ভেটি পূরণ করেছেন, আপনি কি আবার পূরণ করতে ইচ্ছুক?"
        alert(titleText, descriptionText, true, "হ্যাঁ", "না") {
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

    private fun ageRecycler() {

        val dataListAge: MutableList<String> = mutableListOf()
        dataListAge.add("১৮-২৫")
        dataListAge.add("২৫-৩০")
        dataListAge.add("৩০-৩৫")
        dataListAge.add("৩৫-৬৫")

        adapterAge.initLoad(dataListAge)
        binding?.ageRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                adapter = adapterAge
                itemAnimator = null
            }
        }

    }


    private fun familyMemNumRecycler() {

        val education: List<String> = listOf(
            "২-৫",
            "৫-৭",
            "৭-১০",
            "১০-১৫"
        )

        familyMemNumAdapter.initLoad(education)
        binding?.FamilyMemNumRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                adapter = familyMemNumAdapter
                itemAnimator = null
            }
        }
    }

    private fun homeLocationRecycler() {

        val location: List<String> = listOf(
            "মহানগর",
            "সিটি কর্পোরেশন",
            "শহরে",
            "গ্রামে"
        )

        locationAdapter.initLoad(location)
        binding?.residenceLocationRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                adapter = locationAdapter
                itemAnimator = null
            }
        }
    }

    private fun marriageStatusRecycler() {

        val location: List<String> = listOf(
            "বিবাহিত",
            "অবিবাহিত",
            "তালাকপ্রাপ্ত",
            "বিধবা/ বিপত্নীক"
        )

        marriageStatusAdapter.initLoad(location)
        binding?.marriageRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
                adapter = marriageStatusAdapter
                itemAnimator = null
            }
        }
    }


    private fun recommendationRecycler() {

        val recommendation: List<String> = listOf(
            "মোটামুটি",
            "ভাল",
            "খুব  ভাল",
            "চমৎকার"
        )

        recommendationAdapter.initLoad(recommendation)
        binding?.recommendationRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                adapter = recommendationAdapter
                itemAnimator = null
            }
        }
    }

    private fun houseOwnerRecycler() {
        val houseOwner: List<String> = listOf(
            "নিজের",
            "পরিবারের নিজস্ব",
            "ভাড়া"
        )
        houseOwnerAdapter.initLoad(houseOwner)
        binding?.houseOwnerRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
                adapter = houseOwnerAdapter
                itemAnimator = null
            }
        }
    }

    private fun setUpEduactionSpinner() {

        val dataListAge: MutableList<String> = mutableListOf(
            "বেছে নিন",
            "প্রাতিষ্ঠানিক শিক্ষা নেই",
            "পি এস সি",
            "জে এস সি",
            "এস এস সি",
            "এইচ এস সি",
            "স্নাতক",
            "স্নাতকোত্তর"
        )

        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, dataListAge)
        binding?.spinnereducationType?.adapter = spinnerAdapter
        binding?.spinnereducationType?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedEducation = if (position > 0) {
                        spinnerAdapter.getItem(position)!!
                    } else{
                        ""
                    }
                }
            }
    }

    private fun setUpAverageBasketSpinner() {

        val AverageBasket: MutableList<String> = mutableListOf(
            "বেছে নিন",
            "১০০০০০-২০০০০০",
            "২০০০০০-৪০০০০০",
            "৪০০০০০-৬০০০০০",
            "৬০০০০০-৮০০০০০",
            "৮০০০০০-১০০০০০০",
            "১০০০০০০-১২০০০০০"
        )
        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, AverageBasket)
        binding?.spinneraverageBasketType?.adapter = spinnerAdapter
        binding?.spinneraverageBasketType?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedAverageBasket = if (position > 0) {
                        spinnerAdapter.getItem(position)!!
                    } else{
                        ""
                    }
                }
            }
    }

    private fun setUpSpinnerKnownToMerchnatSpinner() {

        val knownToMerchant: MutableList<String> = mutableListOf(
            "বেছে নিন",
            "০-১",
            "১-২",
            "২-৪",
            "৪-৬",
            "৬-৮",
            "৮-১০",
            "১০-১৫",
            "১৫-২০"
        )
        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, knownToMerchant)
        binding?.spinneraverKnownToMerchnat?.adapter = spinnerAdapter
        binding?.spinneraverKnownToMerchnat?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedKnownMerchantDuration = if (position > 0) {
                        spinnerAdapter.getItem(position)!!
                    } else{
                        ""
                    }
                }
            }
    }

    private fun setUpSpinnerAverageOrderSpinner() {

        val averageOrder: MutableList<String> = mutableListOf(
            "বেছে নিন",
            "০-৫০০",
            "৫০০-১০০০",
            "১০০০-১৫০০",
            "১৫০০-২০০০",
            "২০০০-৩০০০",
            "৩০০০-৪০০০"
        )
        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, averageOrder)
        binding?.AverageOrderToMerchnat?.adapter = spinnerAdapter
        binding?.AverageOrderToMerchnat?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedAverageOrder = if (position > 0) {
                        spinnerAdapter.getItem(position)!!
                    } else{
                        ""
                    }
                }
            }
    }
    /*
    private fun setUpSpinnerMarketPositionSpinner() {

        val marketPosition: MutableList<String> = mutableListOf(
            "বেছে নিন",
            "ভাল না",
            "মোটামুটি ভাল",
            "ভাল",
            "খুব ভাল",
            "অসাধারণ"
        )
        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, marketPosition)
        binding?.marketPositionSpinner?.adapter = spinnerAdapter
        binding?.marketPositionSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        selectedMarketPosition = spinnerAdapter.getItem(position)!!
                    }
                }
            }
    }
*/


}