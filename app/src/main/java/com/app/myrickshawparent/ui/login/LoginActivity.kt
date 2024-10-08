package com.app.myrickshawparent.ui.login

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseActivity
import com.app.myrickshawparent.databinding.ActivityLoginBinding
import com.app.myrickshawparent.network.utility.Common.Type.isFromOtp
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.dialog.CancelDialog
import com.app.myrickshawparent.ui.forgotpass.ForgotPasswordActivity
import com.app.myrickshawparent.ui.login.response.LoginResponse
import com.app.myrickshawparent.ui.main.MainActivity
import com.app.myrickshawparent.util.applyFocusChangeTextColor
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.isPermission
import com.app.myrickshawparent.util.myClick
import com.app.myrickshawparent.util.openSetting
import com.app.myrickshawparent.util.printLog
import com.app.myrickshawparent.util.showToastMessage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_login

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets =
                insets.getInsets(WindowInsetsCompat.Type.ime()) // Handle keyboard insets
            v.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.viewModel = loginViewModel

        binding.apply {
            tvForgetPass myClick {
                val intent = Intent(
                    applicationContext,
                    ForgotPasswordActivity::class.java
                )
                startActivity(intent)
            }
            tvMessage.setLineSpacing(10f, 1.2f)
            edtNumber.applyFocusChangeTextColor()
            edtpassword.applyFocusChangeTextColor()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionHandle(onPermissionGranted = {
                if (it) {
                    getToken()
                } else {
                    showToastMessage(resources.getString(R.string.allowNotification))
                }
            }, onPermissionDenied = {
                CancelDialog(
                    context = this,
                    icon = R.mipmap.ic_richshaw,
                    title = getString(R.string.setting),
                    description = getString(R.string.setting_des_notification),
                    okButtonText = getString(R.string.ok),
                    cancelButtonText = getString(R.string.cancel),
                    onOkBtnClick = {
                        openSetting()
                    },
                    isShowCancelButton = true
                ).showDialog()
            })
        }


        lifecycleScope.launch {
            loginViewModel.loginStateFlow.collect {
                when (it) {
                    is ResponseData.Empty -> {}
                    is ResponseData.Error -> {
                        loadingDialog.dismiss()
                        showToastMessage(it.error)
                    }

                    is ResponseData.InternetConnection -> {
                        loadingDialog.dismiss()
                    }

                    is ResponseData.Loading -> {
                        loadingDialog.show()
                    }

                    is ResponseData.Success -> {
                        loadingDialog.dismiss()
                        if (it.type == isFromOtp) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (isPermission(Manifest.permission.POST_NOTIFICATIONS)) {
                                    getToken()
                                } else {
                                    requestNotificationPermission()
                                }
                            } else {
                                getToken()
                            }
                        } else if (it.data is LoginResponse) {
                            if (it.data.status) {
                                withContext(Dispatchers.IO) {
                                    saveUserData(it.data)
                                }
                                withContext(Dispatchers.Main) {
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            MainActivity::class.java
                                        )
                                    )
                                    finishAffinity()
                                }
                                showToastMessage(it.data.message, true)
                            } else {
                                showToastMessage(it.data.message)
                            }
                        }
                    }

                    is ResponseData.Exception -> {
                        loadingDialog.dismiss()
                        exceptionHandle(ExceptionData(code = it.code), dataStore = dataStore)
                    }

                }
            }
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            printLog("Device token :-", task.result)
            val token = task.result
            loginViewModel.callLoginApi(token)
        })
    }


    private suspend fun saveUserData(data: LoginResponse) {
        dataStore.setBooleanData(dataStore.isLogin, true)
        dataStore.setStringData(dataStore.token, data.data?.token.toString())
        dataStore.setStringData(dataStore.fullName, data.data?.fullName.toString())
        dataStore.setStringData(dataStore.email, data.data?.email.toString())
        dataStore.setStringData(dataStore.phone, data.data?.phoneNumber.toString())
        dataStore.setStringData(dataStore.profile, data.data?.userProfile.toString())
    }
}