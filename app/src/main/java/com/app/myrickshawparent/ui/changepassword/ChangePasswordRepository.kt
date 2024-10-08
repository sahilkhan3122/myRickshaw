package com.app.myrickshawparent.ui.changepassword

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

class ChangePasswordRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val context: Context,
) {

    suspend fun getChangePassword(hashMap: HashMap<String, String>): Flow<ResponseData<BaseResponse>> {
        return flow {
            val changePassResponse = apiInterface.updatePasswordApi(hashMap)
            emit(getResponseResult(changePassResponse, context))
        }.flowOn(Dispatchers.IO)
    }
}