package com.app.myrickshawparent.ui.track.response

import com.app.myrickshawparent.myfirebaseservice.response.FirebaseResponse
import com.google.gson.annotations.SerializedName

data class PopupResponse(

    @SerializedName("title")
    var title: String = "",

    @SerializedName("description")
    var description: String = "",

    var firebaseResponse: FirebaseResponse

) {


}