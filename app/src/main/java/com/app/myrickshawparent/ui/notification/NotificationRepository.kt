package com.app.myrickshawparent.ui.notification

import android.content.Context
import com.app.myrickshawparent.network.domain.ApiInterface
import com.app.myrickshawparent.network.domain.getResponseResult
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.notification.response.NotificationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val context: Context,
) {

    suspend fun getNotificationData(uri: String): Flow<ResponseData<NotificationResponse>> {
        return flow {
            val loginResponse = apiInterface.getNotificationApi(uri)
            emit(getResponseResult(loginResponse, context))
        }.flowOn(Dispatchers.IO)
    }


}