package com.app.myrickshawparent.myfirebaseservice.response

import com.google.gson.annotations.SerializedName

data class FirebaseResponse(

    @SerializedName("socket_type")
    val socketType: String = "",

    @SerializedName("route_type")
    val routeType: String = "",

    @SerializedName("id")
    val id: String = ""
) {
}