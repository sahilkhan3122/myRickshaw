package com.app.myrickshawparent

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.app.myrickshawparent.util.Constants
import com.app.myrickshawparent.util.printLog
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application(), Application.ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        FirebaseApp.initializeApp(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        Constants.isAppRunning = true
                        //printLog("data_information", "on create")
                    }

                    Lifecycle.Event.ON_START -> {
                        //printLog("data_information", "on start")
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        //printLog("data_information", "on resume")
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        //printLog("data_information", "on pause")
                    }

                    Lifecycle.Event.ON_STOP -> {
                        //printLog("data_information", "on stop")
                    }

                    Lifecycle.Event.ON_DESTROY -> {
                        //printLog("data_information", "on destroy")
                    }

                    Lifecycle.Event.ON_ANY -> {
                        //printLog("data_information", "on any")
                    }
                }
            }

        })
        registerActivityLifecycleCallbacks(this)

    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        //printLog("data_information", "Call onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
        printLog("data_information", "Call onActivityStarted ${activity.toString()}")
    }

    override fun onActivityResumed(activity: Activity) {
        //printLog("data_information", "Call onActivityResumed")

    }

    override fun onActivityPaused(activity: Activity) {
        //printLog("data_information", "Call onActivityPaused")

    }

    override fun onActivityStopped(activity: Activity) {
        //printLog("data_information", "Call onActivityStopped")

    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
        //printLog("data_information", "Call onActivitySaveInstanceState")

    }

    override fun onActivityDestroyed(activity: Activity) {
        //printLog("data_information", "Call onActivityDestroyed")

    }

}