package com.app.myrickshawparent.ui.resetpassword

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseActivity
import com.app.myrickshawparent.base.BaseResponse
import com.app.myrickshawparent.databinding.ActivityResetPasswordBinding
import com.app.myrickshawparent.network.utility.Common.IntentKey.NUMBER
import com.app.myrickshawparent.network.utility.Common.IntentKey.OTP
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.util.applyFocusChangeTextColor
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.myClick
import com.app.myrickshawparent.util.showToastMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordActivity : BaseActivity<ActivityResetPasswordBinding>() {
    private lateinit var resetPasswordViewModel: ResetPasswordViewModel
    override val layoutId: Int
        get() = R.layout.activity_reset_password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime()) // Handle keyboard insets
             v.setPadding(systemBars.left, systemBars.top, systemBars.right,  maxOf(systemBars.bottom, imeInsets.bottom))
            insets
        }
        resetPasswordViewModel = ViewModelProvider(this)[ResetPasswordViewModel::class.java]
        binding.viewModel = resetPasswordViewModel
        setHeader()
        getIntentData()
        initFun()
        apiCall()
    }

    private fun apiCall() {
        lifecycleScope.launch {
            /**otp verify api*/
            resetPasswordViewModel.resetPassStateFlow.collect {
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
                        if (it.data is BaseResponse) {
                            if (it.data.status) {
                                showToastMessage(it.data.message,true)
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

    private fun getIntentData() {
        if (intent != null) {
            binding.viewModel?.number = intent.getStringExtra(NUMBER).toString()
            binding.viewModel?.otp = intent.getStringExtra(OTP).toString()
        }
    }

    private fun setHeader() {
        binding.apply {
            header.tvTittle.text = getString(R.string.reset_password)
            edtNewPassword.applyFocusChangeTextColor()
            edtConfirmPassword.applyFocusChangeTextColor()
            header.mcvArrow myClick {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }}
    }

    private fun initFun() {
        binding.apply {
            btnResetPass myClick {
                binding.viewModel?.resetPasswordApi()

            }
        }
    }
}