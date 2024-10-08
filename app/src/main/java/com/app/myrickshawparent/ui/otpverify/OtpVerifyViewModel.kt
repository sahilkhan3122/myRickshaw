package com.app.myrickshawparent.ui.otpverify

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.Param.MOBILE_NUMBER
import com.app.myrickshawparent.network.domain.ApiObject.Param.OTP
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.network.utility.Result
import com.app.myrickshawparent.util.failMsg
import com.app.myrickshawparent.util.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpVerifyViewModel @Inject constructor(val context: Context) : BaseViewModel() {

    var mobileNumber = ""

    @Inject
    lateinit var otpVerifyRepository: OtpVerifyRepository

    private val otpVerifyMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var otpVerifyStateFlow : StateFlow<ResponseData<Any>> = otpVerifyMutableStateFlow


    fun otpVerifyApi(resultOtp: String, defaultOtp: String) {
        if (context.isNetworkAvailable()) {
            when (val checkValidation =
                checkValidation.verifyOtpValidation(
                    resultOtp,
                    defaultOtp
                )) {
                is Result.Error -> {
                    otpVerifyMutableStateFlow.value = ResponseData.Error(
                        data = checkValidation.data,
                        error = checkValidation.error
                    )
                }

                is Result.Success -> {
                    viewModelScope.launch {
                        val hashMap = HashMap<String, String>()
                        hashMap[MOBILE_NUMBER] = mobileNumber
                        hashMap[OTP] = resultOtp
                        otpVerifyRepository.getOtpVerifyData(hashMap).onStart {
                            otpVerifyMutableStateFlow.value = ResponseData.Loading()
                        }.catch {
                            otpVerifyMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                        }.collect {
                            otpVerifyMutableStateFlow.value = it
                        }
                    }
                }
            }
        } else {
            otpVerifyMutableStateFlow.value =
                ResponseData.InternetConnection(context.resources.getString(R.string.noInternet))
        }
    }


    /**
     * resend otp
     */

    fun resendOtpApi() {
        if (context.isNetworkAvailable()) {
            viewModelScope.launch {
                val hashMap = HashMap<String, String>()
                hashMap[MOBILE_NUMBER] = mobileNumber

                otpVerifyRepository.getResendOtpData(hashMap).onStart {
                    otpVerifyMutableStateFlow.value = ResponseData.Loading()
                }.catch {
                    otpVerifyMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                }.collect {
                    otpVerifyMutableStateFlow.value = it
                }
            }
        } else {
            otpVerifyMutableStateFlow.value =
                ResponseData.InternetConnection(context.resources.getString(R.string.noInternet))
        }
    }
}