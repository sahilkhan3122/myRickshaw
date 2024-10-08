package com.app.myrickshawparent.network.utility

class Common {

    object IntentKey {
        const val OTP = "otp"
        const val NUMBER = "number"
        const val ISAUTOFILL = "isAutofill"
        const val DATA = "data"
    }

    object IntentValue {

    }

    object OtpType {
        const val TYPE_RESEND_OTP = "RESEND_OTP"
        const val DELETE_ACCOUNT = "deleteAccount"
    }

    object Type {
        const val isFromOtp = "isFromOtp"
    }

    object DeviceType{
        const val ANDROID = "android"
    }

    object SockectEvent {
        const   val USER_CONNECT_EMIT = "userConnected"
        const  val GET_VEHICLE_EMIT = "getVehicle"
        const  val VEHICLE_LIVE_DATA_ON = "vehicleLiveTrackingData"
        const  val TRIP_CLOSED = "tripClosed"
        const val GET_TRIP_ETA = "getTripETA"
        const val LIVE_TRIP_ETA = "LiveTripETA"
    }

}