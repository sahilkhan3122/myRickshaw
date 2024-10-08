package com.app.myrickshawparent.ui.forgotpass

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.Param.MOBILE_NUMBER
import com.app.myrickshawparent.network.utility.CheckValidation
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
class ForgotPasswordViewModel @Inject constructor(val context: Context) : BaseViewModel() {

    @Inject
    lateinit var forgotPasswordRepository: ForgotPasswordRepository

    var number = ObservableField("")

    private val forgotMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var forgotStateFlow : StateFlow<ResponseData<Any>> = forgotMutableStateFlow

    fun forgotApi() {
        when (val checkValidation =
            CheckValidation(context).forgotValidation(
                number.get().toString().trim()
            )) {
            is Result.Error -> {
                forgotMutableStateFlow.value = ResponseData.Error(
                    data = checkValidation.data,
                    error = checkValidation.error
                )
            }

            is Result.Success -> {
                viewModelScope.launch {
                    val hashMap = HashMap<String, String>()
                    hashMap[MOBILE_NUMBER] = number.get().toString().trim()
                    forgotPasswordRepository.getForgotData(hashMap).onStart {
                        forgotMutableStateFlow.value = ResponseData.Loading()
                    }.catch {
                        forgotMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                    }.collect {
                        forgotMutableStateFlow.value = it
                    }
                }
            }
        }

    }
}