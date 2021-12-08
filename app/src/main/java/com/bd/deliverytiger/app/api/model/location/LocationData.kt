package com.bd.deliverytiger.app.api.model.location

import com.bd.deliverytiger.app.api.model.category.CategoryData
import com.bd.deliverytiger.app.api.model.category.SubCategoryData
import com.bd.deliverytiger.app.api.model.district.DistrictData
import java.util.*

data class LocationData(
    var id: Int = 0,
    var displayNameBangla: String? = "",
    var displayNameEng: String? = "",
    var displayPostalCode: String? = "",
    var searchKey: String = "", // lower case
    var isDeactivate: Boolean = false,
    var alertMsg: String? = ""
) {

    companion object {
        fun from(model: DistrictData): LocationData {
            return LocationData(
                model.districtId,
                model.districtBng,
                model.district,
                model.postalCode,
                model.district?.lowercase(Locale.US) ?: "",
                model.isActiveForCorona,
                model.nextDayAlertMessage
            )
        }

        fun from(model: CategoryData): LocationData {
            return LocationData(
                model.categoryId,
                model.categoryNameBng,
                model.categoryNameEng
            )
        }

        fun from(model: SubCategoryData): LocationData {
            return LocationData(
                model.subCategoryId,
                model.subCategoryNameBng,
                model.subCategoryNameEng
            )
        }
    }

}
