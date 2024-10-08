package com.app.myrickshawparent.ui.contactus

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

class ContactUsRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val context: Context,
) {
    suspend fun getContactUs(hashMap: HashMap<String, String>): Flow<ResponseData<BaseResponse>> {
        return flow {
            val contactUsResponse = apiInterface.contactUsApi(hashMap)
            emit(getResponseResult(contactUsResponse, context))
        }.flowOn(Dispatchers.IO)
    }
}