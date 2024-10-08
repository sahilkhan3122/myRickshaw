package com.app.myrickshawparent.ui.faq

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.ApiEndPoint.FAQS
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
class FaqViewModel @Inject constructor(val context: Context, private val faqRepository: FaqRepository) :
    BaseViewModel() {

    private val faqMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var faqStateFlow : StateFlow<ResponseData<Any>> = faqMutableStateFlow

    init {
        faqApi()
    }

    private fun faqApi() {
        if (context.isNetworkAvailable()) {
            viewModelScope.launch {
                faqRepository.getFaqData(FAQS).onStart {
                    faqMutableStateFlow.value = ResponseData.Loading()
                }.catch {
                    faqMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                }.collect {
                    faqMutableStateFlow.value = it
                }
            }
        } else {
            faqMutableStateFlow.value =
                ResponseData.InternetConnection(context.resources.getString(R.string.noInternet))
        }
    }

}