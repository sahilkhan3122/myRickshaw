package com.app.myrickshawparent.ui.mychild

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.Param.SEARCH
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
class ChildViewModel @Inject constructor(
    val context: Context,
    val childRepository: MyChildRepository,
) : BaseViewModel() {

    private val childMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var childStateFlow: StateFlow<ResponseData<Any>> = childMutableStateFlow

    init {
        childApi("")
    }

    fun childApi(searchByName: String) {
        if (context.isNetworkAvailable()) {
            viewModelScope.launch {
                val hashMap = HashMap<String, String>()
                hashMap[SEARCH] = searchByName
                childRepository.getChildData(hashMap).onStart {
                    childMutableStateFlow.value = ResponseData.Loading()
                }.catch {
                    childMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                }.collect {
                    childMutableStateFlow.value = it
                }
            }
        } else {
            childMutableStateFlow.value =
                ResponseData.InternetConnection(context.resources.getString(R.string.noInternet))
        }
    }
}