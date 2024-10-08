package com.app.myrickshawparent.ui.mychild

import android.content.Context
import com.app.myrickshawparent.network.domain.ApiInterface
import com.app.myrickshawparent.network.domain.getResponseResult
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.mychild.response.ChildrenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MyChildRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val context: Context,
) {
    suspend fun getChildData(hashMap: HashMap<String, String>): Flow<ResponseData<ChildrenResponse>> {
        return flow {
            val childResponse = apiInterface.childrenApi(hashMap)
            emit(getResponseResult(childResponse, context))
        }.flowOn(Dispatchers.IO)
    }
}