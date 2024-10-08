package com.app.myrickshawparent.ui.changepassword

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.Param.CONFIRM_PASS
import com.app.myrickshawparent.network.domain.ApiObject.Param.NEW_PASS
import com.app.myrickshawparent.network.domain.ApiObject.Param.OLD_PASS
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
class ChangePasswordViewModel @Inject constructor(val context: Context) : BaseViewModel() {

    @Inject
    lateinit var changePasswordRepository: ChangePasswordRepository

    var oldPass = ObservableField("")
    var newPass = ObservableField("")
    var confirmPass = ObservableField("")

    private val changePassMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var changePassStateFlow : StateFlow<ResponseData<Any>> = changePassMutableStateFlow

    fun changePasswordApi() {
        when (val checkValidation =
            checkValidation.changePassValidation(
                oldPass.get().toString().trim(),
                newPass.get().toString().trim(),
                confirmPass.get().toString().trim(),
            )) {
            is Result.Error -> {
                changePassMutableStateFlow.value = ResponseData.Error(
                    data = checkValidation.data,
                    error = checkValidation.error
                )
            }
            is Result.Success -> {
                viewModelScope.launch {
                    val hashMap = HashMap<String, String>()
                    hashMap[OLD_PASS] = oldPass.get().toString().trim()
                    hashMap[NEW_PASS] = newPass.get().toString().trim()
                    hashMap[CONFIRM_PASS] = confirmPass.get().toString().trim()

                    changePasswordRepository.getChangePassword(hashMap).onStart {
                        changePassMutableStateFlow.value = ResponseData.Loading()
                    }.catch {
                        changePassMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                    }.collect {
                        changePassMutableStateFlow.value = it
                    }
                }
            }
        }
    }
}