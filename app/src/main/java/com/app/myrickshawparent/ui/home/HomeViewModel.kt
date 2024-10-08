package com.app.myrickshawparent.ui.home

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
class HomeViewModel @Inject constructor(
    val context: Context,
    private val homeListRepository: HomeListRepository,
) : BaseViewModel() {

    private val homeMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var homeStateFlow: StateFlow<ResponseData<Any>> = homeMutableStateFlow

    init {
        homeApi("")
    }

    fun homeApi(searchByName: String) {
        if (context.isNetworkAvailable()) {
            viewModelScope.launch {
                val hashMap = HashMap<String, String>()
                hashMap[SEARCH] = searchByName
                homeListRepository.getHomeListData(hashMap).onStart {
                    homeMutableStateFlow.value = ResponseData.Loading()
                }.catch {
                    homeMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                }.collect {
                    homeMutableStateFlow.value = it
                }
            }
        } else {
            homeMutableStateFlow.value =
                ResponseData.InternetConnection(context.resources.getString(R.string.noInternet))
        }
    }
}