package com.app.myrickshawparent.ui.track.response

import com.google.gson.annotations.SerializedName

data class EstimateTimeResponse(
    @SerializedName("etaMessage")
    val etaMessage: String,
) {}
