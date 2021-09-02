package com.bd.deliverytiger.app.api.model.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseCredential(
    var projectId: String = "",
    var applicationId: String = "",
    var apikey: String = "",
    var databaseUrl: String = "",
    var storageUrl: String = "",
    var firebaseWebApiKey: String = ""
): Parcelable
