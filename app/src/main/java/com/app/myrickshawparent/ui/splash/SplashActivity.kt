package com.app.myrickshawparent.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseActivity
import com.app.myrickshawparent.databinding.ActivitySplashBinding
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.dialog.InitStateDialog
import com.app.myrickshawparent.ui.login.LoginActivity
import com.app.myrickshawparent.ui.main.MainActivity
import com.app.myrickshawparent.ui.splash.response.InitResponse
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.showToastMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private lateinit var splashViewModel: SplashViewModel
    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        initFun()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initFun() {
        splashViewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        binding.viewModel = splashViewModel
        splashViewModel.splashApi()
        lifecycleScope.launch {
            splashViewModel.splashStateFlow.collect {
                when (it) {
                    is ResponseData.Empty -> {}
                    is ResponseData.Error -> {
                        showToastMessage(it.error)
                    }

                    is ResponseData.InternetConnection -> {

                    }

                    is ResponseData.Loading -> {

                    }

                    is ResponseData.Success -> {
                        if (it.data is InitResponse) {
                            if (it.data.status) {
                                val isLogin: Boolean =
                                    dataStore.getBooleanData(dataStore.isLogin).first()
                                if (it.data.data.isState != 3) {
                                    manageState(it.data.message, it.data.data.isState, isLogin)
                                } else {
                                    if (!isLogin) {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                MainActivity::class.java
                                            )
                                        )
                                        finishAffinity()
                                    }
                                }
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

    private fun manageState(message: String, state: Int, login: Boolean) {
        InitStateDialog(this@SplashActivity, R.drawable.splash_img, message, state,
            onOkBtnClick = {
                when (state) {
                    0, 1 -> {
                        goToPlayStore()
                    }

                    2 -> {
                        finish()
                    }
                }
            }, onCancelBtnClick = {
                if (state == 0) {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            if (login) MainActivity::class.java else LoginActivity::class.java
                        )
                    )
                    finish()
                }
            }).showDialog()
    }

}