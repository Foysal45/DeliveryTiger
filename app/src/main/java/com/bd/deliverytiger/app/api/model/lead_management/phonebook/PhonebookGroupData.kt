package com.bd.deliverytiger.app.api.model.lead_management.phonebook


import com.google.gson.annotations.SerializedName

data class PhonebookGroupData(
    @SerializedName("groupName")
    var groupName: String? = "",
    @SerializedName("phoneBookGroupId")
    var phoneBookGroupId: Int = 0
)