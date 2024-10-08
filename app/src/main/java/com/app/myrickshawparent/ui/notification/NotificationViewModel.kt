package com.app.myrickshawparent.ui.notification

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.NOTIFICATIONS
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
class NotificationViewModel @Inject constructor(
    val context: Context,
    private val notificationRepository: NotificationRepository,
) : BaseViewModel() {

    private val nfMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var nfStateFlow : StateFlow<ResponseData<Any>> = nfMutableStateFlow

    init {
        notificationApi()
    }

    private fun notificationApi() {
        if (context.isNetworkAvailable()) {
            viewModelScope.launch {
                notificationRepository.getNotificationData(NOTIFICATIONS).onStart {
                    nfMutableStateFlow.value = ResponseData.Loading()
                }.catch {
                    nfMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                }.collect {
                    nfMutableStateFlow.value = it
                }
            }
        } else {
            nfMutableStateFlow.value =
                ResponseData.InternetConnection(context.resources.getString(R.string.noInternet))
        }
    }
}