package com.bd.deliverytiger.app.fcm

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "notification_table")
@Parcelize
data class FCMData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Int = 0,

    @ColumnInfo(name = "notificationType")
    @SerializedName("notificationType")
    var notificationType: String? = "",

    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String? = "",

    @ColumnInfo(name = "body")
    @SerializedName("body")
    var body: String? = "",

    @ColumnInfo(name = "imageUrl")
    @SerializedName("imageUrl")
    var imageUrl: String? = "",

    @ColumnInfo(name = "bigText")
    @SerializedName("bigText")
    var bigText: String? = "",

    @ColumnInfo(name = "productImage")
    @SerializedName("productImage")
    var productImage: String? = "",

    @ColumnInfo(name = "status")
    @SerializedName("status")
    var status: String? = "",

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    var createdAt: String? = "", //2019-12-13 16:18:32

    @Ignore
    @SerializedName("senderId")
    var senderId: String? = "",
    @Ignore
    @SerializedName("senderName")
    var senderName: String? = "",
    @Ignore
    @SerializedName("senderRole")
    var senderRole: String? = "",
    @Ignore
    @SerializedName("receiverId")
    var receiverId: String? = ""

): Parcelable