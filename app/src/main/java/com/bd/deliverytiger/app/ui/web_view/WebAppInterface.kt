package com.bd.deliverytiger.app.ui.web_view

import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.toast
import timber.log.Timber

class WebAppInterface(private val context: Context?, private val repository: AppRepository, private val bundle: Bundle?) {

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
     */
    @JavascriptInterface
    fun goToOrderConfirmationPageInAppForBkash(id: String?, message: String?, CouponId: String?, OrderType: String?) {

        Timber.d("webView WebAppInterface $id $message $CouponId $OrderType")
        context?.toast(message)
        if (id == "1") {

            /*val requestBody: MutableList<StatusUpdateModel> = bundle?.getParcelableArrayList("updateModel") ?: mutableListOf()
            CoroutineScope(Dispatchers.IO).launch {
                val response = repository.orderStatusUpdate(requestBody)
                withContext(Dispatchers.Main) {
                    when (response) {
                        is NetworkResponse.Success -> {
                            if (response.body.isSuccess) {
                                //val message = "স্টেটাস আপডেট হয়েছে"
                                val message = response.body.message
                                context?.toast(message)
                            } else {
                                val message1 = response.body.message
                                context?.toast(message1)
                            }
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
            }*/
        }
    }

}