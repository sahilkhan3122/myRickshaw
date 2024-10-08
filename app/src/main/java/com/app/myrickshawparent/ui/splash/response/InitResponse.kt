package com.app.myrickshawparent.ui.splash.response

import com.app.myrickshawparent.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class InitResponse(

    @field:SerializedName("data")
	val data: Data,

    ):BaseResponse()
{
	data class Data(

		@field:SerializedName("isforceUpdate")
		val isforceUpdate: Int? = null,

		@field:SerializedName("isMaintanance")
		val isMaintanance: Int? = null,

		@field:SerializedName("isUpdate")
		val isUpdate: Int? = null,

		@field:SerializedName("isState")
		val isState: Int = 0

	)
}


