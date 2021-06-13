package com.bd.deliverytiger.app.api.model.location

import com.bd.deliverytiger.app.api.model.district.AllDistrictListsModel
import com.google.gson.annotations.SerializedName
import java.util.*

data class LocationData(
    var id: Int = 0,
    var displayNameBangla: String? = "",
    var displayNameEng: String? = "",
    var displayPostalCode: String? = "",
    var searchKey: String = "" // lower case
) {

    companion object {
        fun from(model: AllDistrictListsModel): LocationData {
            return LocationData(
                model.districtId,
                model.districtBng,
                model.district,
                model.postalCode,
                model.district?.lowercase(Locale.US) ?: ""
            )
        }
    }

}
