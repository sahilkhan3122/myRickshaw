package com.app.myrickshawparent.ui.home

import android.content.Context
import com.app.myrickshawparent.network.domain.ApiInterface
import com.app.myrickshawparent.network.domain.getResponseResult
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.home.item.HomeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeListRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val context: Context,
) {
    suspend fun getHomeListData(hashMap: HashMap<String, String>): Flow<ResponseData<HomeResponse>> {
        return flow {
            val homeResponse = apiInterface.homeApi(hashMap)
            emit(getResponseResult(homeResponse, context))
        }.flowOn(Dispatchers.IO)
    }
}