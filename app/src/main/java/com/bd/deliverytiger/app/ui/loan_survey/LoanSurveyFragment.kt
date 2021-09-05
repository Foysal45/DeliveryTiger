package com.bd.deliverytiger.app.ui.loan_survey

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
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

    private var loanRangeList: MutableList<String> = mutableListOf()
    private var selectedLoanRange = 0
    private var loanRange = ""

    private var monthlyParcelCountList: MutableList<String> = mutableListOf()
    private var selectedMonthlyParcelCount = 0
    private var monthlyParcelCount = ""

    private var monthlyTransactionList: MutableList<String> = mutableListOf()
    private var selectedMonthlyTransaction = 0
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
        init()
        initClickListener()
        fetchLoanRange()
        fetchMonthlyParcelCount()
        fetchMonthlyTransaction()
    }

    private fun init() {
        /*binding?.recyclerViewEducation?.let { recyclerView ->
            recyclerView.apply {
                layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
                adapter = dataAdapterEducation
                itemAnimator = null
            }
        }
        binding?.recyclerViewProfession?.let { recyclerView ->
            recyclerView.apply {
                layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
                adapter = dataAdapterProfession
                itemAnimator = null
            }
        }*/
    }

    private fun fetchLoanRange() {
        val tempList = mutableListOf("১,০০০-৫,০০০", "৫,০০০-১০,০০০", "১০,০০০-২০,০০০", "২০,০০০-৫০,০০০", "অন্যান্য")
        val firstOption = resources.getStringArray(R.array.pick_options).toList()
        loanRangeList.clear()
        loanRangeList.addAll(firstOption)
        loanRangeList.addAll(tempList)
        /*danaViewModel.danaAllVariables.observe(viewLifecycleOwner, Observer { data ->
            data.income?.let {
                monthlyIncomeList.addAll(it.values)
            }
        })*/

        val spinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, loanRangeList)
        binding?.spinnerLoanRange?.adapter = spinnerAdapter
        binding?.spinnerLoanRange?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLoanRange = position
                Timber.d("requestBody $selectedLoanRange, ${loanRangeList.size-1}")
                binding?.loanRangeETLayout?.isVisible = selectedLoanRange == (loanRangeList.size-1)
            }
        }

    }

    private fun fetchMonthlyParcelCount() {
        val tempList = mutableListOf("১০-৫০", "৫০-২০০", "২০০-৫০০", "৫০০-১০০০", "অন্যান্য")
        val firstOption = resources.getStringArray(R.array.pick_options).toList()
        monthlyParcelCountList.clear()
        monthlyParcelCountList.addAll(firstOption)
        monthlyParcelCountList.addAll(tempList)
        /*danaViewModel.danaAllVariables.observe(viewLifecycleOwner, Observer { data ->
            data.income?.let {
                monthlyIncomeList.addAll(it.values)
            }
        })*/

        val spinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, monthlyParcelCountList)
        binding?.spinnerMonthlyParcelCount?.adapter = spinnerAdapter
        binding?.spinnerMonthlyParcelCount?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMonthlyParcelCount = position
                Timber.d("requestBody $selectedMonthlyParcelCount, ${monthlyParcelCountList.size-1}")
                binding?.monthlyParcelCountETLayout?.isVisible = selectedMonthlyParcelCount == (monthlyParcelCountList.size-1)
            }
        }
    }

    private fun fetchMonthlyTransaction() {
        val tempList = mutableListOf("১,০০০-৫,০০০", "৫,০০০-১০,০০০", "১০,০০০-২০,০০০", "২০,০০০-৫০,০০০", "অন্যান্য")
        val firstOption = resources.getStringArray(R.array.pick_options).toList()
        monthlyTransactionList.clear()
        monthlyTransactionList.addAll(firstOption)
        monthlyTransactionList.addAll(tempList)
        /*danaViewModel.danaAllVariables.observe(viewLifecycleOwner, Observer { data ->
            data.income?.let {
                monthlyIncomeList.addAll(it.values)
            }
        })*/

        val spinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, monthlyTransactionList)
        binding?.spinnerMonthlyTransaction?.adapter = spinnerAdapter
        binding?.spinnerMonthlyTransaction?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMonthlyTransaction = position
                Timber.d("requestBody $selectedMonthlyTransaction, ${monthlyTransactionList.size-1}")
                binding?.monthlyTransactionETLayout?.isVisible = selectedMonthlyTransaction == (monthlyTransactionList.size-1)
            }
        }
    }

    private fun initClickListener() {
        binding?.applyLoanBtn?.setOnClickListener {
            if (verify()) {
                /*val requestBody = LoanSurveyRequestBody(merchantGender, loanRange, monthlyParcelCount, monthlyTransaction,
                    hasBankAccount, hasPhysicalShop, hasTradeLicence, imageTradeLicencePath)*/

                val requestBody = LoanSurveyRequestBody(SessionManager.courierUserId, merchantGender,
                "https://static.ajkerdeal.com/delivery_tiger/trade_license/trade_${SessionManager.courierUserId}.jpg")
                uploadImage(
                    "trade_${SessionManager.courierUserId}.jpg",
                    "delivery_tiger/trade_license",
                    imageTradeLicencePath, requestBody
                )
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
                    imagePickFlag = 1
                    hasTradeLicence = true
                    binding?.merchantTradeLicenceLayout?.isVisible = true
                }
                R.id.merchantHasTradeLicenceNo -> {
                    imagePickFlag = 0
                    hasTradeLicence = false
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

        if (selectedLoanRange == 0) {
            context?.toast("Select a Loan range")
            return false
        } else if (selectedLoanRange == loanRangeList.size-1) {
            val tempLoanRange = binding?.loanRangeET?.text
            if (tempLoanRange.isNullOrEmpty()) {
                context?.toast("Please enter loan range")
                return false
            } else {
                loanRange = tempLoanRange.toString()
            }
        } else {
            loanRange = loanRangeList[selectedLoanRange]
        }

        if (selectedMonthlyParcelCount == 0) {
            context?.toast("Select a Monthly Parcel Count")
            return false
        } else if (selectedMonthlyParcelCount == monthlyParcelCountList.size-1) {
            val tempMonthlyParcelCount = binding?.monthlyParcelCountET?.text
            if (tempMonthlyParcelCount.isNullOrEmpty()) {
                context?.toast("Please enter Monthly Parcel Count")
                return false
            } else {
                monthlyParcelCount = tempMonthlyParcelCount.toString()
            }
        } else {
            monthlyParcelCount = monthlyParcelCountList[selectedMonthlyParcelCount]
        }


        if (selectedMonthlyTransaction == 0) {
            context?.toast("Select a Monthly Transaction")
            return false
        } else if (selectedMonthlyTransaction == monthlyTransactionList.size-1) {
            val tempMonthlyTransaction = binding?.monthlyTransactionET?.text
            if (tempMonthlyTransaction.isNullOrEmpty()) {
                context?.toast("Please enter Monthly Transaction")
                return false
            } else {
                monthlyTransaction = tempMonthlyTransaction.toString()
            }
        } else {
            monthlyTransaction = monthlyTransactionList[selectedMonthlyTransaction]
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
                    imagePickFlag = 0
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
                context?.toast("Uploaded successfully")
                viewModel.submitLoanSurvey(requestBody)
            } else {
                context?.toast("Uploaded Failed")
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

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image: Image? = ImagePicker.getFirstImageOrNull(data)
            if (image != null) {
                imagePath = image.path
                binding?.image?.let { view ->
                    Glide.with(requireContext()).load(imagePath).apply(RequestOptions().placeholder(R.drawable.ic_banner_place)).into(view)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }*/
}