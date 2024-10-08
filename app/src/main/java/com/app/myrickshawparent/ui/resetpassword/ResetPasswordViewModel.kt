package com.app.myrickshawparent.ui.resetpassword

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.Param.MOBILE_NUMBER
import com.app.myrickshawparent.network.domain.ApiObject.Param.OTP
import com.app.myrickshawparent.network.domain.ApiObject.Param.PASSWORD
import com.app.myrickshawparent.network.domain.ApiObject.Param.PASSWORD_CONFIRM
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.network.utility.Result
import com.app.myrickshawparent.util.failMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ResetPasswordViewModel @Inject constructor(val context: Context) : BaseViewModel() {

    var number: String = ""
    var otp: String = ""

    @Inject
    lateinit var resetPasswordRepository: ResetPasswordRepository

    var newPass = ObservableField("")
    var confirmPass = ObservableField("")

    private val resetPassMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var resetPassStateFlow : StateFlow<ResponseData<Any>> = resetPassMutableStateFlow

    fun resetPasswordApi() {
        when (val checkValidation =
            checkValidation.resetPasswordValidation(
                newPass.get().toString().trim(),
                confirmPass.get().toString().trim()
            )) {
            is Result.Error -> {
                resetPassMutableStateFlow.value = ResponseData.Error(
                    data = checkValidation.data,
                    error = checkValidation.error
                )
            }

            is Result.Success -> {
                viewModelScope.launch {
                    val hashMap = HashMap<String, String>()
                    hashMap[MOBILE_NUMBER] = number
                    hashMap[PASSWORD] = newPass.get().toString().trim()
                    hashMap[PASSWORD_CONFIRM] = confirmPass.get().toString().trim()
                    hashMap[OTP] = otp
                    resetPasswordRepository.getResetPasswordData(hashMap).onStart {
                        resetPassMutableStateFlow.value = ResponseData.Loading()
                    }.catch {
                        resetPassMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                    }.collect {
                        resetPassMutableStateFlow.value = it
                    }
                }
            }
        }
    }
}