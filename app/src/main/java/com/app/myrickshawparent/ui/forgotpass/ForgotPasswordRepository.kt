package com.app.myrickshawparent.ui.forgotpass

import android.content.Context
import com.app.myrickshawparent.base.BaseResponse
import com.app.myrickshawparent.network.domain.ApiInterface
import com.app.myrickshawparent.network.domain.getResponseResult
import com.app.myrickshawparent.network.utility.ResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ForgotPasswordRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val context: Context,
) {
    suspend fun getForgotData(hashMap: HashMap<String, String>): Flow<ResponseData<ForgotPasswordResponse>> {
        return flow {
            val forgotResponse = apiInterface.forgotApi(hashMap)
            emit(getResponseResult(forgotResponse, context))
        }.flowOn(Dispatchers.IO)
    }

}