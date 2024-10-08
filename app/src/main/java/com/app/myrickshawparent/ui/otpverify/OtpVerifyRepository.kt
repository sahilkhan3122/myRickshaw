package com.app.myrickshawparent.ui.otpverify

import android.content.Context
import com.app.myrickshawparent.base.BaseResponse
import com.app.myrickshawparent.network.domain.ApiInterface
import com.app.myrickshawparent.network.domain.getResponseResult
import com.app.myrickshawparent.network.utility.Common.OtpType.TYPE_RESEND_OTP
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.forgotpass.ForgotPasswordResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

open class OtpVerifyRepository @Inject constructor(
    var apiInterface: ApiInterface,
    private var context: Context,
) {

    suspend fun getOtpVerifyData(hashMap: HashMap<String, String>): Flow<ResponseData<ForgotPasswordResponse>> {
        return flow {
            val otpVerifyResponse = apiInterface.otpVerifyApi(hashMap)
            emit(getResponseResult(otpVerifyResponse, context))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getResendOtpData(hashMap: HashMap<String, String>): Flow<ResponseData<ForgotPasswordResponse>> {
        return flow {
            val resendOtpResponse = apiInterface.resendOtpApi(hashMap)
            emit(getResponseResult(resendOtpResponse, context, TYPE_RESEND_OTP))
        }.flowOn(Dispatchers.IO)
    }
}