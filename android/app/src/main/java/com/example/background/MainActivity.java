package com.example.background;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {

    private Intent forService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Notifications.createNotificationChannels(this);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "com.example/background_service")
            .setMethodCallHandler(
                (call, result) -> {
                    if (call.method.equals("startService")) {
                        Long callbackRawHandle = call.arguments();
                        BackgroundService background_service = new BackgroundService();
                        background_service.startService(MainActivity.this, callbackRawHandle);
                        result.success(null);
                    } else {
                        result.notImplemented();
                    }
                }
            );

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "com.example/app_retain")
            .setMethodCallHandler(
                (call, result) -> {
                    if (call.method.equals("sendToBackground")) {
                        moveTaskToBack(true);
                        result.success("Moved task to back");
                    }
                }
            );
    }
}
