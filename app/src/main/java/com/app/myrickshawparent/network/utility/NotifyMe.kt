package com.app.myrickshawparent.network.utility

sealed class NotifyMe() {
    data class Notify<out D>(val data: D, val type: NotifyType) : NotifyMe()
    data class Empty<out D>(val data: D) : NotifyMe()
}

enum class NotifyType{
    LIVE_TRIP_STOP,
    EMPTY
}