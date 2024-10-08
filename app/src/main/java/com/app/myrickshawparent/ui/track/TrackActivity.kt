package com.app.myrickshawparent.ui.track

import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseActivity
import com.app.myrickshawparent.databinding.ActivityTrackBinding
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.NotifyMe
import com.app.myrickshawparent.network.utility.NotifyType
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.network.utility.SocketManage
import com.app.myrickshawparent.ui.dialog.CancelDialog
import com.app.myrickshawparent.ui.home.item.HomeResponse
import com.app.myrickshawparent.ui.track.adapter.BottomSheetAdapter
import com.app.myrickshawparent.ui.track.response.EstimateTimeResponse
import com.app.myrickshawparent.ui.track.response.LiveTrackSocketResponse
import com.app.myrickshawparent.ui.track.response.PopupResponse
import com.app.myrickshawparent.util.Constants
import com.app.myrickshawparent.util.Constants.cameraZoom
import com.app.myrickshawparent.util.Constants.homeResponseData
import com.app.myrickshawparent.util.Constants.homeResponseFullData
import com.app.myrickshawparent.util.bitmapToIcon
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.myClick
import com.app.myrickshawparent.util.printLog
import com.app.myrickshawparent.util.showToastMessage
import com.app.myrickshawparent.util.visible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrackActivity : BaseActivity<ActivityTrackBinding>(), OnMapReadyCallback {

    private lateinit var mGoogleMap: GoogleMap
    private var carMarker: Marker? = null

    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private lateinit var trackViewModel: TrackViewModel
    private lateinit var routeStopsItemList: MutableList<HomeResponse.RouteStopsItem>

    private lateinit var globalPathList: MutableList<LatLng>

    private val markers = ArrayList<Marker>()

    override val layoutId: Int
        get() = R.layout.activity_track


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        CoroutineScope(Dispatchers.IO).launch {
            Constants.notifyMutableStateFlow.emit(NotifyMe.Empty(NotifyType.EMPTY))
        }

        setupInsets()
        setHeader()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                printLog("Tracking info", "Call onBackPressedDispatcher")
                trackViewModel.socketDisconnect()
                finish()
            }
        })

        trackViewModel = ViewModelProvider(this)[TrackViewModel::class.java]
        trackViewModel.sendRoutsEmit.clear()
        trackViewModel.sendRoutsEmit.addAll(homeResponseData.routeStops)

        routeStopsItemList = arrayListOf()
        globalPathList = mutableListOf()

        initializeMap()
        setupBottomSheet()
        observeTrackState()
        notifyData()
    }

    /**
     * loading the map that time I'm showing the loader. After completing the loading map and adding all bus stop location then hide the loader.
     * @see loadingDialog
     * @see handleSocketData
     */
    private fun initializeMap() {
        loadingDialog.show()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_white))
        drawRout()
    }


    private fun setHeader() {
        binding.apply {
            header.tvTittle.text = homeResponseFullData.fullName
            header.btnInSchool.visible()
            header.mcvArrow myClick {
                globalPathList.clear()
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(
                systemBars.left, 0, systemBars.right, maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_sheet)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBottomSheet() {
        val bottomSheet = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet))
        bottomSheet.peekHeight = 600
        bottomSheet.isDraggable = true
        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN)
        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {}
            override fun onSlide(view: View, slideOffset: Float) {}
        })
        setBottomSheetAdapter(homeResponseData.routeStops)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setBottomSheetAdapter(routeStops: MutableList<HomeResponse.RouteStopsItem>) {
        routeStopsItemList.clear()
        routeStopsItemList.addAll(routeStops)

        if (bottomSheetAdapter == null) {
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.bottomSheet.rvBottomSheet.layoutManager = layoutManager
            bottomSheetAdapter = BottomSheetAdapter(this, routeStopsItemList)
            binding.bottomSheet.rvBottomSheet.adapter = bottomSheetAdapter
        } else {
            if (bottomSheetAdapter != null) {
                bottomSheetAdapter?.notifyDataSetChanged()
            }
        }

    }

    private fun observeTrackState() {
        lifecycleScope.launch {
            trackViewModel.trackStateFlow.collect { response ->
                when (response) {
                    is ResponseData.Loading -> {
                        loadingDialog.show()
                    }

                    is ResponseData.Success -> {
                        handleSocketData(response.data)
                    }

                    is ResponseData.Error -> {
                        loadingDialog.dismiss()
                        showToastMessage(response.error)
                    }

                    is ResponseData.Empty -> {}
                    is ResponseData.InternetConnection -> {
                        loadingDialog.dismiss()
                    }

                    is ResponseData.Exception -> {
                        loadingDialog.dismiss()
                        exceptionHandle(ExceptionData(code = response.code))
                    }
                }
            }
        }
    }

    /**
     * This function get notify data and perform operation
     * @see notifyData
     */
    private fun notifyData() {

        lifecycleScope.launch {
            Constants.notifyStateFlow.collect { newData ->
                when (newData) {

                    is NotifyMe.Notify<*> -> {
                        printLog("notifyData", "notifyData: ${newData.data}")
                        val response: String = newData.data.toString()
                        val popupResponse = gson.fromJson(response, PopupResponse::class.java)
                        if (popupResponse.firebaseResponse.routeType == "1") {
                            stopTrackingDialog(popupResponse)
                        } else {
                             if (popupResponse.firebaseResponse.id == homeResponseFullData.id.toString()) {
                                stopTrackingDialog(popupResponse)
                            }
                        }
                    }

                }
            }
        }

    }

    private fun stopTrackingDialog(popupResponse: PopupResponse) {
        trackViewModel.stopEmitLiveData()
        CancelDialog(
            context = this@TrackActivity,
            icon = R.mipmap.ic_richshaw,
            title = popupResponse.title,
            description = popupResponse.description,
            okButtonText = getString(R.string.ok),
            onOkBtnClick = {
                setResult(RESULT_OK)
                finish()
            },
        ).showDialog()
    }

    private fun handleSocketData(data: Any?) {

        when (data) {
            is SocketManage.Connect<*> -> {
                printLog("Tracking info", "Socket ==========> Connect  ${data.data.toString()}")
                trackViewModel.userEmit()
                trackViewModel.getTripEta()
            }

            is SocketManage.Disconnect<*> -> {
                printLog("Tracking info", "Socket ==========> Disconnect")
                dismissDialog()
                printLog(value = data.data.toString())
            }

            is SocketManage.SocketError<*> -> {
                printLog("Tracking info", "Socket ==========> SocketError")
                dismissDialog()
                printLog(value = data.data.toString())
            }

            is SocketManage.LiveTripEta<*> -> {
                printLog(
                    "Tracking info", "Socket ==========> LiveTripEta ${data.data.toString()}"
                )
                val estimateTimeResponse =
                    gson.fromJson(data.data.toString(), EstimateTimeResponse::class.java)
                binding.bottomSheet.timerContainer.visible()
                binding.bottomSheet.tvMessageTB.text = estimateTimeResponse.etaMessage
                dismissDialog()
            }

            is SocketManage.TrackLiveData<*> -> {
                printLog(
                    "Tracking info", "Socket ==========> TrackLiveData ${data.data.toString()}"
                )
                lifecycleScope.launch {
                    printLog(value = data.data.toString())
                    dismissDialog()
                    updateMapWithLiveData(data.data)
                }
            }

            is SocketManage.BackToHome<*> -> {

            }

        }

    }

    /**
     * Draw the route when google map loads.
     * @see carMarker set using first bus stop location
     * @see homeResponseData.routeStops set using all bus stop location
     */

    private fun drawRout() {

        val destinationLocation = LatLng(
            homeResponseData.destination?.latitude!!.toDouble(),
            homeResponseData.destination?.longitude!!.toDouble()
        )

        val startLocation = LatLng(
            homeResponseData.routeStops[0].latitude.toDouble(),
            homeResponseData.routeStops[0].longitude.toDouble()
        )

        carMarker = mGoogleMap.addMarker(
            MarkerOptions().position(startLocation).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_school_tracking)
            ).anchor(0.5f, 0.5f).flat(true)
        )


        /**
         * Add multiple stop with destination stop.
         * @see waypoints It Includes all waypoints that lie between the starting and ending points.
         */

        val waypoints: MutableList<LatLng> = arrayListOf()

        for (i in 0 until homeResponseData.routeStops.size) {
            printLog("Tracking info", "Add marker $i")
            val addNewLocation = LatLng(
                homeResponseData.routeStops[i].latitude.toDouble(),
                homeResponseData.routeStops[i].longitude.toDouble()
            )
            mGoogleMap.addMarker(
                MarkerOptions().position(addNewLocation).icon(
                    bitmapToIcon(R.drawable.ic_destination)
                )
            )?.let { markers.add(it) }

            if (homeResponseData.routeStops.size > 1 && i < homeResponseData.routeStops.size - 1) {
                waypoints.add(addNewLocation)
            }

            if (i == 0) {
                mGoogleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        addNewLocation, cameraZoom
                    )
                )
            }
        }

        /*trackViewModel.setMapPolyLine(
            mGoogleMap, startLocation, destinationLocation, waypoints
        ) { path ->
            globalPathList.addAll(path)
            if (path.isNotEmpty()) {
                trackViewModel.trackLiveDataEmit()
            }
        }*/

        trackViewModel.trackLiveDataEmit()

        printLog("Tracking info", "drawRout")

        dismissDialog()

    }

    private fun updateMapWithLiveData(data: Any?) {

        try {

            val trackingData = Gson().fromJson(data.toString(), LiveTrackSocketResponse::class.java)

            trackViewModel.sendRoutsEmit.clear()
            trackViewModel.sendRoutsEmit.addAll(trackingData.routeStops)

            val startLocation =
                LatLng(trackingData.latitude.toDouble(), trackingData.longitude.toDouble())
            updateBusLocation(startLocation)

            if (routeStopsItemList != trackingData.routeStops) {
                setBottomSheetAdapter(trackingData.routeStops)
            }

        } catch (e: Exception) {
            printLog(tag = "Tracking info", value = e.toString())
        }
    }

    private fun updateBusLocation(latLng: LatLng) {
        printLog("Tracking info", "updateCarLocation $latLng")
        carMarker?.let { animateMarkerNew(it, latLng) }
    }

    /**
     * Animate the marker to the destination
     */

    private fun animateMarkerNew(marker: Marker, destination: LatLng) {
        var readBearing = 0
        var bearingF = 0f
        val startPosition = marker.position
        val endPosition = LatLng(destination.latitude, destination.longitude)

        val latLngInterpolator: LatLngInterpolatorNew = LatLngInterpolatorNew.LinearFixed()

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.setDuration(3000)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->
            try {
                val v = animation.animatedFraction
                val newPosition: LatLng? =
                    latLngInterpolator.interpolate(v, startPosition, endPosition)
                marker.position = newPosition!!
                bearingF = trackViewModel.getBearing(
                    startPosition, LatLng(
                        destination.latitude, destination.longitude
                    )
                )
                if (bearingF != 0.0f) {
                    marker.rotation = bearingF
                    trackViewModel.rotateMarker(marker, bearingF)
                    readBearing = 1
                }
                printLog("Tracking info", "Bearing $bearingF")

            } catch (ex: Exception) {
                printLog("Exception Animation Marker", ex.toString())
            }
        }

        mGoogleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(endPosition).zoom(cameraZoom).bearing(bearingF)
                    .build()
            )
        )

        valueAnimator.addListener(object : AnimatorListenerAdapter() {})
        valueAnimator.start()

    }

    /**
     * Dismiss the loading dialog if it is showing
     */

    private fun dismissDialog() {
        if (loadingDialog.isShowing) loadingDialog.dismiss()
    }

    override fun onSupportNavigateUp(): Boolean {
        printLog("Tracking info", "Call onSupportNavigateUp")
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        printLog("Tracking info", "Call onDestroy")
        super.onDestroy()
    }
}
