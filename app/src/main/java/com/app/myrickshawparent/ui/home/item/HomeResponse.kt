package com.app.myrickshawparent.ui.home.item

import com.app.myrickshawparent.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class HomeResponse(

    @field:SerializedName("data")
    val data: Data,

    ) : BaseResponse() {

    data class School(

        @field:SerializedName("school_name")
        val schoolName: String = "",

        @field:SerializedName("id")
        val id: Int = 0,
    )

    data class Route(

        @field:SerializedName("route_id")
        val routeId: Int = 0,

        @field:SerializedName("route_type")
        val routeType: Int = 0,

        @field:SerializedName("children_stop_id")
        val childrenStopId: Int = 0,

        @field:SerializedName("vehicle_number")
        val vehicleNumber: String = "",

        @field:SerializedName("school_latitude")
        val schoolLatitude: String = "",

        @field:SerializedName("school_longitude")
        val schoolLongitude: String = "",

        @field:SerializedName("school_address")
        val schoolAddress: String = "",

        @field:SerializedName("destination")
        val destination: Destination? = null,

        @field:SerializedName("route_stops")
        val routeStops: MutableList<RouteStopsItem>,

        )

    data class Data(
        @field:SerializedName("childrens")
        val childrens: List<ChildrensItem>,
    )

    data class Destination(

        @field:SerializedName("latitude")
        val latitude: String = "",

        @field:SerializedName("longitude")
        val longitude: String = "",
    )


    data class RouteStopsItem(

        @field:SerializedName("route_id")
        val routeId: Int = 0,

        @field:SerializedName("address")
        val address: String = "",

        @field:SerializedName("status")
        val status: String = "",

        @field:SerializedName("latitude")
        val latitude: String = "",

        @field:SerializedName("id")
        val id: Int = 0,

        @field:SerializedName("longitude")
        val longitude: String = "",
    )

    data class ChildrensItem(

        @field:SerializedName("standard")
        val standard: String = "",

        @field:SerializedName("status_label")
        val statusLabel: String = "",

        @field:SerializedName("bus_status")
        var busStatus: Int = 0,

        @field:SerializedName("full_name")
        val fullName: String = "",

        @field:SerializedName("route")
        val route: Route,

        @field:SerializedName("gender")
        val gender: String = "",

        @field:SerializedName("school")
        val school: School,

        @field:SerializedName("profile")
        val profile: String = "",

        @field:SerializedName("last_name")
        val lastName: String = "",

        @field:SerializedName("id")
        val id: Int = 0,

        @field:SerializedName("first_name")
        val firstName: String = "",

        @field:SerializedName("status")
        val status: Int = 0,
    )

}
