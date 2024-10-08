package com.app.myrickshawparent.ui.otpverify

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseActivity
import com.app.myrickshawparent.databinding.ActivityOtpVerifyBinding
import com.app.myrickshawparent.network.utility.Common.IntentKey.ISAUTOFILL
import com.app.myrickshawparent.network.utility.Common.IntentKey.NUMBER
import com.app.myrickshawparent.network.utility.Common.IntentKey.OTP
import com.app.myrickshawparent.network.utility.Common.OtpType.TYPE_RESEND_OTP
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.forgotpass.ForgotPasswordResponse
import com.app.myrickshawparent.ui.resetpassword.ResetPasswordActivity
import com.app.myrickshawparent.util.Constants
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.gone
import com.app.myrickshawparent.util.myClick
import com.app.myrickshawparent.util.showToastMessage
import com.app.myrickshawparent.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class OtpVerifyActivity : BaseActivity<ActivityOtpVerifyBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_otp_verify

    // private var otp = "1234"
    private var resultOtp = ""
    private var isTimer = false
    private lateinit var otpVerifyViewModel: OtpVerifyViewModel

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
        otpVerifyViewModel = ViewModelProvider(this)[OtpVerifyViewModel::class.java]
        binding.viewModel = otpVerifyViewModel
        setHeader()
        initFun()
        otpAutoFill()
        timer.start()

        /*** Api call back handle*/
        lifecycleScope.launch {
            otpVerifyViewModel.otpVerifyStateFlow.collect {
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
                                if (it.type == TYPE_RESEND_OTP) {
                                    isTimer = true
                                    timer.start()
                                    binding.tvResendOTP.gone()
                                    binding.tvDidNotGetOtp.gone()
                                    binding.tvOtpCountdown.visible()
                                    Constants.OTP = it.data.data.otp.toString()
                                    otpAutoFill()
                                } else {
                                    val intent = Intent(
                                        this@OtpVerifyActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                    intent.putExtra(NUMBER, binding.viewModel?.mobileNumber)
                                    intent.putExtra(OTP, resultOtp)
                                    startActivity(intent)
                                    finish()
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

    private fun initFun() {
        if (intent != null) {
            binding.viewModel?.mobileNumber = intent.getStringExtra(NUMBER).toString()
            Constants.OTP = intent.getStringExtra(OTP).toString()
            Constants.IsAUTOFILL = intent.getStringExtra(ISAUTOFILL).toString()
        }
    }

    private var timer = object : CountDownTimer(60000, 1000) {
        @SuppressLint("DefaultLocale")
        override fun onTick(millisUntilFinished: Long) {
            val hms = String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                ),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        millisUntilFinished
                    )
                )
            )
            binding.tvOtpCountdown.text = hms
        }

        override fun onFinish() {
            binding.tvResendOTP.visible()
            binding.tvDidNotGetOtp.visible()
            binding.tvOtpCountdown.gone()
        }
    }

    private fun otpAutoFill() {
        val editTextList = arrayOf(
            binding.etOne,
            binding.etTwo,
            binding.etThree,
            binding.etFour,
        )
        for (i in editTextList.indices) {
            val editText = editTextList[i]

            if (Constants.IsAUTOFILL.toBoolean()) {
                // Auto-fill the OTP fields if IsAUTOFILL is true
                editText.setText(Constants.OTP.substring(i, i + 1))
                editText.setSelection(editText.text.length)
            } else {
                // Allow user to type the OTP if IsAUTOFILL is false
                editText.setText("") // Clear any previous text
                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int, count: Int, after: Int,
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        s?.let {
                            if (it.length == 1) {
                                // Move to the next field when a digit is entered
                                if (i < editTextList.size - 1) {
                                    editTextList[i + 1].requestFocus()
                                }
                            } else if (it.isEmpty()) {
                                // Move to the previous field when a digit is deleted
                                if (i > 0) {
                                    editTextList[i - 1].requestFocus()
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    private fun setHeader() {
        binding.apply {
            header.tvTittle.text = getString(R.string.otpVerify)
            header.mcvArrow myClick {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }
            btnSubmit myClick {
                resultOtp =
                    etOne.text.toString().plus(etTwo.text.toString()).plus(etThree.text.toString()).plus(etFour.text.toString())
                Constants.OTP = resultOtp
                binding.viewModel?.otpVerifyApi(resultOtp, Constants.OTP)
            }
        }
    }

    fun resend(view: View) {
        binding.viewModel?.resendOtpApi()
    }
}