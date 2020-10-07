package com.bd.deliverytiger.app.ui.web_view

import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableUpdateRequest
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.exhaustive
import com.bd.deliverytiger.app.utils.toast
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class WebAppInterface(private val context: Context?, private val repository: AppRepository, private val bundle: Bundle? = null) {

    @JavascriptInterface
    fun showToast(msg: String?) {
        context?.toast(msg)
    }

    @JavascriptInterface
    fun SendToBackButtonInApp(isClicked: String) {
        if (isClicked == "Clicked") {
            (context as HomeActivity).onBackPressed()
        }
    }

    /**
     * Call back from website for bkash getway
     *
     * @param id            "1" success
     * @param message       "মুল্য প্রদান সফল হয়েছে" / "মুল্য প্রদান সফল হয়নি"
     * @param CouponId      coupon id of order
     * @param OrderType     "S" for single order, "C" for cart Order
     * @param TransactionId "TransactionId of bKash payment"
     */
    @JavascriptInterface
    fun goToOrderConfirmationPageInAppForBkash(id: String?, message: String?, CouponId: String?, OrderType: String?, TransactionId: String?) {

        Timber.d("webView WebAppInterface $id $message $CouponId $OrderType $TransactionId")
        context?.toast(message)
        if (id == "1") {

            val requestBody: MonthlyReceivableUpdateRequest? = bundle?.getParcelable("requestBody")
            if (requestBody != null) {
                requestBody.transactionId = TransactionId ?: ""
                CoroutineScope(Dispatchers.IO).launch {
                    val response = repository.bulkMerchantCashCollection(requestBody)
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is NetworkResponse.Success -> {
                                val responseMsg = response.body
                                context?.toast(responseMsg)
                            }
                            is NetworkResponse.ServerError -> {
                                val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                                context?.toast(message)
                            }
                            is NetworkResponse.NetworkError -> {
                                val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                                context?.toast(message)
                            }
                            is NetworkResponse.UnknownError -> {
                                val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                                context?.toast(message)
                                Timber.d(response.error)
                            }
                        }.exhaustive
                    }
                }
            }
        }
    }

    @JavascriptInterface
    fun goToAppForAdvancePayment(id : String?, message: String?, TransactionId: String?) {
        context?.toast(message)
        //(context as HomeActivity).onBackPressed()
    }

}