package com.example.background;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.IBinder;

import android.util.Log;
import java.lang.InterruptedException;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.view.FlutterCallbackInformation;
import io.flutter.view.FlutterMain;
import io.flutter.plugins.GeneratedPluginRegistrant;


public class BackgroundService extends Service implements LifecycleDetector.Listener {

    private FlutterEngine flutterEngine;

    private static String SHARED_PREFERENCES_NAME = "com.exmaple.background.BackgroundService";
    private static String KEY_CALLBACK_RAW_HANDLE = "callbackRawHandle";

    private LifecycleDetector instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = Notifications.buildForegroundNotification(BackgroundService.this);
        startForeground(Notifications.NOTIFICATION_ID_BACKGROUND_SERVICE, notification);
        LifecycleDetector instance = LifecycleDetector.getInstance();
        instance.listener = BackgroundService.this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Long callbackRawHandle = intent.getLongExtra(KEY_CALLBACK_RAW_HANDLE, -1);
            if (callbackRawHandle != null) {
                if (callbackRawHandle != -1L) {
                    setCallbackRawHandle(callbackRawHandle);
                }
            }
        }
        if (!LifecycleDetector.getInstance().getIsActivityRunning()) {
            startFlutterNativeView();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        instance.listener = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onFlutterActivityCreated() {
        stopFlutterNativeView();
    }

    @Override
    public void onFlutterActivityDestroyed() {
        startFlutterNativeView();
    }

    private void startFlutterNativeView() {
        if (flutterEngine != null) return;

        Log.i("BackgroundService", "Starting FlutterEngine");
        Long callbackRawHandle = getCallbackRawHandle();
        if (callbackRawHandle != null) {
            FlutterCallbackInformation callbackInformation =
            FlutterCallbackInformation.lookupCallbackInformation(callbackRawHandle);

            flutterEngine = new FlutterEngine(this);
            DartExecutor executor = flutterEngine.getDartExecutor();
            String appBundlePath = FlutterMain.findAppBundlePath();
            AssetManager assets = this.getAssets();

            DartExecutor.DartCallback dartCallback = new DartExecutor.DartCallback(assets, appBundlePath, callbackInformation);
            executor.executeDartCallback(dartCallback);
        }
    }

    private void stopFlutterNativeView() {
        Log.i("BackgroundService", "Stopping FlutterEngine");
        if (flutterEngine != null) flutterEngine.destroy();
        flutterEngine = null;
    }

    private Long getCallbackRawHandle() {
        Long callbackRawHandle = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getLong(KEY_CALLBACK_RAW_HANDLE, -1);
        return (callbackRawHandle != -1L) ? callbackRawHandle : null;
    }

    private void setCallbackRawHandle(Long handle) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(KEY_CALLBACK_RAW_HANDLE, handle).commit();
    }

    public static void startService(Context context, Long callbackRawHandle) {
        Intent intent = new Intent(context, BackgroundService.class);
        intent.putExtra(KEY_CALLBACK_RAW_HANDLE, callbackRawHandle);
        ContextCompat.startForegroundService(context, intent);
    }
}
