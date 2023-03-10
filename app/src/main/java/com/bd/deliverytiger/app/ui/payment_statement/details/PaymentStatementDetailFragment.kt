package com.bd.deliverytiger.app.ui.payment_statement.details

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.payment_statement.OrderHistoryData
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsResponse
import com.bd.deliverytiger.app.databinding.FragmentPaymentStatementDetailBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.payment_statement.excel_generator.ExcelGenerator
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File

class PaymentStatementDetailFragment : Fragment() {

    private val viewModel: PaymentStatementDetailViewModel by inject()
    private var binding: FragmentPaymentStatementDetailBinding? = null

    private lateinit var dataAdapter: PaymentStatementDetailsAdapter
    private var transactionId: String = ""
    private var responseModel: PaymentDetailsResponse? = null

    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val permissionStorage = 9875

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentPaymentStatementDetailBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bundle: Bundle? = arguments
        bundle?.let {
            transactionId = it.getString("transactionId") ?: ""
        }

        dataAdapter = PaymentStatementDetailsAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.onItemClicked = { model, tabFlag ->
            showDetails(model, tabFlag)
        }

        viewModel.getPaymentHistoryDetails(SessionManager.courierUserId, transactionId).observe(viewLifecycleOwner, Observer { model ->
            responseModel = model
            model?.orderList?.let { list ->
                //dataAdapter.initLoad(list)
                if (list.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                    binding?.statementCard?.visibility = View.GONE
                    binding?.filterTab?.visibility = View.GONE
                    //binding?.header?.info1?.text = "????????? ???????????????????????? ??? ??????"
                    //binding?.header?.info2?.text = "????????? ??????????????????????????????: ??? ???"
                } else {
                    binding?.emptyView?.visibility = View.GONE
                    binding?.statementCard?.visibility = View.VISIBLE
                    binding?.filterTab?.visibility = View.VISIBLE
                    //binding?.header?.info1?.text = "????????? ???????????????????????? ${DigitConverter.toBanglaDigit(list.size)} ??????"
                    //binding?.header?.info2?.text = "????????? ??????????????????????????????: ${DigitConverter.toBanglaDigit(it.netPaidAmount)} ???"
                }
            }

            val transName = when (model.modeOfPayment) {
                "Bkash" -> "??????????????? ???????????????????????????????????? ??????"
                "Cheque" -> "????????? ??????"
                else -> "??????????????????????????? ??????"
            }

            binding?.key1?.text = transName
            binding?.transactionNo?.text = model?.transactionNo
            binding?.paymentMedium?.text = model?.modeOfPayment
            binding?.orderCount?.text = "${DigitConverter.toBanglaDigit(model?.totalOrderCount.toString())} ??????"
            binding?.totalCollectionAmount?.text = "${DigitConverter.toBanglaDigit(model?.netCollectedAmount.toString())} ???"
            binding?.totalCharge?.text = "- ${DigitConverter.toBanglaDigit(model?.netTotalCharge.toString())} ???"
            binding?.totalAdjustment?.text = "- ${DigitConverter.toBanglaDigit(model?.netAdjustedAmount.toString())} ???"
            if (model.iPCharge > 0){
                binding?.key10?.isVisible = true
                binding?.instantPaymentCharge?.isVisible = true
                binding?.instantPaymentCharge?.text = "- ${DigitConverter.toBanglaDigit(model?.iPCharge.toString())} ???"
            }

            if ((model?.netAdvanceReceivable ?: 0) > 0) {
                binding?.key9?.isVisible = true
                binding?.adjustmentReceivable?.isVisible = true
                binding?.adjustmentReceivable?.text = "- ${DigitConverter.toBanglaDigit(model?.netAdvanceReceivable.toString())} ???"
            }
            binding?.totalPayment?.text = "${DigitConverter.toBanglaDigit(model?.netPaidAmount.toString())} ???"

            binding?.filterTab?.getTabAt(0)?.text = "COD (${model?.totalCrOrderCount.toString()})"
            binding?.filterTab?.getTabAt(1)?.text = "Only Delivery & Adjustment (${model?.totalAdOrderCount.toString()})"
            if ((model?.netAdvanceReceivable ?: 0) > 0) {
                binding?.filterTab?.let { tabLayout ->
                    tabLayout.addTab(tabLayout.newTab())
                    tabLayout.getTabAt(2)?.text = "Return payment Adjustment (${model?.totalAdvanceReceivableCount.toString()})"
                }
            }

            filterOrderList(model?.orderList, "CR")
            binding?.filterTab?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            Timber.d("Tab selected 0")
                            dataAdapter.tabFlag = 0
                            filterOrderList(model?.orderList, "CR")
                        }
                        1 -> {
                            dataAdapter.tabFlag = 1
                            filterOrderList(model?.orderList, "CC")
                        }
                        2 -> {
                            dataAdapter.tabFlag = 2
                            filterOrderList(model?.orderList, "ADJ")
                        }
                    }
                }
            })


        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    requireContext().toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun showDetails(model: OrderHistoryData, tabFlag: Int) {
        if (tabFlag == 2) {
            return
        }
        val dialog = OrderChargeDetailsBottomSheet.newInstance(model, tabFlag)
        dialog.show(childFragmentManager, OrderChargeDetailsBottomSheet.tag)
    }

    private fun filterOrderList(dataList: List<OrderHistoryData>?, filterKey: String) {

        val filteredList = dataList?.filter { it.type == filterKey }
        if (!filteredList.isNullOrEmpty()) {
            dataAdapter.initLoad(filteredList)
            binding?.emptyView?.visibility = View.GONE
        } else {
            dataAdapter.clear()
            binding?.emptyView?.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("?????????????????????????????? ?????????????????????")
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }

    /**
     * Called from Home Activity
     */
    fun downloadFile() {
        if (isStoragePermissions()) {
            generateExcel()
        }
    }

    private fun isStoragePermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val storagePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                val storagePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (storagePermissionRationale) {
                    requestPermissions(permissions, permissionStorage)
                } else {
                    requestPermissions(permissions, permissionStorage)
                }
                false
            } else {
                true
            }
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            permissionStorage -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        generateExcel()
                    } else {
                        val storagePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        if (storagePermissionRationale) {
                            alert("Permission Required", "App required Storage permission to function properly. Please grand permission.", true, "Give Permission", "Cancel") {
                                if (it == AlertDialog.BUTTON_POSITIVE) {
                                    requestPermissions(permissions, permissionStorage)
                                }
                            }
                        } else {
                            alert("Permission Required", "Please go to Settings to enable Storage permission. (Settings-apps--permissions)", true, "Settings", "Cancel")  {
                                if (it == AlertDialog.BUTTON_POSITIVE) {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${requireContext().packageName}")).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    requireContext().startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun generateExcel() {

        if (responseModel != null) {
            val generator = ExcelGenerator(requireContext())
            val filePath = generator.writeExcel(responseModel)
            Timber.d("generateExcel $filePath")
            if (filePath.isNotEmpty()) {
                binding?.parent?.snackbar("???????????? ????????????????????? ?????????????????? ???????????? ????????? ??????????????????\nSDCard/Download/${getString(R.string.app_name)}", Snackbar.LENGTH_INDEFINITE, "????????????") {
                    openFile(filePath)
                }?.show()
            }
        } else {
            context?.toast("???????????? ???????????? ?????????")
        }

    }

    private fun openFile(filePath: String) {
        try {
            val fileName = Uri.parse(filePath).lastPathSegment
            val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
            val fileDir = "${requireContext().getString(R.string.app_name)}/$fileName"
            val filePath = "$directoryPath/$fileDir"

            val file = File(filePath)
            if (file.exists()) {
                val fileUri = FileProvider.getUriForFile(requireContext(), getString(R.string.file_provider_authority), file)
                Intent(Intent.ACTION_VIEW).apply {
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    setDataAndType(fileUri, getFileContentType(filePath))
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }.also {
                    startActivity(it)
                }
            } else {
                binding?.parent?.snackbar("Unable to open file", Snackbar.LENGTH_SHORT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding?.parent?.snackbar("Unable to open file", Snackbar.LENGTH_SHORT)
        }
    }

}