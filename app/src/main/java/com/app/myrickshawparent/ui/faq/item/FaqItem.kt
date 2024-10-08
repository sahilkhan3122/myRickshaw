package com.app.myrickshawparent.ui.faq.item

import com.google.gson.annotations.SerializedName

data class FaqItem(
    @SerializedName("id") val id: String = "",
    @SerializedName("question") val question: String = "",
    @SerializedName("answer") val answer: String = "",
)
