package com.example.background

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.view.FlutterCallbackInformation
import io.flutter.view.FlutterMain
import io.flutter.view.FlutterNativeView
import io.flutter.view.FlutterRunArguments

class BackgroundService : Service(), LifecycleDetector.Listener {

    private var flutterNativeView: FlutterNativeView? = null

    override fun onCreate() {
        super.onCreate()

        val notification = Notifications.buildForegroundNotification(this)
        startForeground(Notifications.NOTIFICATION_ID_BACKGROUND_SERVICE, notification)

        LifecycleDetector.listener = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getLongExtra(KEY_CALLBACK_RAW_HANDLE, -1)?.let { callbackRawHandle ->
            if (callbackRawHandle != -1L) setCallbackRawHandle(callbackRawHandle)
        }

        if (!LifecycleDetector.isActivityRunning) {
            startFlutterNativeView()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        LifecycleDetector.listener = null
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onFlutterActivityCreated() {
        stopFlutterNativeView()
    }

    override fun onFlutterActivityDestroyed() {
        startFlutterNativeView()
    }

    private fun startFlutterNativeView() {
        Log.i("BackgroundService", "Starting FlutterNativeView")
        FlutterMain.ensureInitializationComplete(this, null)

        getCallbackInformation()?.let { flutterCallbackInformation ->
            flutterNativeView = FlutterNativeView(this, true).apply {
                GeneratedPluginRegistrant.registerWith(pluginRegistry)

                val args = FlutterRunArguments().apply {
                    bundlePath = FlutterMain.findAppBundlePath()
                    entrypoint = flutterCallbackInformation.callbackName
                    libraryPath = flutterCallbackInformation.callbackLibraryPath
                }

                runFromBundle(args)
            }
        }
    }

    private fun stopFlutterNativeView() {
        Log.i("BackgroundService", "Stopping FlutterNativeView")
        flutterNativeView?.destroy()
        flutterNativeView = null
    }

    private fun getCallbackInformation(): FlutterCallbackInformation? {
        val prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val callbackRawHandle = prefs.getLong(KEY_CALLBACK_RAW_HANDLE, -1)
        return if (callbackRawHandle != -1L) {
            FlutterCallbackInformation.lookupCallbackInformation(callbackRawHandle)
        } else {
            null
        }
    }

    private fun setCallbackRawHandle(handle: Long) {
        val prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_CALLBACK_RAW_HANDLE, handle).apply()
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "com.example.BackgroundService"

        private const val KEY_CALLBACK_RAW_HANDLE = "callbackRawHandle"

        fun startService(context: Context, callbackRawHandle: Long) {
            val intent = Intent(context, BackgroundService::class.java).apply {
                putExtra(KEY_CALLBACK_RAW_HANDLE, callbackRawHandle)
            }
            ContextCompat.startForegroundService(context, intent)
        }

    }

}
