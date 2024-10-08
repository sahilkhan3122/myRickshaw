package com.app.myrickshawparent.ui.setting.item

import com.app.myrickshawparent.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class LogoutResponse(

	@field:SerializedName("errors")
	val errors: List<Any?>? = null,

):BaseResponse()
