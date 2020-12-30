package com.bd.deliverytiger.app.ui.add_order


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.courier_info.CourierInfoModel
import com.bd.deliverytiger.app.api.model.log_sms.SMSLogRequest
import com.bd.deliverytiger.app.api.model.offer.OfferUpdateRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.all_orders.AllOrdersFragment
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.button.MaterialButton
import org.koin.android.ext.android.inject
import java.util.*

class OrderSuccessFragment : Fragment() {

    private val viewModel: OrderSuccessViewModel by inject()

    private lateinit var tvSuccessOrderId: TextView
    private lateinit var tvSuccessOrderTitle: TextView
    private lateinit var tvSuccessOrderAddress: TextView
    private lateinit var orderListClickedLay: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var offerBtn: MaterialButton

    private var bundle: Bundle? = null
    private var orderResponse: OrderResponse? = null
    private var isCollection: Boolean = false
    private var courierInfoModel: CourierInfoModel? = null
    private var offerBkashDiscount: Int = 0
    private var offerCodDiscount: Int = 0

    private var offerBkashClaimed: Boolean = false
    private var offerCodClaimed: Boolean = false

    private var courierOrdersId: String = ""


    companion object {
        fun newInstance(bundle: Bundle?): OrderSuccessFragment = OrderSuccessFragment().apply{
            this.bundle = bundle
        }
        val tag: String = AddOrderFragmentOne::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_success, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvSuccessOrderId = view.findViewById(R.id.tvSuccessOrderId)
        tvSuccessOrderTitle = view.findViewById(R.id.tvSuccessOrderTitle)
        tvSuccessOrderAddress = view.findViewById(R.id.tvSuccessOrderAddress)
        orderListClickedLay = view.findViewById(R.id.orderListClickedLay)
        progressBar = view.findViewById(R.id.progressBar)
        offerBtn = view.findViewById(R.id.offerBtn)

        bundle?.let {
            orderResponse = it.getParcelable("orderResponse")
            isCollection = it.getBoolean("isCollection", false)
        }

        if(orderResponse != null){
            courierOrdersId = orderResponse!!.courierOrdersId ?: ""
            tvSuccessOrderId.text ="# $courierOrdersId"
            tvSuccessOrderTitle.text =orderResponse!!.collectionName
            tvSuccessOrderAddress.text = getAddress(orderResponse)

            timeLimitAlert()
        }

        viewModel.getCourierUsersInformation(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            courierInfoModel = model
            SessionManager.credit = courierInfoModel?.credit?.toInt() ?: 0
            offerBkashDiscount = if (orderResponse?.districtId == 14) {
                courierInfoModel?.offerBkashDiscountDhaka?.toInt() ?: 0
            } else {
                courierInfoModel?.offerBkashDiscountOutSideDhaka?.toInt() ?: 0
            }
            offerCodDiscount = courierInfoModel?.offerCodDiscount?.toInt() ?: 0
            if (model.isOfferActive) {
                when (model.offerType) {
                    // COD
                    1 -> {
                        offerBtn.visibility = View.VISIBLE
                        offerBottomSheet(model)
                    }
                    // bkash
                    2 -> {
                        if (isCollection) {
                            offerBtn.visibility = View.VISIBLE
                            offerBottomSheet(model)
                        }
                    }
                    // All
                    3 -> {
                        offerBtn.visibility = View.VISIBLE
                        offerBottomSheet(model)
                    }
                }
            }
        })



        orderListClickedLay.setOnClickListener {
            allOrderListFragment()
        }

        offerBtn.setOnClickListener {
            offerBottomSheet(courierInfoModel)
        }

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    context?.toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    if (state.isShow) {
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        })

        UserLogger.logPurchase(SessionManager.totalAmount)
    }

    private fun timeLimitAlert() {

        var msg = "Your Delivery Tiger order (${orderResponse?.courierOrdersId}) has been placed. We will collect it as soon as possible by today before 6 pm."
        val calendar = Calendar.getInstance()
        val currentHour24 = calendar.get(Calendar.HOUR_OF_DAY)
        if (currentHour24 >= 16) {
            msg += " If your order has been placed after 4 pm, it might not be collected today."
        }
        alert ("Confirmation", msg, false, "Ok", "Cancel").show()
    }

    private fun offerBottomSheet(model: CourierInfoModel?) {

        model ?: return

        val bundle = bundleOf(
            "offerType" to model.offerType,
            "offerCodDiscount" to offerCodDiscount,
            "offerBkashDiscount" to offerBkashDiscount,
            "offerBkashClaimed" to offerBkashClaimed,
            "offerCodClaimed" to offerCodClaimed,
            "isCollection" to isCollection // if true show Adv offer
        )

        val tag: String = OfferBottomSheet.tag
        val dialog: OfferBottomSheet = OfferBottomSheet.newInstance(bundle)
        dialog.show(childFragmentManager, tag)
        dialog.onOfferSelected = { offerType ->
            dialog.dismiss()
            when(offerType) {
                1 -> {
                    addProductBottomSheet(offerType)
                }
                2 -> {
                    addProductBottomSheet(offerType)
                    //claimBkashOffer()
                }
            }
        }
    }

    private fun claimBkashOffer(dealId: Int) {
        val requestBody = OfferUpdateRequest(offerBkashDiscount, 0, dealId)
        viewModel.updateOffer(orderResponse?.id ?: 0, requestBody).observe(viewLifecycleOwner, Observer { model ->
            // For customer
            val body1 = "আপনাকে https://deliverytiger.com.bd/apply-offer/${model.id}/${model.offerCode} লিংকে গিয়ে পেমেন্ট করার জন্য অনুরোধ করা হচ্ছে।"
            viewModel.sendSMS(model.mobile ?: "", body1)
            // For merchant
            val body2 = "এই বিকাশ পেমেন্ট লিংকটি আপনার কাস্টমারকে দিয়ে পেমেন্ট করার অনুরোধ করুন।\nhttps://deliverytiger.com.bd/apply-offer/${model.id}/${model.offerCode}"
            viewModel.sendSMS(SessionManager.mobile, body2)
            offerBkashClaimed = true
            alert("অফার", getString(R.string.offer_advance_success)).show()
            viewModel.logSMS(SMSLogRequest(courierOrdersId, body1))
        })
    }

    private fun claimCodOffer(dealId: Int) {
        val requestBody = OfferUpdateRequest(0, offerCodDiscount, dealId)
        viewModel.updateOffer(orderResponse?.id ?: 0, requestBody).observe(viewLifecycleOwner, Observer { model ->
            offerCodClaimed = true
            alert("অফার", "আপনার প্রোডাক্টটি আজকেরডিল মার্কেটপ্লেসে যুক্ত হয়েছে এবং আপনি ${DigitConverter.toBanglaDigit(offerCodDiscount)} টাকা ছাড় পেয়েছেন।").show()
        })
    }

    private fun addProductBottomSheet(offerType: Int) {

        val tag: String = AddProductBottomSheet.tag
        val dialog: AddProductBottomSheet = AddProductBottomSheet.newInstance(offerType)
        dialog.show(childFragmentManager, tag)
        dialog.onProductUploaded = { dealId, offerType ->
            dialog.dismiss()
            when(offerType) {
                1 -> {
                    if (dealId > 0) {
                        claimCodOffer(dealId)
                    }
                }
                2 -> {
                    if (dealId > 0) {
                        claimBkashOffer(dealId)
                    }
                }
            }

        }
    }

    private fun allOrderListFragment() {
       // activity?.supportFragmentManager?.popBackStack()
        val fragment = AllOrdersFragment.newInstance()
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, AllOrdersFragment.tag)
       // ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()
    }

    private fun getAddress(orderResponse: OrderResponse?): String {
        return orderResponse?.customerName + "\n" +
                orderResponse?.mobile + "," +
                orderResponse?.otherMobile + "\n" +
                orderResponse?.address
    }


}
