package com.app.myrickshawparent.ui.setting

import android.content.Context
import com.app.myrickshawparent.network.domain.ApiInterface
import com.app.myrickshawparent.network.domain.getResponseResult
import com.app.myrickshawparent.network.utility.Common.OtpType.DELETE_ACCOUNT
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.setting.item.LogoutResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

open class SettingRepository @Inject constructor(
    private var apiInterface: ApiInterface,
    val context: Context,
) {
    // GET request to fetch splash data, or any other data based on the given URL
    suspend fun getLogoutData(url: String): Flow<ResponseData<LogoutResponse>> {
        return flow {
            val logOutResponse = apiInterface.logOutApi(url) // Change this to your GET API method
            emit(getResponseResult(logOutResponse, context))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getDeleteAccountData(hashMap: HashMap<String, String>): Flow<ResponseData<LogoutResponse>> {
        return flow {
            val deleteAccountResponse = apiInterface.deleteAccountApi(hashMap)
            emit(getResponseResult(deleteAccountResponse, context, DELETE_ACCOUNT))
        }.flowOn(Dispatchers.IO)
    }
}