package com.bd.deliverytiger.app.log

import androidx.core.os.bundleOf
import com.bd.deliverytiger.app.utils.SessionManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object UserLogger {

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    private val deviceId = SessionManager.deviceId

    fun logAppOpen() {
        val courierUserId = SessionManager.courierUserId
        val bundle = bundleOf(
            "DeviceId" to deviceId,
            "CourierUserId" to courierUserId,
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
    }

    fun logLogIn() {
        val courierUserId = SessionManager.courierUserId
        val bundle = bundleOf(
            "DeviceId" to deviceId,
            "CourierUserId" to courierUserId,
            FirebaseAnalytics.Param.METHOD to "PhoneNumber"
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    fun logRegistration(courierUserId: Int, orderSource: String) {
        val bundle = bundleOf(
            "DeviceId" to deviceId,
            "CourierUserId" to courierUserId,
            "Source" to orderSource
        )
        firebaseAnalytics.logEvent("Registration", bundle)
    }

    fun logOpenSource(openSource: String, deepLink: String? = null) {
        val courierUserId = SessionManager.courierUserId
        val bundle = bundleOf(
            "DeviceId" to deviceId,
            "CourierUserId" to courierUserId,
            "OpenSource" to openSource,
            "DeepLink" to deepLink
        )
        firebaseAnalytics.logEvent("OpenSource", bundle)
    }

    fun logPurchase(totalAmount: Int) {
        val courierUserId = SessionManager.courierUserId
        val orderSource = SessionManager.orderSource
        val bundle = bundleOf(
            "DeviceId" to deviceId,
            "CourierUserId" to courierUserId,
            "Source" to orderSource,
            FirebaseAnalytics.Param.CURRENCY to "BDT",
            FirebaseAnalytics.Param.VALUE to totalAmount
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)
    }

    fun logGenie(eventName: String) {
        val courierUserId = SessionManager.courierUserId
        val bundle = bundleOf(
            "DeviceId" to deviceId,
            "CourierUserId" to courierUserId,
            "ButtonClicked" to eventName
        )
        firebaseAnalytics.logEvent("GenieEvent", bundle)
    }


}