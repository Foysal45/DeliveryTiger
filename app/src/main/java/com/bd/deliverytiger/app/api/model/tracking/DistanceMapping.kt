package com.bd.deliverytiger.app.api.model.tracking

import com.bd.deliverytiger.app.api.model.cod_collection.HubInfo

data class DistanceMapping (
    var distance: Float = 0.0f,
    var hubModel: HubInfo
)