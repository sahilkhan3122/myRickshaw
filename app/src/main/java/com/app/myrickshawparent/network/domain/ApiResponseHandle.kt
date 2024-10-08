package com.app.myrickshawparent.network.domain

import android.content.Context
import com.app.myrickshawparent.R
import com.app.myrickshawparent.network.utility.CheckValidation
import com.app.myrickshawparent.network.utility.ResponseData
import retrofit2.Response

fun <T> getResponseResult(
    response: Response<T>,
    context: Context,
    type: String = "",
): ResponseData<T> {
    return if (response.code() == 200) {
        if (response.isSuccessful) {
            ResponseData.Success(response.body(), type)
        } else {
            ResponseData.Error(response.body(), context.getString(R.string.wrong))
        }
    } else if (response.code() == 401) {
        ResponseData.Exception(
            response.body(),
            response.code(),
            type,
            context.getString(R.string.unauthorized)
        )
    } else {
        ResponseData.Error(
            response.body(),
            CheckValidation(context).networkError(code = response.code())
        )
    }
}

