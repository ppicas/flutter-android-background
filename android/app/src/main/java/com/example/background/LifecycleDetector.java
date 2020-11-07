package com.example.background;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;


public class LifecycleDetector implements Application.ActivityLifecycleCallbacks {

    private static LifecycleDetector instance = null;

    private LifecycleDetector(){}

    private synchronized static void createInstance() {
        if (instance == null) {
            instance = new LifecycleDetector();
        }
    }

    public static LifecycleDetector getInstance() {
        if (instance == null) createInstance();
        return instance;
    }

    private boolean isActivityRunning = false;

    public void setIsActivityRunning(boolean value) {
        isActivityRunning = value;
    }

    public boolean getIsActivityRunning() {
        return isActivityRunning;
    }

    Listener listener;

    public static interface Listener {
        void onFlutterActivityCreated();
        void onFlutterActivityDestroyed();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof MainActivity) {
                isActivityRunning = true;
                if (listener != null) listener.onFlutterActivityCreated();
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity instanceof MainActivity) {
            isActivityRunning = false;
            if (listener != null) listener.onFlutterActivityDestroyed();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

}