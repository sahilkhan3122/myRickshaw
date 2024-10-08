package com.app.myrickshawparent.ui.login.response

import com.app.myrickshawparent.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("data")
    val data: Data? = null,

    ) : BaseResponse() {

    data class Data(

        @field:SerializedName("token")
        val token: String = "",

        @field:SerializedName("full_name")
        val fullName: String = "",

        @field:SerializedName("updated_at")
        val updatedAt: String = "",

        @field:SerializedName("fcm_token")
        val fcmToken: String = "",

        @field:SerializedName("last_name")
        val lastName: String = "",

        @field:SerializedName("profile_picture")
        val profilePicture: String = "",

        @field:SerializedName("phone_number")
        val phoneNumber: String = "",

        @field:SerializedName("device_type")
        val deviceType: String = "",

        @field:SerializedName("id")
        val id: Int = 0,

        @field:SerializedName("user_profile")
        val userProfile: String = "",

        @field:SerializedName("first_name")
        val firstName: String = "",

        @field:SerializedName("email")
        val email: String = "",
    )

}




