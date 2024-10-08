package com.app.myrickshawparent.network.utility

import android.content.Context
import com.app.myrickshawparent.R
import javax.inject.Inject


open class CheckValidation @Inject constructor(val context: Context) {

    fun loginValidation(mobileNumber: String, password: String): Result<Any, Any> {
        return if (mobileNumber.isEmpty()) {
            Result.Error(
                LoginError.ENTER_NUMBER,
                context.getString(R.string.please_enter_number)
            )
        } else if (password.isEmpty()) {
            Result.Error(
                LoginError.ENTER_PASSWORD,
                context.getString(R.string.please_enter_password)
            )
        } else {
            Result.Success(true, "")
        }
    }

    fun resetPasswordValidation(newPassword: String, confirmPassword: String): Result<Any, Any> {
        return if (newPassword.isEmpty()) {
            Result.Error(
                ResetPassError.ENTER_NEW_PASS,
                context.getString(R.string.please_enter_new_pass)
            )
        } else if (confirmPassword.isEmpty()) {
            Result.Error(
                ResetPassError.ENTER_CONFIRM_PASS,
                context.getString(R.string.please_enter_confirm_pass)
            )
        } else if (!newPassword.equals(confirmPassword)) {
            Result.Error(
                ResetPassError.PASS_NOT_MATCH,
                context.getString(R.string.password_not_match)
            )
        } else {
            Result.Success(true, "")
        }
    }

    fun forgotValidation(mobileNumber: String): Result<Any, Any> {
        return if (mobileNumber.isEmpty()) {
            Result.Error(
                LoginError.ENTER_NUMBER,
                context.getString(R.string.please_enter_number)
            )
        } else if (mobileNumber.length > 10) {
            Result.Error(
                ForgotError.ENTER_NUMBER,
                context.getString(R.string.invalid_number)
            )
        } else if (mobileNumber.length < 9) {
            Result.Error(
                ForgotError.ENTER_NUMBER,
                context.getString(R.string.invalid_number)
            )
        } else {
            Result.Success(true, "")
        }
    }

    fun changePassValidation(oldPass: String, newPass: String, confirmPass: String): Result<Any, Any> {
        return if (oldPass.isEmpty()) {
            Result.Error(
                ChangePassError.ENTER_OLD_PASS,
                context.getString(R.string.please_enter_old_pass)
            )
        } else if (newPass.isEmpty()) {
            Result.Error(
                ChangePassError.ENTER_NEW_PASS,
                context.getString(R.string.please_enter_new_pass)
            )
        } else if (confirmPass.isEmpty()) {
            Result.Error(
                ChangePassError.ENTER_CONFIRM_PASS,
                context.getString(R.string.please_enter_confirm_pass)
            )
        } else if(confirmPass.length<9){
            Result.Error(
                ChangePassError.PASSWORD_LENGTH,
                context.getString(R.string.confirm_password_invalid)
            )
        }else if(newPass.length<9){
            Result.Error(
                ChangePassError.PASSWORD_LENGTH,
                context.getString(R.string.new_password_invalid)
            )
        }else if(oldPass.length<9 ){
            Result.Error(
                ChangePassError.PASSWORD_LENGTH,
                context.getString(R.string.password_invalid)
            )
        }else if(!newPass.equals(confirmPass)){
            Result.Error(
                ChangePassError.PASS_NOT_MATCH,
                context.getString(R.string.password_not_match_change_pass)
            )
        }else {
            Result.Success(true, "")
        }
    }

    fun contactUsValidation(name: String, number: String, message: String): Result<Any, Any> {
        return if (name.isEmpty()) {
            Result.Error(
                ProfileError.ENTER_NAME,
                context.getString(R.string.please_enter_name)
            )
        } else if (number.isEmpty()) {
            Result.Error(
                ForgotError.ENTER_NUMBER,
                context.getString(R.string.please_enter_number)
            )
        } else if (message.isEmpty()) {
            Result.Error(
                ContactUs.ENTER_MESSAGE,
                context.getString(R.string.enter_message)
            )
        }else {
            Result.Success(true, "")
        }
    }

    fun verifyOtpValidation(enteredOtp: String, actualOtp: String): Result<Any, Any> {
        return when {
            enteredOtp.isEmpty() -> {
                Result.Error(
                    OtpError.ENTER_OTP,
                    context.getString(R.string.please_enter_otp)
                )
            }

            enteredOtp.length < 4 -> {
                Result.Error(
                    OtpError.INVALID_OTP,
                    context.getString(R.string.otp_does_not_match)
                )
            }

            enteredOtp != actualOtp -> {
                Result.Error(
                    OtpError.INVALID_OTP,
                    context.getString(R.string.otp_does_not_match)
                )
            }

            else -> {
                Result.Success(true, "")
            }
        }
    }

    fun profileValidation(name: String): Result<Any, Any> {
        return if (name.isEmpty()) {
            Result.Error(ProfileError.ENTER_NAME, context.getString(R.string.please_enter_name))
        } else {
            Result.Success(true, "")
        }
    }

    fun networkError(code: Int): String {
        return when (code) {
            400 -> context.getString(R.string.bad_request)
            401 -> context.getString(R.string.unauthorized)
            403 -> context.getString(R.string.forbidden)
            404 -> context.getString(R.string.not_found)
            405 -> context.getString(R.string.method_not_allowed)
            409 -> context.getString(R.string.conflict)
            422 -> context.getString(R.string.unprocessable_entity)
            500 -> context.getString(R.string.internal_server_error)
            502 -> context.getString(R.string.bad_gateway)
            503 -> context.getString(R.string.service_unavailable)
            504 -> context.getString(R.string.gateway_timeout)
            else -> context.getString(R.string.unknown)
        }
    }

}