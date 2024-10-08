package com.app.myrickshawparent.ui.forgotpass

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseActivity
import com.app.myrickshawparent.base.BaseResponse
import com.app.myrickshawparent.databinding.ActivityForgotPasswordBinding
import com.app.myrickshawparent.network.domain.ApiObject
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.otpverify.OtpVerifyActivity
import com.app.myrickshawparent.util.applyFocusChangeTextColor
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.myClick
import com.app.myrickshawparent.util.printLog
import com.app.myrickshawparent.util.showToastMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    override val layoutId: Int
        get() = R.layout.activity_forgot_password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets =
                insets.getInsets(WindowInsetsCompat.Type.ime()) // Handle keyboard insets
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }
        initFun()
    }

    private fun initFun() {
        forgotPasswordViewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        binding.viewModel = forgotPasswordViewModel
        binding.apply {
            header.tvTittle.text = getString(R.string.forgotPasswordTitle)
            header.mcvArrow myClick {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }
            tvMessage.setLineSpacing(10f, 1.2f)
            edtNumber.applyFocusChangeTextColor()
        }

        lifecycleScope.launch {
            forgotPasswordViewModel.forgotStateFlow.collect {
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
                        if (it.data is ForgotPasswordResponse) {
                            if (it.data.status) {
                                showToastMessage(it.data.message, true)
                                val intent = Intent(
                                    this@ForgotPasswordActivity,
                                    OtpVerifyActivity::class.java
                                )
                                intent.putExtra(
                                    "number",
                                    binding.edtNumber.text.toString().trim()
                                )
                                intent.putExtra(
                                    "otp",
                                    it.data.data.otp.toString()
                                )
                                intent.putExtra(
                                    "isAutofill",
                                    it.data.data.isAutoFill
                                )
                                startActivity(intent)
                                finish()
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
}