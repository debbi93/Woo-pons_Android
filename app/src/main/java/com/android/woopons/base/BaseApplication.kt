@file:Suppress("unused")

package com.android.woopons.base


import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Typeface
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.util.*
import java.util.concurrent.TimeUnit


open class BaseApplication : Application(), LifecycleObserver {


    companion object {
        @SuppressLint("StaticFieldLeak")
        var instance: BaseApplication? = null
        var inBackground = false
        var fontRobotoBold: Typeface? = null
        var fontRobotoRegular: Typeface? = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onMoveToForeground() {
        // app moved to foreground
        Log.e("inBackground", "false")
        inBackground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onMoveToBackground() {
        // app moved to background
        Log.e("inBackground", "true")
        inBackground = true
    }


    override fun onCreate() {
        super.onCreate()
        instance = this

        AndroidNetworking.initialize(this)
        val okHttpClient = OkHttpClient()
            .newBuilder()
            .protocols(Arrays.asList(Protocol.HTTP_1_1))
            .connectTimeout(42000, TimeUnit.SECONDS)
            .readTimeout(42000, TimeUnit.SECONDS)
            .writeTimeout(42000, TimeUnit.SECONDS)
        AndroidNetworking.initialize(this,okHttpClient.build())
        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.HEADERS)
//        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY)
        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BASIC)

//        FirebaseCrashlytics.getInstance().setUserId(LocalPreference.shared.user?.email ?: "")
    }


}
