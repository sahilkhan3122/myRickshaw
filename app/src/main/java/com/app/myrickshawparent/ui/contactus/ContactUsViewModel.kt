package com.app.myrickshawparent.ui.contactus

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.Param.CONTACT_NUMBER
import com.app.myrickshawparent.network.domain.ApiObject.Param.MESSAGE
import com.app.myrickshawparent.network.domain.ApiObject.Param.NAME
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
class ContactUsViewModel @Inject constructor(val context: Context) : BaseViewModel() {


    @Inject
    lateinit var contactUsRepository: ContactUsRepository

    var name = ObservableField("")
    var number = ObservableField("")
    var message = ObservableField("")

    private val contactUsMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var contactUSStateFlow : StateFlow<ResponseData<Any>> = contactUsMutableStateFlow

    fun contactUsApi() {
        when (val checkValidation =
            checkValidation.contactUsValidation(
                name.get().toString().trim(),
                number.get().toString().trim(),
                message.get().toString().trim(),
            )) {
            is Result.Error -> {
                contactUsMutableStateFlow.value = ResponseData.Error(
                    data = checkValidation.data,
                    error = checkValidation.error
                )
            }
            is Result.Success -> {
                viewModelScope.launch {
                    val hashMap = HashMap<String, String>()
                    hashMap[NAME] = name.get().toString().trim()
                    hashMap[CONTACT_NUMBER] = number.get().toString().trim()
                    hashMap[MESSAGE] = message.get().toString().trim()

                    contactUsRepository.getContactUs(hashMap).onStart {
                        contactUsMutableStateFlow.value = ResponseData.Loading()
                    }.catch {
                        contactUsMutableStateFlow.value = ResponseData.Error(error = context.failMsg(it.message.toString()))
                    }.collect {
                        contactUsMutableStateFlow.value = it
                    }
                }
            }
        }
    }
}