package com.bd.deliverytiger.app.api.model.district

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.SerializedName


@Entity(tableName = "district_table")
@Parcelize
data class DistrictData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Int = 0,

    @ColumnInfo(name = "districtId")
    @SerializedName("districtId")
    var districtId: Int = 0,

    @ColumnInfo(name = "district")
    @SerializedName("district")
    var district: String? = "",

    @ColumnInfo(name = "districtBng")
    @SerializedName("districtBng")
    var districtBng: String? = "",

    @ColumnInfo(name = "areaType")
    @SerializedName("areaType")
    var areaType: Int = 0,

    @ColumnInfo(name = "parentId")
    @SerializedName("parentId")
    var parentId: Int = 0,

    @ColumnInfo(name = "postalCode")
    @SerializedName("postalCode")
    var postalCode: String? = "",

    @ColumnInfo(name = "isCity")
    @SerializedName("isCity")
    var isCity: Boolean = false,

    @ColumnInfo(name = "isActive")
    @SerializedName("isActive")
    var isActive: Boolean = false,

    @ColumnInfo(name = "isActiveForCorona")
    @SerializedName("isActiveForCorona")
    var isActiveForCorona: Boolean = false,

    @ColumnInfo(name = "isPickupLocation")
    @SerializedName("isPickupLocation")
    var isPickupLocation: Boolean = false,

    @ColumnInfo(name = "districtPriority")
    @SerializedName("districtPriority")
    var districtPriority: Int = 0
): Parcelable