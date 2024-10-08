package com.app.myrickshawparent.ui.track

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.lifecycle.viewModelScope
import com.app.myrickshawparent.BuildConfig
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseViewModel
import com.app.myrickshawparent.network.domain.ApiObject.Param.PARAM_TOKEN
import com.app.myrickshawparent.network.domain.ApiObject.Param.ROUTE_STOP_PARAM
import com.app.myrickshawparent.network.domain.ApiObject.Param.ROUT_ID_PARAM
import com.app.myrickshawparent.network.domain.ApiObject.Param.ROUT_TYPE_PARAM
import com.app.myrickshawparent.network.domain.ApiObject.Param.VEHICLE_NO_PARAM
import com.app.myrickshawparent.network.utility.Common.SockectEvent.GET_TRIP_ETA
import com.app.myrickshawparent.network.utility.Common.SockectEvent.GET_VEHICLE_EMIT
import com.app.myrickshawparent.network.utility.Common.SockectEvent.LIVE_TRIP_ETA
import com.app.myrickshawparent.network.utility.Common.SockectEvent.TRIP_CLOSED
import com.app.myrickshawparent.network.utility.Common.SockectEvent.USER_CONNECT_EMIT
import com.app.myrickshawparent.network.utility.Common.SockectEvent.VEHICLE_LIVE_DATA_ON
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.network.utility.SocketManage
import com.app.myrickshawparent.ui.home.item.HomeResponse.RouteStopsItem
import com.app.myrickshawparent.util.Constants.homeResponseData
import com.app.myrickshawparent.util.printLog
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@HiltViewModel
class TrackViewModel @Inject constructor(
    val context: Context,
    private val socket: Socket,
) : BaseViewModel() {

    private val client = OkHttpClient()
    private val trackMutableStateFlow: MutableStateFlow<ResponseData<Any>> =
        MutableStateFlow(ResponseData.Empty())
    var trackStateFlow: StateFlow<ResponseData<Any>> = trackMutableStateFlow

    private var job: Job
    var sendRoutsEmit: MutableList<RouteStopsItem> = arrayListOf()

    init {

        job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            emitLiveDataData()
            while (true) {
                delay(5000)
                emitLiveDataData()
            }
        }

        socket.connect()
        socket.on(Socket.EVENT_CONNECT) {
            trackMutableStateFlow.value =
                ResponseData.Success(data = SocketManage.Connect("Socket EVENT_CONNECT"))
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) {
            trackMutableStateFlow.value =
                ResponseData.Success(data = SocketManage.SocketError("Socket EVENT_CONNECT_ERROR"))
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            trackMutableStateFlow.value =
                ResponseData.Success(data = SocketManage.Disconnect("Socket EVENT_DISCONNECT"))
        }

        socket.on(LIVE_TRIP_ETA) {
            if (it != null && it.isNotEmpty()) {
                val data = it[0]
                trackMutableStateFlow.value =
                    ResponseData.Success(data = SocketManage.LiveTripEta(data))
            }
        }

        socket.on(VEHICLE_LIVE_DATA_ON) {
            if (it != null && it.isNotEmpty()) {
                val data = it[0]
                printLog("live_tracking", data)
                trackMutableStateFlow.value =
                    ResponseData.Success(data = SocketManage.TrackLiveData(data))
            }
        }

        socket.on(TRIP_CLOSED) {
            printLog("Tracking info", "Call after data ==========> TRIP_CLOSED")
            if (it != null && it.isNotEmpty()) {
                val data = it[0]
                trackMutableStateFlow.value =
                    ResponseData.Success(data = SocketManage.BackToHome(data))
            }
        }

    }

    fun userEmit() {
        viewModelScope.launch {
            val jsonObject = JSONObject()
            var token: String
            withContext(Dispatchers.IO) {
                token = dataStore.getStringData(dataStore.token).first()
                jsonObject.put(PARAM_TOKEN, token)
            }
            withContext(Dispatchers.Main) {
                socket.emit(USER_CONNECT_EMIT, jsonObject)
            }
        }
    }

    fun trackLiveDataEmit() {
        job.start()
    }

    fun getTripEta() {
        if (socket.connected()) {
            val jsonObject = JSONObject()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    jsonObject.put(VEHICLE_NO_PARAM, homeResponseData.vehicleNumber)
                    jsonObject.put(ROUT_ID_PARAM, homeResponseData.routeId)
                    jsonObject.put(
                        ROUTE_STOP_PARAM, JSONObject(Gson().toJson(homeResponseData))
                    )
                }
                withContext(Dispatchers.Main) {
                    printLog("socket", "GET_TRIP_ETA: $jsonObject")
                    socket.emit(GET_TRIP_ETA, jsonObject)
                }
            }
        }
    }

    private suspend fun emitLiveDataData() {
        if (socket.connected()) {
            val jsonObject = JSONObject()
            withContext(Dispatchers.IO) {
                jsonObject.put(VEHICLE_NO_PARAM, homeResponseData.vehicleNumber)
                jsonObject.put(
                    ROUTE_STOP_PARAM, JSONObject(Gson().toJson(homeResponseData))
                )
                jsonObject.put(ROUT_ID_PARAM, homeResponseData.routeId)
                jsonObject.put(ROUT_TYPE_PARAM, sendRoutsEmit.toString())
            }
            withContext(Dispatchers.Main) {
                socket.emit(GET_VEHICLE_EMIT, jsonObject)
                printLog("socket", "trackLiveDataEmit: $jsonObject")
            }
        }
    }

    fun stopEmitLiveData() {
        // To stop the delayed emission:
        if (job.isActive) {
            job.cancel()
        }
        socketDisconnect()
    }

    /**
     * Draw polyline point
     * @see setMapPolyLine
     */

    fun setMapPolyLine(
        googleMap: GoogleMap,
        startLocation: LatLng,
        destinationLocation: LatLng,
        waypoints: MutableList<LatLng>,
        onPathReady: (List<LatLng>) -> Unit,
    ) {

        printLog("Tracking info", "setMapPolyLine call")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                var url: String?
                var path: List<LatLng>? = null
                withContext(Dispatchers.IO) {
                    url = getGooglePolylineUrl(startLocation, destinationLocation, waypoints)
                }
                withContext(Dispatchers.IO) {
                    if (url != null) {
                        val request = Request.Builder().url(url!!).build()
                        val response = client.newCall(request).execute()
                        val responseData = response.body?.string()
                        path = extractPolylinePoints(responseData)
                    }
                }

                withContext(Dispatchers.Main) {
                    if (path != null) {
                        drawPolyline(googleMap, path!!)
                        onPathReady(path!!)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * @see getGooglePolylineUrl
     * Show root with multiple stop(way point)
     */
    private fun getGooglePolylineUrl(
        startLocation: LatLng,
        destinationLocation: LatLng,
        waypoints: List<LatLng>,
    ): String {
        val origin = "${startLocation.latitude},${startLocation.longitude}"
        val destination = "${destinationLocation.latitude},${destinationLocation.longitude}"

        // Add waypoints (intermediate stops) to the URL if provided
        val waypointString = if (waypoints.isNotEmpty()) {
            val waypointsEncoded = waypoints.joinToString("|") { waypoint ->
                "${waypoint.latitude},${waypoint.longitude}"
            }
            "&waypoints=${URLEncoder.encode(waypointsEncoded, StandardCharsets.UTF_8.toString())}"
        } else {
            ""
        }

        return "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${URLEncoder.encode(origin, StandardCharsets.UTF_8.toString())}" +
                "&destination=${
                    URLEncoder.encode(
                        destination,
                        StandardCharsets.UTF_8.toString()
                    )
                }" +
                waypointString +
                "&key=${BuildConfig.MAP_KEY}"
    }


    private fun extractPolylinePoints(responseData: String?): List<LatLng> {
        val jsonObject = Gson().fromJson(responseData, Map::class.java)
        val routes = jsonObject["routes"] as List<*>
        val overviewPolyline = (routes[0] as Map<*, *>)["overview_polyline"] as Map<*, *>
        val encodedString = overviewPolyline["points"] as String
        return decodePolyline(encodedString)
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng((lat / 1E5), (lng / 1E5)))
        }

        return poly
    }

    private fun drawPolyline(googleMap: GoogleMap, path: List<LatLng>) {
        val polylineOptions = PolylineOptions().addAll(path).color(R.color.black).width(15f)
        googleMap.addPolyline(polylineOptions)
    }

    /**
     * Calculate the bearing between two points
     */

    fun getBearing(startLatLng: LatLng, endLatLng: LatLng): Float {
        val PI = 3.14159
        val lat1 = startLatLng.latitude * PI / 180
        val long1 = startLatLng.longitude * PI / 180
        val lat2 = endLatLng.latitude * PI / 180
        val long2 = endLatLng.longitude * PI / 180

        val dLon = (long2 - long1)

        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

        var brng = atan2(y, x)

        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360

        return brng.toFloat()
    }

    /**
     * Rotate the marker on the map
     */

    fun rotateMarker(marker: Marker, toRotation: Float) {
        val handler = Handler(Looper.getMainLooper())
        val start = SystemClock.uptimeMillis()
        val startRotation = marker.rotation
        val duration: Long = 1555

        val interpolator: Interpolator = LinearInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = interpolator.getInterpolation(elapsed.toFloat() / duration)

                val rot = t * toRotation + (1 - t) * startRotation

                marker.rotation = if (-rot > 180) rot / 2 else rot
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                }
            }
        })
    }

    fun updatePolylineWithLiveTracking(
        googleMap: GoogleMap,
        globalPathList: MutableList<LatLng>,
        latestLatLng: LatLng,
    ) {
        if (globalPathList.isNotEmpty()) {
            globalPathList.removeAt(0) // Remove the oldest point
            globalPathList.add(latestLatLng) // Add the new point
            googleMap.clear() // Clear previous polyline
            drawPolyline(googleMap, globalPathList) // Draw updated polyline
        }
    }

    fun socketDisconnect() {
        if (job.isActive) {
            job.cancel()
            printLog("Tracking info", "Call job cancel")
        }
        socket.off(USER_CONNECT_EMIT)
        socket.off(GET_VEHICLE_EMIT)
        socket.off(VEHICLE_LIVE_DATA_ON)
        socket.off(TRIP_CLOSED)
        socket.off(Socket.EVENT_CONNECT)
        socket.off(Socket.EVENT_CONNECT_ERROR)
        socket.off(Socket.EVENT_DISCONNECT)
        socket.off()
        socket.disconnect()
    }
}