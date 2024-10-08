package com.app.myrickshawparent.ui.setting

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.LOGOUT
import com.app.myrickshawparent.network.utility.ResponseData
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
class SettingViewModel @Inject constructor(val context: Context) : BaseViewModel() {

    @Inject
    lateinit var settingRepository: SettingRepository

    private val logoutMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var logoutStateFlow : StateFlow<ResponseData<Any>> = logoutMutableStateFlow

    /**
     * Logout account api
     */
    fun logoutApi() {
        if (context.isNetworkAvailable()) {
            viewModelScope.launch {
                settingRepository.getLogoutData(LOGOUT).onStart {
                    logoutMutableStateFlow.value = ResponseData.Loading()
                }.catch {
                    logoutMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                }.collect {
                    logoutMutableStateFlow.value = it
                }
            }
        } else {
            logoutMutableStateFlow.value =
                ResponseData.InternetConnection(context.resources.getString(R.string.noInternet))
        }
    }

}