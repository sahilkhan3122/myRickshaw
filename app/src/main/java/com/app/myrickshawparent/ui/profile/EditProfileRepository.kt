package com.app.myrickshawparent.ui.profile

import android.content.Context
import com.app.myrickshawparent.network.domain.ApiInterface
import com.app.myrickshawparent.network.domain.getResponseResult
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.login.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

open class EditProfileRepository @Inject constructor(
    var apiInterface: ApiInterface,
    private var context: Context,
) {

    suspend fun getEditProfile(
        image: MultipartBody.Part,
        hashMap: HashMap<String, RequestBody>,
    ): Flow<ResponseData<LoginResponse>> {
        return flow {
            val getEditProfileResponse = apiInterface.editProfileApi(image, hashMap)
            emit(getResponseResult(getEditProfileResponse, context))
        }.flowOn(Dispatchers.IO)
    }

}