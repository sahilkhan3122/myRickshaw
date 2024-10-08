package com.app.myrickshawparent.ui.splash

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.BuildConfig
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.SPLASH
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
class SplashViewModel @Inject constructor(val context: Context) : BaseViewModel() {

    @Inject
    lateinit var splashscreenRepository: SplashscreenRepository

    private val splashMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var splashStateFlow: StateFlow<ResponseData<Any>> = splashMutableStateFlow

    fun splashApi() {
        if (context.isNetworkAvailable()) {
            viewModelScope.launch {
                val fullUrl = SPLASH.plus(BuildConfig.VERSION_NAME)

                splashscreenRepository.getSplashData(fullUrl).onStart {
                    splashMutableStateFlow.value = ResponseData.Loading()
                }.catch {
                    splashMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                }.collect {
                    splashMutableStateFlow.value = it
                }
            }
        } else {
            splashMutableStateFlow.value =
                ResponseData.InternetConnection(context.resources.getString(R.string.noInternet))
        }
    }
}