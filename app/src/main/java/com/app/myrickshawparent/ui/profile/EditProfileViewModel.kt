package com.app.myrickshawparent.ui.profile

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.Param.FULL_NAME
import com.app.myrickshawparent.network.domain.ApiObject.Param.PROFILE
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.network.utility.Result
import com.app.myrickshawparent.util.failMsg
import com.app.myrickshawparent.util.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(val context: Context) : BaseViewModel() {

    @Inject
    lateinit var editProfileRepository: EditProfileRepository

    var profilePath = ""

    var name = ObservableField("")

    private val editProMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var editProStateFlow : StateFlow<ResponseData<Any>> = editProMutableStateFlow

    fun editProfileApi() {
        if (context.isNetworkAvailable()) {
            when (val checkValidation =
                checkValidation.profileValidation(name.get().toString().trim())) {
                is Result.Error -> {
                    editProMutableStateFlow.value = ResponseData.Error(
                        data = checkValidation.data,
                        error = checkValidation.error
                    )
                }

                is Result.Success -> {

                    viewModelScope.launch {
                        val hashMap = HashMap<String, RequestBody>()

                        val body =
                            if (!profilePath.contains("https") && !profilePath.contains("http")) {
                                val file = File(profilePath)
                                val requestFile =
                                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

                                MultipartBody.Part.createFormData(PROFILE, file.name, requestFile)
                            } else {
                                MultipartBody.Part.createFormData(
                                    PROFILE,
                                    "",
                                    RequestBody.create(null, "")
                                )
                            }


                        val fullName =
                            RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                name.get().toString().trim()
                            )
                        hashMap[FULL_NAME] = fullName

                        editProfileRepository.getEditProfile(body, hashMap).onStart {
                            editProMutableStateFlow.value = ResponseData.Loading()
                        }.catch { exception ->
                            editProMutableStateFlow.value =
                                ResponseData.Error(error = context.failMsg(error = exception.toString()))
                        }.collect {
                            editProMutableStateFlow.value = it
                        }
                    }
                }
            }
        } else {
            editProMutableStateFlow.value =
                ResponseData.Error(error = context.resources.getString(R.string.noInternet))
        }
    }
}