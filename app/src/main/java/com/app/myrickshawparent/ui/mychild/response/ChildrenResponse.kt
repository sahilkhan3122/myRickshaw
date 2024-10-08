package com.app.myrickshawparent.ui.mychild.response

import com.app.myrickshawparent.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class ChildrenResponse(

    @field:SerializedName("data")
	val data: List<DataItem>,

    ):BaseResponse(){

	data class DataItem(

        @field:SerializedName("standard")
		val standard: String = "",

        @field:SerializedName("full_name")
		val fullName: String = "",

        @field:SerializedName("gender")
		val gender: String = "",

        @field:SerializedName("school")
		val school: School,

        @field:SerializedName("profile")
		val profile: String = "",

        @field:SerializedName("last_name")
		val lastName: String = "",

        @field:SerializedName("id")
		val id: Int = 0,

        @field:SerializedName("first_name")
		val firstName: String= ""
	)

	data class School(

		@field:SerializedName("school_name")
		val schoolName: String= "",

		@field:SerializedName("id")
		val id: Int= 0
	)
}

