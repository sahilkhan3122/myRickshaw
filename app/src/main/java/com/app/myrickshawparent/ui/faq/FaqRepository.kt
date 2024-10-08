package com.app.myrickshawparent.ui.faq

import android.content.Context
import com.app.myrickshawparent.network.domain.ApiInterface
import com.app.myrickshawparent.network.domain.getResponseResult
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.faq.item.FaqResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FaqRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val context: Context,
) {

    suspend fun getFaqData(url: String): Flow<ResponseData<FaqResponse>> {
        return flow {
            val loginResponse = apiInterface.faqApi(url)
            emit(getResponseResult(loginResponse, context))
        }.flowOn(Dispatchers.IO)
    }


}