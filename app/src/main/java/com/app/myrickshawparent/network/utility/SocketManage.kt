package com.app.myrickshawparent.network.utility

sealed class SocketManage() {
    data class Connect<out D>(val data: D) : SocketManage()
    data class Disconnect<out D>(val data: D) : SocketManage()
    data class SocketError<out D>(val data: D) : SocketManage()
    data class LiveTripEta<out D>(val data: D) : SocketManage()
    data class ReConnect<out D>(val data: D) : SocketManage()
    data class UserConnect<out D>(val data: D) : SocketManage()
    data class UserDisconnect<out D>(val data: D) : SocketManage()
    data class TrackLiveData<out D>(val data: D) : SocketManage()
    data class BackToHome<out T>(val data: T? = null) : SocketManage()
}