package com.app.myrickshawparent.ui.forgotpass

import com.app.myrickshawparent.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(

	@field:SerializedName("data")
	val data: Data,

):BaseResponse(){
	data class Data(

		@field:SerializedName("is_auto_fill")
		val isAutoFill: Boolean,

		@field:SerializedName("otp")
		val otp: Int
	)

}

