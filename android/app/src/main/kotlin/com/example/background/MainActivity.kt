package com.example.background

import android.os.Bundle
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)

        Notifications.createNotificationChannels(this)

        MethodChannel(flutterView, "com.example/background_service").apply {
            setMethodCallHandler { method, result ->
                if (method.method == "startService") {
                    val callbackRawHandle = method.arguments as Long
                    BackgroundService.startService(this@MainActivity, callbackRawHandle)
                    result.success(null)
                } else {
                    result.notImplemented()
                }
            }
        }

        MethodChannel(flutterView, "com.example/app_retain").apply {
            setMethodCallHandler { method, result ->
                if (method.method == "sendToBackground") {
                    moveTaskToBack(true)
                    result.success(null)
                } else {
                    result.notImplemented()
                }
            }
        }
    }
}
