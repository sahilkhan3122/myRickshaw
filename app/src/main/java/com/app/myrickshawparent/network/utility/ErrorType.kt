package com.app.myrickshawparent.network.utility


enum class LoginError {
    ENTER_NUMBER,
    ENTER_PASSWORD,
}

enum class ForgotError {
    ENTER_NUMBER
}

enum class ResetPassError {
    ENTER_CONFIRM_PASS,
    ENTER_NEW_PASS,
    PASS_NOT_MATCH
}

enum class ChangePassError {
    ENTER_OLD_PASS,
    ENTER_NEW_PASS,
    PASSWORD_LENGTH,
    ENTER_CONFIRM_PASS,
    PASS_NOT_MATCH
}

enum class OtpError {
    ENTER_OTP,
    INVALID_OTP
}

enum class ContactUs {
    ENTER_MESSAGE
}

enum class ProfileError {
    ENTER_NAME
}