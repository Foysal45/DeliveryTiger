package com.bd.deliverytiger.app.ui.payment_statement.details

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.payment_statement.OrderHistoryData
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsResponse
import com.bd.deliverytiger.app.databinding.FragmentPaymentStatementDetailBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.payment_statement.excel_generator.ExcelGenerator
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.inject
import timber.log.Timber

class PaymentStatementDetailFragment : Fragment() {

    private val viewModel: PaymentStatementDetailViewModel by inject()
    private var binding: FragmentPaymentStatementDetailBinding? = null

    private lateinit var dataAdapter: PaymentStatementDetailsAdapter
    private var transactionId: String = ""
    private var responseModel: PaymentDetailsResponse? = null

    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val permissionStorage = 9875

    companion object {
        fun newInstance(transactionId: String): PaymentStatementDetailFragment = PaymentStatementDetailFragment().apply {
            this.transactionId = transactionId
        }

        val tag: String = PaymentStatementDetailFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentPaymentStatementDetailBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dataAdapter = PaymentStatementDetailsAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.onItemClicked = {
            val dialog = OrderChargeDetailsFragment.newInstance(it)
            dialog.show(childFragmentManager, OrderChargeDetailsFragment.tag)
        }

        viewModel.getPaymentHistoryDetails(transactionId).observe(viewLifecycleOwner, Observer { model ->
            responseModel = model
            model?.orderList?.let { list ->
                //dataAdapter.initLoad(list)
                if (list.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                    binding?.statementCard?.visibility = View.GONE
                    binding?.filterTab?.visibility = View.GONE
                    //binding?.header?.info1?.text = "মোট অর্ডার: ০ টি"
                    //binding?.header?.info2?.text = "মোট অ্যামাউন্ট: ০ ৳"
                } else {
                    binding?.emptyView?.visibility = View.GONE
                    binding?.statementCard?.visibility = View.VISIBLE
                    binding?.filterTab?.visibility = View.VISIBLE
                    //binding?.header?.info1?.text = "মোট অর্ডার: ${DigitConverter.toBanglaDigit(list.size)} টি"
                    //binding?.header?.info2?.text = "মোট অ্যামাউন্ট: ${DigitConverter.toBanglaDigit(it.netPaidAmount)} ৳"
                }
            }

            binding?.transactionNo?.text = model?.transactionNo
            binding?.paymentMedium?.text = model?.modeOfPayment
            binding?.orderCount?.text = "${DigitConverter.toBanglaDigit(model?.totalOrderCount.toString())} টি"
            binding?.totalCollectionAmount?.text = "${DigitConverter.toBanglaDigit(model?.netCollectedAmount.toString())} ৳"
            binding?.totalCharge?.text = "- ${DigitConverter.toBanglaDigit(model?.netTotalCharge.toString())} ৳"
            binding?.totalAdjustment?.text = "- ${DigitConverter.toBanglaDigit(model?.netAdjustedAmount.toString())} ৳"
            binding?.totalPayment?.text = "${DigitConverter.toBanglaDigit(model?.netPaidAmount.toString())} ৳"

            binding?.filterTab?.getTabAt(0)?.text = "পেইড (${DigitConverter.toBanglaDigit(model?.totalCrOrderCount.toString())})"
            binding?.filterTab?.getTabAt(1)?.text = "এডজাস্টেড (${DigitConverter.toBanglaDigit(model?.totalAdOrderCount.toString())})"

            filterOrderList(model?.orderList, "CR")
            binding?.filterTab?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            Timber.d("Tab selected 0")
                            filterOrderList(model?.orderList, "CR")
                        }
                        1 -> {
                            filterOrderList(model?.orderList, "CC")
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
        (activity as HomeActivity).setToolbarTitle("স্টেটমেন্ট ডিটেলস")
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
            generator.writeExcel(responseModel)
        } else {
            context?.toast("কোনো তথ্য নেই")
        }

    }

}