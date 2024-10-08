package com.app.myrickshawparent.ui.notification.response

import com.app.myrickshawparent.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class NotificationResponse(

    @SerializedName("data")
    val notificationList: MutableList<NotificationItem>,

    ) : BaseResponse() {

}