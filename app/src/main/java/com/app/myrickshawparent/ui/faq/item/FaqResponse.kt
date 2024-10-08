package com.app.myrickshawparent.ui.faq.item

import com.app.myrickshawparent.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class FaqResponse(
    @SerializedName("data")
    val faqList : MutableList<FaqItem> = arrayListOf()
) : BaseResponse() {
}