package com.app.myrickshawparent.ui.notification.response

import com.google.gson.annotations.SerializedName

data class NotificationItem(

    @SerializedName("id")
    val id: String = "",

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("created")
    val date: String,
)
