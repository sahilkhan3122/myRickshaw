package com.app.myrickshawparent.ui.changepassword

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseActivity
import com.app.myrickshawparent.base.BaseResponse
import com.app.myrickshawparent.databinding.ActivityChangePasswordBinding
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.util.applyFocusChangeTextColor
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.myClick
import com.app.myrickshawparent.util.setDrawableEndClickListener
import com.app.myrickshawparent.util.showToastMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding>() {
    lateinit var changePasswordViewModel: ChangePasswordViewModel
    override val layoutId: Int
        get() = R.layout.activity_change_password

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

        changePasswordViewModel = ViewModelProvider(this)[ChangePasswordViewModel::class.java]
        binding.viewModel = changePasswordViewModel
        initFun()
        lifecycleScope.launch {
            changePasswordViewModel.changePassStateFlow.collect {
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
                                showToastMessage(it.data.message, true)
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

    private fun initFun() {
        binding.apply {
            header.tvTittle.text = getString(R.string.change_password)
            edtOldPassword.setDrawableEndClickListener()
            edtOldPassword.applyFocusChangeTextColor()
            edtNewPassword.applyFocusChangeTextColor()
            edtConfirmPassword.applyFocusChangeTextColor()
            btnSubmit myClick {
                viewModel?.changePasswordApi()
            }
            header.mcvArrow myClick {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }
        }
    }
}