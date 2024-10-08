package com.app.myrickshawparent.network.domain

class ApiObject {

    object ApiHeaderKey {

        const val X_API_KEY = "x-api-key"
    }

    object ApiHeaderValue {
        const val X_API_KEY_VALUE = "my-rickshaw"
    }

    object ApiEndPoint {
        const val LOGIN = "login"
        const val SPLASH = "init/android/"
        const val FORGOT = "forgot-password"
        const val VERIFY_OTP = "verify-otp"
        const val RESEND_OTP = "resend-otp"
        const val UPDATE_PROFILE = "update-profile"
        const val RESET_PASS = "reset-password"
        const val LOGOUT = "logout"
        const val DELETE_ACCOUNT = "delete-account"
        const val UPDATE_PASSWORD = "update-password"
        const val CONTACT_US = "contact-us-save"
        const val CHILDREN = "childrens"
        const val HOME = "home"
        const val FAQS = "faqs"
        const val NOTIFICATIONS = "notifications"

    }

    object Param {

        //Login
        const val MOBILE_NUMBER = "mobile_number"
        const val PASSWORD = "password"
        const val DEVICE_TOKEN = "device_token"
        const val DEVICE_TYPE = "device_type"

        //verify otp
        const val OTP = "otp"

        //reset password
        const val PASSWORD_CONFIRM = "password_confirmation"

        //Edit Profile
        const val FULL_NAME = "full_name"
        const val PROFILE = "profile"

        //change password
        const val OLD_PASS = "current_password"
        const val NEW_PASS = "new_password"
        const val CONFIRM_PASS = "new_password_confirmation"

        //ContactUs
        const val NAME = "contact_name"
        const val CONTACT_NUMBER = "contact_number"

        //child
        const val SEARCH = "search"

        //ContactUs
        const val MESSAGE = "message"

        const val VEHICLE_NO_PARAM = "vehicleNo"
        const val ROUTE_STOP_PARAM = "routeStops"
        const val ROUT_ID_PARAM = "routeId"
        const val ROUT_TYPE_PARAM = "routeType"

        const val PARAM_TOKEN = "token"

    }

}