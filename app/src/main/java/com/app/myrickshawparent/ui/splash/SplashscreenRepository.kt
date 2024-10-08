package com.app.myrickshawparent.ui.splash

import android.content.Context
import com.app.myrickshawparent.network.domain.ApiInterface
import com.app.myrickshawparent.network.domain.getResponseResult
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.splash.response.InitResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

open class SplashscreenRepository @Inject constructor(
    private var apiInterface: ApiInterface,
    val context: Context,
) {
    // GET request to fetch splash data, or any other data based on the given URL
    suspend fun getSplashData(url: String): Flow<ResponseData<InitResponse>> {
        return flow {
            val splashResponse = apiInterface.initApi(url) // Change this to your GET API method
            emit(getResponseResult(splashResponse, context))
        }.flowOn(Dispatchers.IO)
    }
}