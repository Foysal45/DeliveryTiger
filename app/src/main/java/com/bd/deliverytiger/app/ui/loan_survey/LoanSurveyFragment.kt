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
import com.bd.deliverytiger.app.api.model.loan_survey.*
import com.bd.deliverytiger.app.databinding.FragmentLoanSurveyBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.loan_survey.adapters.LocalUniversalAdapter
import com.bd.deliverytiger.app.utils.*
import com.bd.deliverytiger.app.utils.Validator.isValidMobileNumber
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.github.dhaval2404.imagepicker.ImagePicker
import org.koin.android.ext.android.inject
import timber.log.Timber
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.internal.concurrent.formatDuration
import org.koin.android.ext.android.bind
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
    private var loanRepayMonthPeriod = ""
    private var yearlyTotalIncome = ""
    private var otherIncome = ""
    private var nidCardNo = ""
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
    private var hasTradeLicence = false
    private var hasGuarantor = false
    private var hasPreviousLoan = false
    private var isExpireDateofTradeLisenseSelectd = false
    private var isPut: Boolean = false

    private var imagePath: String = ""
    private var imagePickFlag = 0
    private var globalIDFOrLoan = 0
    private var imageTradeLicencePath: String = ""

    private var selectedEducation = ""
    private var selectedAverageBasket = ""
    private var selectedKnownMerchantDuration = ""
    private var selectedAverageOrder = ""
    private var selectedMonthlyExp = ""
    private var selectedCurrentLoanEMI = ""
    private var selectedMarketPosition = ""
    private var selectedOwnerShipOfMarket = ""

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val sdf1 = SimpleDateFormat("dd MMM, yyyy", Locale.US)

    private var selectedDateTradeLisence = "2001-01-01"
    private var selectedDateDOB = "2001-01-01"
    private var selectedDateFormattedTradeLisence = ""
    private var selectedDateFormattedDOB = ""
    private var tradeLicenseImageUrl = ""
    private var applicationDate = ""


    private val adapterAge = LocalUniversalAdapter()
    private val familyMemNumAdapter = LocalUniversalAdapter()
    private val locationAdapter = LocalUniversalAdapter()
    private val marriageStatusAdapter = LocalUniversalAdapter()
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
        initData()
        //fetchCourierList()
        initClickListener()
        //initViews()
    }

    private fun initViews() {
        ageRecycler()
        setUpEduactionSpinner()
        familyMemNumRecycler()
        homeLocationRecycler()
        marriageStatusRecycler()
        setUpAverageBasketSpinner()
        setUpSpinnerKnownToMerchnatSpinner()
        houseOwnerRecycler()
        setUpSpinnerAverageOrderSpinner()
        setUpSpinnerMonthlyExpSpinner()
        setUpSpinnerCurrentLoanEMISpinner()
        fetchCourierList()
    }

    private fun initData() {
        viewModel.previousLoanSurveyResponse(SessionManager.courierUserId)
            .observe(viewLifecycleOwner, Observer {
                if (it.isEmpty()) {
                    isPut = false
                    initViews()
                } else {
                    isPut = true
                    globalIDFOrLoan = it[0].loanSurveyId
                    applicationDate = it[0].applicationDate
                    binding?.merchantGenderRadioGroup?.check(
                        if (it[0].gender == "male")
                            R.id.merchantGenderMale
                        else
                            R.id.merchantGenderFemale
                    )
                    if (!it[0].merchantName.isNullOrEmpty()) {
                        merchantName = it[0].merchantName
                        binding?.merchantNameET?.setText(it[0].merchantName)
                    }
                    if (!it[0].age.isNullOrEmpty()) {
                        ageRecycler(it[0].age, true)
                    } else {
                        ageRecycler(it[0].age, false)
                    }
                    setDateRangePickerTitleTradeLisencee(0, true, it[0].tradeLicenseExpireDate)
                    setDateRangePickerTitleDOB(0, true, it[0].dateOfBirth)
                    if (!it[0].nidNo.isNullOrEmpty()) {
                        binding?.nidCardNoET?.setText(it[0].nidNo)
                    }
                    binding?.DOBET?.setText(
                        DigitConverter.formatDate(
                            it[0].dateOfBirth,
                            "yyyy-MM-dd",
                            "dd MMM, yyyy"
                        )
                    )
                    if (!it[0].eduLevel.isNullOrEmpty()) {
                        setUpEduactionSpinner(it[0].eduLevel, true)
                    } else {
                        setUpEduactionSpinner(it[0].eduLevel, false)
                    }
                    if (!it[0].loanEmi.isNullOrEmpty()) {
                        setUpSpinnerCurrentLoanEMISpinner(it[0].loanEmi, true)
                    } else {
                        setUpSpinnerCurrentLoanEMISpinner(it[0].loanEmi, false)
                    }
                    if (!it[0].monthlyOrder.isNullOrEmpty()) {
                        setUpSpinnerAverageOrderSpinner(it[0].monthlyOrder, true)
                    } else {
                        setUpSpinnerAverageOrderSpinner(it[0].monthlyOrder, false)
                    }
                    if (!it[0].famMem.isNullOrEmpty()) {
                        familyMemNumRecycler(it[0].famMem, true)
                    } else {
                        familyMemNumRecycler(it[0].famMem, false)
                    }
                    if (!it[0].homeOwnership.isNullOrEmpty()) {
                        houseOwnerRecycler(it[0].homeOwnership, true)
                    } else {
                        houseOwnerRecycler(it[0].homeOwnership, false)
                    }
                    if (!it[0].residenceLocation.isNullOrEmpty()) {
                        homeLocationRecycler(it[0].residenceLocation, true)
                    } else {
                        homeLocationRecycler(it[0].residenceLocation, false)
                    }
                    if (!it[0].married.isNullOrEmpty()) {
                        marriageStatusRecycler(it[0].married, true)
                    } else {
                        marriageStatusRecycler(it[0].married, false)
                    }
                    if (!it[0].basketValue.isNullOrEmpty()) {
                        setUpAverageBasketSpinner(it[0].basketValue, true)
                    } else {
                        setUpAverageBasketSpinner(it[0].basketValue, false)
                    }
                    if (!it[0].monthlyExp.isNullOrEmpty()) {
                        setUpSpinnerMonthlyExpSpinner(it[0].monthlyExp, true)
                    } else {
                        setUpSpinnerMonthlyExpSpinner(it[0].monthlyExp, false)
                    }
                    if (!it[0].relationMarchent.isNullOrEmpty()) {
                        setUpSpinnerKnownToMerchnatSpinner(it[0].relationMarchent, true)
                    } else {
                        setUpSpinnerKnownToMerchnatSpinner(it[0].relationMarchent, false)
                    }

                    binding?.merchantPhysicalShopExistsRadioGroup?.check(
                        if (it[0].isLocalShop)
                            R.id.merchantPhysicalShopExistsYes
                        else
                            R.id.merchantPhysicalShopExistsNo
                    )
                    binding?.totalMonthlyAverageSellET?.setText(
                        if (it[0].monthlyTotalAverageSale.toInt().toString() == "0") {
                            ""
                        } else {
                            it[0].monthlyTotalAverageSale.toInt().toString()
                        }
                    )
                    binding?.OwnertypeofoownershipInBuisnessRadioGroup?.check(
                        when (it[0].shopOwnership) {
                            "নিজের" -> {
                                R.id.InBuisnessRadioButtonOwner
                            }
                            "ভাড়া" -> {
                                R.id.InBuisnessRadioButtonRental
                            }
                            "পরিবারের নিজস্ব" -> {
                                R.id.InBuisnessRadioButtonFamily
                            }
                            else -> {
                                R.id.InBuisnessRadioButtonOwner
                            }
                        }
                    )
                    binding?.totalCODFromOtherServicesET?.setText(
                        it[0].monthlyTotalCodAmount.toInt().toString()
                    )
                    binding?.merchantTakeLoanRadioGroup?.check(
                        if (it[0].hasPreviousLoan) {
                            R.id.merchantTakeLoanAccountYes
                        } else {
                            R.id.merchantTakeLoanAccountNo
                        }
                    )
                    binding?.loanAMountET?.setText(
                        if (it[0].loanAmount.toInt().toString() == 0.toString()) {
                            ""
                        } else {
                            it[0].loanAmount.toInt().toString()
                        }
                    )
                    /*binding?.loanAMountET?.setText(it[0].loanAmount.toInt().toString())*/
                    if (!it[0].bankName.isNullOrEmpty()) {
                        binding?.bankNameET?.setText(it[0].bankName)
                    }
                    binding?.loanRepayRadioGroupType?.check(
                        if (it[0].repayType == "weekly") {
                            R.id.loanRepayWeekly
                        } else {
                            R.id.loanRepayMonthly
                        }
                    )
                    binding?.merchantHasBankAccountRadioGroup?.check(
                        if (it[0].isBankAccount) {
                            R.id.merchantHasBankAccountYes
                        } else {
                            R.id.merchantHasBankAccountNo
                        }
                    )
                    if (!it[0].companyBankAccName.isNullOrEmpty()) {
                        binding?.conmapyBankNameTextInput?.setText(it[0].companyBankAccName)
                    }
                    if (!it[0].companyBankAccNo.isNullOrEmpty()) {
                        binding?.bankAccountNumberET?.setText(it[0].companyBankAccNo)
                    }
                    binding?.haveAnyCreditCardRadioGroup?.check(
                        if (it[0].hasCreditCard) {
                            R.id.yes_haveAnyCreditCard_radio_button
                        } else {
                            R.id.no_haveAnyCreditCard_radio_button
                        }
                    )
                    if (!it[0].cardHolder.isNullOrEmpty()) {
                        binding?.creditCardName?.setText(it[0].cardHolder)
                    }
                    if (!it[0].cardLimit.isNullOrEmpty()) {
                        binding?.creditCardLimit?.setText(it[0].cardLimit)
                    }
                    binding?.haveAnyTINRadioGroup?.check(
                        if (it[0].hasTin) {
                            R.id.yes_haveAnyTin_radio_button
                        } else {
                            R.id.no_haveAnyTin_radio_button
                        }
                    )
                    if (!it[0].tinNumber.isNullOrEmpty()) {
                        binding?.teamTINNumberET?.setText(it[0].tinNumber)
                    }
                    binding?.merchantHasTradeLicenceRadioGroup?.check(
                        if (it[0].hasTradeLicense) {
                            R.id.merchantHasTradeLicenceYes
                        } else {
                            R.id.merchantHasTradeLicenceNo
                        }
                    )
                    if (!it[0].tradeLicenseNo.isNullOrEmpty()) {
                        binding?.tradeliesenceNOTV?.setText(it[0].tradeLicenseNo)
                    }
                    if (!it[0].tradeLicenseExpireDate.isNullOrEmpty()) {
                        binding?.tradeliesencExpireDateTV?.setText(
                            DigitConverter.formatDate(
                                it[0].tradeLicenseExpireDate,
                                "yyyy-MM-dd",
                                "dd MMM yyyy"
                            )
                        )
                    }

                    if (!it[0].tradeLicenseImageUrl.isNullOrEmpty()) {
                        binding?.imageTradeLicencePickedIV?.isVisible = true
                        binding?.imageTradeLicencePickedIV?.let { image ->
                            Glide.with(image)
                                .load(it[0].tradeLicenseImageUrl)
                                .apply(RequestOptions().placeholder(R.drawable.ic_banner_place))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(image)
                        }
                        tradeLicenseImageUrl = it[0].tradeLicenseImageUrl
                    }
                    binding?.merchantHasGuarantorRadioGroup?.check(
                        if (!it[0].guarantorMobile.isNullOrEmpty() && !it[0].guarantorName.isNullOrEmpty()) {
                            R.id.merchantHasGuarantorYes
                        } else {
                            R.id.merchantHasGuarantorNo
                        }
                    )
                    binding?.merchantGuarantorNameET?.setText(it[0].guarantorName)
                    binding?.merchantGuarantorNumberET?.setText(it[0].guarantorMobile)
                    binding?.loanRangeET?.setText(it[0].interestedAmount.toInt().toString())
                    binding?.reqTenorMonthET?.setText(it[0].reqTenorMonth.toInt().toString())
                    binding?.yearlyTotalIncomehET?.setText(
                        it[0].annualTotalIncome.toInt().toString()
                    )
                    binding?.otherIncomeET?.setText(it[0].othersIncome.toInt().toString())
                    binding?.monthlyTransactionET?.setText(
                        it[0].monthlyTotalCodAmount.toInt().toString()
                    )
                    courierList.clear()

                    viewModel.fetchCourierList().observe(viewLifecycleOwner, Observer { list ->
                        if (!list.isNullOrEmpty()) {
                            for (index in 0 until list.size) {
                                it[0].courierWithLoanSurvey.forEach { courrersofcurrnetUser ->
                                    if (courrersofcurrnetUser.courierId == list[index].courierId
                                    ) {
                                        dataAdapter.multipleSelection(list[index], index)
                                    }
                                }
                            }
                            courierList.addAll(list)
                            dataAdapter.initLoad(courierList)
                        }
                    })
                }
            })
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
            dataAdapter.multipleSelection(model, position)
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        binding?.applyLoanBtn?.setOnClickListener {
            if (verify()) {
                var requestBody2 = LoanSurveyRequestBody(
                    age = adapterAge.selectedItem,
                    annualTotalIncome = yearlyTotalIncome.toDouble().toInt() ?: 0,
                    bankName = binding?.bankNameET?.text.toString(),
                    basketValue = selectedAverageBasket,
                    cardHolder = binding?.creditCardName?.text.toString(),
                    cardLimit = binding?.creditCardLimit?.text.toString(),
                    companyBankAccName = binding?.conmapyBankNameTextInput?.text.toString().trim(),
                    companyBankAccNo = binding?.bankAccountNumberET?.text.toString().trim(),
                    courierUserId = SessionManager.courierUserId,
                    dateOfBirth = selectedDateDOB,
                    eduLevel = selectedEducation,
                    famMem = familyMemNumAdapter.selectedItem,
                    gender = merchantGender,
                    guarantorMobile = guarantorNumber,
                    guarantorName = guarantorName,
                    hasCreditCard = binding?.haveAnyCreditCardRadioGroup?.checkedRadioButtonId == R.id.yes_haveAnyCreditCard_radio_button,
                    hasTin = binding?.haveAnyTINRadioGroup?.checkedRadioButtonId == R.id.yes_haveAnyTin_radio_button,
                    hasTradeLicense = binding?.merchantHasTradeLicenceRadioGroup?.checkedRadioButtonId == R.id.merchantHasTradeLicenceYes,
                    homeOwnership = houseOwnerAdapter.selectedItem,
                    interestedAmount = loanRange.toDouble().toInt(),
                    isBankAccount = hasBankAccount,
                    isLocalShop = hasPhysicalShop,
                    loanAmount = if (hasPreviousLoan) previousTakingLoanAmount else 0,
                    loanEmi = selectedCurrentLoanEMI,
                    married = marriageStatusAdapter.selectedItem,
                    merchantName = merchantName,
                    monthlyExp = selectedMonthlyExp,
                    monthlyOrder = selectedAverageOrder,
                    monthlyTotalAverageSale = if (hasPhysicalShop) totalMonthlyAverageSell.toDouble()
                        .toInt() else 0,
                    monthlyTotalCodAmount = totalMonthlyCOD.toDouble().toInt(),
                    nidNo = nidCardNo,
                    othersIncome = binding?.otherIncomeET?.text.toString().trim().toDouble().toInt()
                        ?: 0,
                    recommend = "",
                    relationMarchent = selectedKnownMerchantDuration,
                    repayType = if (binding?.loanRepayRadioGroupType?.checkedRadioButtonId == R.id.loanRepayWeekly) "weekly" else "monthly",
                    reqTenorMonth = loanRepayMonthPeriod.toInt() ?: 0,
                    residenceLocation = locationAdapter.selectedItem,
                    shopOwnership = selectedOwnerShipOfMarket,
                    tinNumber = binding?.teamTINNumberET?.text.toString().trim(),
                    tradeLicenseExpireDate = selectedDateTradeLisence,
                    tradeLicenseImageUrl = tradeLicenseImageUrl,
                    tradeLicenseNo = binding?.tradeliesenceNOTV?.text.toString().trim(),
                    transactionAmount = monthlyTransaction.toDouble().toInt() ?: 0,
                    hasPreviousLoan = hasPreviousLoan,
                    loanSurveyId = globalIDFOrLoan,
                    applicationDate = if (applicationDate == "") sdf.format(Calendar.getInstance().time) else applicationDate
                )
                if (imagePickFlag == 1) {
                    requestBody2.apply {
                        tradeLicenseImageUrl =
                            "https://static.ajkerdeal.com/delivery_tiger/trade_license/trade_${SessionManager.courierUserId}.jpg"
                    }
                    if (imageTradeLicencePath != "") {
                        uploadImage(
                            "trade_${SessionManager.courierUserId}.jpg",
                            "delivery_tiger/trade_license",
                            imageTradeLicencePath, requestBody2
                        )
                    } else {
                        submitLoanSurveyData(requestBody2)
                    }
                } else {
                    submitLoanSurveyData(requestBody2)
                }
            }
        }
        binding?.tradeliesencExpireDateTV?.setOnClickListener {
            datePickerTradeLisence()
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
                    binding?.hasBankAccountLayout?.visibility = View.VISIBLE
                    binding?.companyBankNameLayout?.visibility = View.VISIBLE
                    binding?.conmapyBankNameTextInput?.visibility = View.VISIBLE
                    binding?.bankAccountNumberLayout?.visibility = View.VISIBLE
                    binding?.bankAccountNumberET?.visibility = View.VISIBLE
                }
                R.id.merchantHasBankAccountNo -> {
                    hasBankAccount = false
                    binding?.hasBankAccountLayout?.visibility = View.GONE
                    binding?.bankAccountNumberLayout?.visibility = View.GONE
                    binding?.bankAccountNumberET?.visibility = View.GONE
                    binding?.companyBankNameLayout?.visibility = View.GONE
                    binding?.conmapyBankNameTextInput?.visibility = View.GONE
                }
            }
        }

        binding?.DOBET?.setOnClickListener {
            datePickerDOB()
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
                    binding?.tradeliesencenoandexpiredateLayout?.visibility = View.VISIBLE
                    binding?.tradeliesenceNOLayout?.visibility = View.VISIBLE
                    binding?.tradeliesenceNOTV?.visibility = View.VISIBLE
                    binding?.tradeliesencExpireDateLayout?.visibility = View.VISIBLE
                    binding?.tradeliesencExpireDateTV?.visibility = View.VISIBLE
                }
                R.id.merchantHasTradeLicenceNo -> {
                    hasTradeLicence = false
                    imagePickFlag = 0
                    binding?.merchantTradeLicenceLayout?.isVisible = false
                    binding?.tradeliesencenoandexpiredateLayout?.visibility = View.GONE
                    binding?.tradeliesenceNOLayout?.visibility = View.GONE
                    binding?.tradeliesenceNOTV?.visibility = View.GONE
                    binding?.tradeliesencExpireDateLayout?.visibility = View.GONE
                    binding?.tradeliesencExpireDateTV?.visibility = View.GONE

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
                    binding?.spinnerCurrentLoanEMILayout?.isVisible = true
                    binding?.spinnerCurrentLoanEMIType?.isVisible = true
                    binding?.spinnerCurrentLoanEMIText?.isVisible = true
                }
                R.id.merchantTakeLoanAccountNo -> {
                    hasPreviousLoan = false
                    binding?.merchantLoanAmountETLayout?.isVisible = false
                    binding?.bankNameETETLayout?.isVisible = false
                    binding?.loanRepayMonthlyLayout?.isVisible = false
                    binding?.spinnerCurrentLoanEMILayout?.isVisible = false
                    binding?.spinnerCurrentLoanEMIType?.isVisible = false
                    binding?.spinnerCurrentLoanEMIText?.isVisible = false
                }
            }
        }
        //endregion

        binding?.OwnertypeofoownershipInBuisnessRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.InBuisnessRadioButtonOwner -> {
                    selectedOwnerShipOfMarket = "নিজের"
                }
                R.id.InBuisnessRadioButtonRental -> {
                    selectedOwnerShipOfMarket = "ভাড়া"
                }
                R.id.InBuisnessRadioButtonFamily -> {
                    selectedOwnerShipOfMarket = "পরিবারের নিজস্ব"
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
            haveAnyTINRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.yes_haveAnyTin_radio_button -> {
                        hasTin = true
                        TIINNumberLayout.isVisible = true
                        teamTINNumberET.isVisible = true
                    }
                    R.id.no_haveAnyTin_radio_button -> {
                        hasTin = false
                        TIINNumberLayout.isVisible = false
                        teamTINNumberET.isVisible = false
                    }
                }
            }
        }
    }

    private fun fetchCourierList() {
        courierList.clear()

        viewModel.fetchCourierList().observe(viewLifecycleOwner, Observer { list ->
            if (!list.isNullOrEmpty()) {
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
        requestBody: LoanSurveyRequestBody
    ) {
        val progressDialog = progressDialog()
        progressDialog.show()
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

    private fun submitLoanSurveyData(requestBody: LoanSurveyRequestBody) {
        val progressDialog = progressDialog()
        progressDialog.show()
        progressDialog.setCanceledOnTouchOutside(false)
        if (isPut) {
            viewModel.updateLoanSurvey(requestBody, SessionManager.accessToken)
                .observe(viewLifecycleOwner, { model ->
                    SessionManager.isSurveyComplete = true
                    val tempLoanSurveyId = globalIDFOrLoan
                    selectedCourierList.clear()
                    for (item in dataAdapter.getSelectedItemModelList()) {
                        item.apply {
                            loanSurveyId = tempLoanSurveyId
                        }
                        selectedCourierList.add(item)
                    }
                    viewModel.updateCourierWithLoanSurvey(
                        selectedCourierList,
                        tempLoanSurveyId,
                        SessionManager.accessToken
                    )
                        .observe(viewLifecycleOwner, {})
                    progressDialog.dismiss()
                    findNavController().popBackStack()
                    showAlert()
                })
        } else {
            viewModel.submitLoanSurvey(requestBody).observe(viewLifecycleOwner, Observer { model ->
                SessionManager.isSurveyComplete = true
                val tempLoanSurveyId =
                    if (globalIDFOrLoan == 0) model.loanSurveyId else globalIDFOrLoan
                selectedCourierList.clear()
                for (item in dataAdapter.getSelectedItemModelList()) {
                    item.apply {
                        loanSurveyId = tempLoanSurveyId
                    }
                    selectedCourierList.add(item)
                }
                viewModel.submitCourierList(selectedCourierList)
                progressDialog.dismiss()
                findNavController().popBackStack()
                showAlert()
            })
        }
    }

    private fun showAlert() {
        val titleText = "নির্দেশনা"
        val descriptionText = "সার্ভেটি পূরণ করার জন্য ধন্যবাদ।"
        alert(titleText, descriptionText, false, "ঠিক আছে", "না").show()
    }

    private fun warning() {
        val titleText = "নির্দেশনা"
        val descriptionText =
            "আপনি ইতিপূর্বে একবার সার্ভেটি পূরণ করেছেন, আপনি কি আবার পূরণ করতে ইচ্ছুক?"
        alert(titleText, descriptionText, false, "হ্যাঁ") {
            if (it == AlertDialog.BUTTON_POSITIVE) {
//                findNavController().popBackStack()
            }
        }.apply {
            setCanceledOnTouchOutside(false)
            show()
        }
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

    private fun ageRecycler(preselectedItem: String = "", hasPreviousSelection: Boolean = false) {

        val dataListAge: MutableList<String> = mutableListOf()
        dataListAge.add("১৮-২৫")
        dataListAge.add("২৫-৩০")
        dataListAge.add("৩০-৩৫")
        dataListAge.add("৩৫-৬৫")
        val indexOfselectedItem = dataListAge.indexOf(preselectedItem)
        adapterAge.initLoad(dataListAge, indexOfselectedItem, hasPreviousSelection)
        binding?.ageRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                adapter = adapterAge
                itemAnimator = null
            }
        }
    }


    private fun familyMemNumRecycler(
        preselectedItem: String = "",
        hasPreviousSelection: Boolean = false
    ) {

        val education: List<String> = listOf("২-৫", "৫-৭", "৭-১০", "১০-১৫")
        val indexOfselectedItem = education.indexOf(preselectedItem)
        familyMemNumAdapter.initLoad(education, indexOfselectedItem, hasPreviousSelection)
        binding?.FamilyMemNumRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                adapter = familyMemNumAdapter
                itemAnimator = null
            }
        }
    }

    private fun homeLocationRecycler(
        preselectedItem: String = "",
        hasPreviousSelection: Boolean = false
    ) {

        val location: List<String> = listOf("মহানগর", "সিটি কর্পোরেশন", "শহরে", "গ্রামে")
        val indexOfselectedItem = location.indexOf(preselectedItem)

        locationAdapter.initLoad(location, indexOfselectedItem, hasPreviousSelection)
        binding?.residenceLocationRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                adapter = locationAdapter
                itemAnimator = null
            }
        }
    }

    private fun marriageStatusRecycler(
        preselectedItem: String = "",
        hasPreviousSelection: Boolean = false
    ) {

        val location: List<String> =
            listOf("বিবাহিত", "অবিবাহিত", "তালাকপ্রাপ্ত", "বিধবা/ বিপত্নীক")
        val indexOfselectedItem = location.indexOf(preselectedItem)
        marriageStatusAdapter.initLoad(location, indexOfselectedItem, hasPreviousSelection)
        binding?.marriageRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
                adapter = marriageStatusAdapter
                itemAnimator = null
            }
        }
    }

    private fun houseOwnerRecycler(
        preselectedItem: String = "",
        hasPreviousSelection: Boolean = false
    ) {
        val houseOwner: List<String> = listOf("নিজের", "পরিবারের নিজস্ব", "ভাড়া")
        val indexOfselectedItem = houseOwner.indexOf(preselectedItem)

        houseOwnerAdapter.initLoad(houseOwner, indexOfselectedItem, hasPreviousSelection)
        binding?.houseOwnerRecyclerView?.let { recyclerView ->
            recyclerView.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
                adapter = houseOwnerAdapter
                itemAnimator = null
            }
        }
    }

    private fun setUpEduactionSpinner(
        preselectedItem: String = "",
        hasPreviousSelection: Boolean = false
    ) {

        val dataListAge: MutableList<String> = mutableListOf(
            "বেছে নিন", "প্রাতিষ্ঠানিক শিক্ষা নেই", "পি এস সি", "জে এস সি", "এস এস সি",
            "এইচ এস সি", "স্নাতক", "স্নাতকোত্তর"
        )
        val indexOfselectedItem = dataListAge.indexOf(preselectedItem)

        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, dataListAge)

        binding?.spinnereducationType?.adapter = spinnerAdapter
        if (indexOfselectedItem > 0) {
            binding?.spinnereducationType?.setSelection(indexOfselectedItem)
        }
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
                    } else {
                        ""
                    }
                }
            }
    }

    private fun setUpAverageBasketSpinner(
        preselectedItem: String = "",
        hasPreviousSelection: Boolean = false
    ) {

        val AverageBasket: MutableList<String> = mutableListOf(
            "বেছে নিন", "১০০০০০-২০০০০০",
            "২০০০০০-৪০০০০০", "৪০০০০০-৬০০০০০", "৬০০০০০-৮০০০০০", "৮০০০০০-১০০০০০০", "১০০০০০০-১২০০০০০"
        )
        val indexOfselectedItem = AverageBasket.indexOf(preselectedItem)
        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, AverageBasket)
        binding?.spinneraverageBasketType?.adapter = spinnerAdapter
        binding?.spinneraverageBasketType?.setSelection(indexOfselectedItem)
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
                    } else {
                        ""
                    }
                }
            }
    }

    private fun setUpSpinnerKnownToMerchnatSpinner(
        preselectedItem: String = "",
        hasPreviousSelection: Boolean = false
    ) {

        val knownToMerchant: MutableList<String> = mutableListOf(
            "বেছে নিন", "০-১", "১-২", "২-৪", "৪-৬", "৬-৮", "৮-১০", "১০-১৫", "১৫-২০"
        )
        val indexOfselectedItem = knownToMerchant.indexOf(preselectedItem)
        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, knownToMerchant)
        binding?.spinneraverKnownToMerchnat?.adapter = spinnerAdapter
        binding?.spinneraverKnownToMerchnat?.setSelection(indexOfselectedItem)
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
                    } else {
                        ""
                    }
                }
            }
    }

    private fun setUpSpinnerAverageOrderSpinner(
        preselectedItem: String = "",
        hasPreviousSelection: Boolean = false
    ) {

        val averageOrder: MutableList<String> = mutableListOf(
            "বেছে নিন", "০-৫০০", "৫০০-১০০০", "১০০০-১৫০০", "১৫০০-২০০০", "২০০০-৩০০০", "৩০০০-৪০০০"
        )
        val indexOfselectedItem = averageOrder.indexOf(preselectedItem)
        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, averageOrder)
        binding?.AverageOrderToMerchnat?.adapter = spinnerAdapter
        binding?.AverageOrderToMerchnat?.setSelection(indexOfselectedItem)
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
                    } else {
                        ""
                    }
                }
            }
    }

    private fun setUpSpinnerMonthlyExpSpinner(
        preselectedItem: String = "",
        hasPreviousSelection: Boolean = false
    ) {

        val MonthlyExp: MutableList<String> = mutableListOf(
            "বেছে নিন", "০-৫,০০০", "৫,০০০-১০,০০০", "১০,০০০-১৫,০০০", "১৫,০০০-২০,০০০",
            "২০,০০০-২৫,০০০", "২৫,০০০-৩০,০০০", "৩০,০০০-৪০,০০০", "৪০,০০০-৬০,০০০", "৬০,০০০-১,০০,০০০"
        )

        val indexOfselectedItem = MonthlyExp.indexOf(preselectedItem)
        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, MonthlyExp)
        binding?.spinnerMonthlyExpType?.adapter = spinnerAdapter
        binding?.spinnerMonthlyExpType?.setSelection(indexOfselectedItem)
        binding?.spinnerMonthlyExpType?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedMonthlyExp = if (position > 0) {
                        spinnerAdapter.getItem(position)!!
                    } else {
                        ""
                    }
                }
            }
    }

    private fun setUpSpinnerCurrentLoanEMISpinner(
        preselectedItem: String = "",
        hasPreviousSelection: Boolean = false
    ) {

        val CurrentLoanEMI: MutableList<String> = mutableListOf(
            "বেছে নিন", "৫০০-১০০০", "১০০০-২০০০", "২০০০-৪০০০", "৪০০০-৬০০০", "৬০০০-৮০০০",
            "৮০০০-১০০০০", "১০০০০-১২০০০", "১২০০০-১৫০০০", "১৫০০০-২০০০০"
        )
        val indexOfselectedItem = CurrentLoanEMI.indexOf(preselectedItem)
        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, CurrentLoanEMI)
        binding?.spinnerCurrentLoanEMIType?.adapter = spinnerAdapter
        binding?.spinnerCurrentLoanEMIType?.setSelection(indexOfselectedItem)
        binding?.spinnerCurrentLoanEMIType?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCurrentLoanEMI = if (position > 0) {
                        spinnerAdapter.getItem(position)!!
                    } else {
                        ""
                    }
                }
            }
    }


    private fun datePickerTradeLisence() {
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTheme(R.style.CustomMaterialCalendarTheme)
        builder.setTitleText("Select trade license ")
        val picker = builder.build()
        picker.show(childFragmentManager, "Picker")
        picker.addOnPositiveButtonClickListener {
            setDateRangePickerTitleTradeLisencee(it)
        }
    }

    private fun setDateRangePickerTitleTradeLisencee(
        selectedDOB: Long,
        fromInit: Boolean = false,
        serverDate: String = ""
    ) {
        if (fromInit) {
            selectedDateTradeLisence =
                DigitConverter.formatDate(serverDate, "yyyy-MM-dd", "yyyy-MM-dd")
            selectedDateFormattedTradeLisence =
                DigitConverter.formatDate(serverDate, "yyyy-MM-dd", "dd MMM yyyy")
            binding?.tradeliesencExpireDateTV?.setText(selectedDateFormattedTradeLisence)
            isExpireDateofTradeLisenseSelectd = true
        } else {
            selectedDateTradeLisence = sdf.format(selectedDOB)
            selectedDateFormattedTradeLisence = sdf1.format(selectedDOB)
            binding?.tradeliesencExpireDateTV?.setText(selectedDateFormattedTradeLisence)
            isExpireDateofTradeLisenseSelectd = true
        }
    }

    private fun datePickerDOB() {
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTheme(R.style.CustomMaterialCalendarTheme)
        builder.setTitleText("Select your birthday")
        val picker = builder.build()
        picker.show(childFragmentManager, "Picker")
        picker.addOnPositiveButtonClickListener {
            setDateRangePickerTitleDOB(it)
        }
    }

    private fun setDateRangePickerTitleDOB(
        selectedDOB: Long,
        fromInit: Boolean = false,
        serverDate: String = ""
    ) {
        if (fromInit) {
            selectedDateDOB = DigitConverter.formatDate(serverDate, "yyyy-MM-dd", "yyyy-MM-dd")
            selectedDateFormattedDOB =
                DigitConverter.formatDate(serverDate, "yyyy-MM-dd", "dd MMM yyyy")
            binding?.DOBET?.setText(selectedDateFormattedDOB)
        } else {
            selectedDateDOB = sdf.format(selectedDOB)
            selectedDateFormattedDOB = sdf1.format(selectedDOB)
            binding?.DOBET?.setText(selectedDateFormattedDOB)
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

        nidCardNo = binding?.nidCardNoET?.text.toString()
        if (nidCardNo.isEmpty()) {
            context?.toast("আপনার এনআইডি নাম্বার উল্লেখ করুন")
            return false
        }

        if (binding?.DOBET?.text.toString().isEmpty()) {
            context?.toast("আপনার আপনার জন্মতারিখ উল্লেখ করুন")
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

        if (selectedMonthlyExp.isEmpty()) {
            context?.toast("আপনার মাসিক ব্যায়ের তথ্য দিন")
            return false
        }

        if (selectedKnownMerchantDuration.isEmpty()) {
            context?.toast("আপনার মার্চেন্ট এর সাথে পরিচয়ের সময়কাল নির্বাচন করুন")
            return false
        }

        loanRange = binding?.loanRangeET?.text.toString() ?: ""
        if (loanRange.isEmpty()) {
            context?.toast("আপনার কাঙ্ক্ষিত লোন রেঞ্জ লিখুন")
            return false
        }

        loanRepayMonthPeriod = binding?.reqTenorMonthET?.text.toString() ?: ""
        if (loanRepayMonthPeriod.isEmpty()) {
            context?.toast("আপনি কত মাসের মধ্যে লোন পরিশোধ করতে ইচ্ছুক")
            return false
        } else if (loanRepayMonthPeriod.toDouble().toInt() ?: 0 >= 60) {
            context?.toast("লোন পরিশোধের সময় ৬০ মাসের মধ্যে হয়া বাধ্যতামূলক")
            return false
        }

        yearlyTotalIncome = binding?.yearlyTotalIncomehET?.text.toString()
        if (yearlyTotalIncome.isNullOrEmpty()) {
            context?.toast("আপনার বাৎসরিক সর্বমোট আয় উল্লেখ করুন")
            return false
        }
        otherIncome = binding?.otherIncomeET?.text.toString() ?: ""
        if (otherIncome.isEmpty()) {
            context?.toast("অন্যান্য উৎস থেকে সর্বমোট আয় উল্লেখ করুন")
            return false
        }

        monthlyTransaction = binding?.monthlyTransactionET?.text.toString() ?: ""
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


        totalMonthlyCOD = binding?.totalCODFromOtherServicesET?.text.toString() ?: ""
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
            if (binding?.loanAMountET?.text.isNullOrBlank() || binding?.loanAMountET?.text.toString()
                    .toDouble().toInt() ?: 0 < 1
            ) {
                context?.toast("পূর্বের লোনের পরিমাণ লিখুন")
                return false
            } else {
                previousTakingLoanAmount =
                    binding?.loanAMountET?.text.toString().toDouble().toInt() ?: 0
            }
            if (binding?.bankNameET?.text!!.isEmpty()) {
                context?.toast("ব্যাংকের নাম লিখুন")
                return false
            } else {
                bankName = binding?.bankNameET?.text.toString() ?: ""
            }

            if (binding?.loanRepayRadioGroupType?.checkedRadioButtonId == -1) {
                context?.toast("আপনার লোন পরিশোধের ধরণ নির্বাচন করুন")
                return false
            }
            if (selectedCurrentLoanEMI.isEmpty()) {
                context?.toast("আপনার লোন ই এম আই এর পরিমান দিন")
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
        if (binding?.merchantHasBankAccountRadioGroup?.checkedRadioButtonId == R.id.merchantHasBankAccountYes
            && (binding?.bankAccountNumberET?.text.toString()
                .isEmpty() || binding?.conmapyBankNameTextInput?.text.toString().isEmpty())
        ) {
            context?.toast("পূর্বের কোনো ব্যাংক একাউন্ট তথ্য যদি থাকে তবে তা নির্বাচন করুন")
            return false
        }

        if (binding?.haveAnyCreditCardRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("আপনার কার্ডের তথ্য দিন")
            return false
        }

        if (hasCreditCard) {
            if (binding?.creditCardName?.text.toString().isEmpty()) {
                context?.toast("ব্যাংকের নাম লিখুন")
                return false
            }
            if (binding?.creditCardLimit?.text.toString().isEmpty()) {
                context?.toast("ক্রেডিট কার্ডের লিমিট দিন")
                return false
            }
        }

        if (binding?.haveAnyTINRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("আপনার TIN তথ্য দিন")
            return false
        }

        if (hasTin) {
            if (binding?.teamTINNumberET?.text.toString().isEmpty()) {
                context?.toast("আপনার TIN নাম্বার দিন")
                return false
            }
            if (binding?.teamTINNumberET?.text.toString().trim().length < 12) {
                context?.toast("ট্টিন নাম্বার ১১ ডিজিট হওয়া বাধ্যতামূলক")
                return false
            }
        }

        if (binding?.merchantHasTradeLicenceRadioGroup?.checkedRadioButtonId == -1) {
            context?.toast("ট্রেড লাইসেন্স এর তথ্য দিন")
            return false
        }

        if (binding?.merchantHasTradeLicenceRadioGroup?.checkedRadioButtonId == R.id.merchantHasTradeLicenceYes
            && (binding?.tradeliesenceNOTV?.text.toString()
                .isEmpty() || binding?.tradeliesencExpireDateTV?.text.toString().isEmpty())
        ) {
            return if (!isExpireDateofTradeLisenseSelectd) {
                context?.toast("ট্রেড লাইসেন্স এর এক্সপায়ার ডেট এড করুন")
                false
            } else if (binding?.tradeliesenceNOTV?.text.toString().isEmpty()) {
                context?.toast("টট্রেড লাইসেন্স এর নাম্বার দিন")
                false
            } else {
                context?.toast("ট্রেড লাইসেন্স এর তথ্য দিন")
                false
            }
        }

        if (imagePickFlag == 1) {
            if (tradeLicenseImageUrl.isEmpty()) {
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
            } else if (guarantorNumber.length != 11 || !isValidMobileNumber(guarantorNumber)) {
                context?.toast("গ্যারান্টার এর সঠিক মোবাইল নাম্বার লিখুন")
                return false
            }
        }

        if (selectedAverageOrder.isEmpty()) {
            context?.toast("আপনার গড় অর্ডার এর তথ্য দিন")
            return false
        }
        if (selectedMonthlyExp.isEmpty()) {
            context?.toast("আপনার মাসিক ব্যায় এর পরিমান দিন")
            return false
        }

        /*if (selectedMarketPosition.isEmpty()) {
            context?.toast("Please Fill in your market position information")
            return false
        }*/

        /*if (recommendationAdapter.selectedItem == "") {
            context?.toast("সুপারিশের তথ্য দিন")
            return false
        }*/
        return true
    }
}