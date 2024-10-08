package com.app.myrickshawparent.ui.track.response

import com.app.myrickshawparent.ui.home.item.HomeResponse
import com.google.gson.annotations.SerializedName

data class LiveTrackSocketResponse(

    @field:SerializedName("vehicleNo")
    val vehicleNo: String = "",

    @field:SerializedName("etaMessage")
    val etaMessage: String = "",

    @field:SerializedName("latitude")
    val latitude: String = "",

    @field:SerializedName("longitude")
    val longitude: String = "",

    @SerializedName("routeStops")
    val routeStops: MutableList<HomeResponse.RouteStopsItem> = arrayListOf(),

    ) {}
