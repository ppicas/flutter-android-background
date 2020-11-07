package com.example.background;

import android.app.Application;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LifecycleDetector lifecycleDetector = LifecycleDetector.getInstance();
        registerActivityLifecycleCallbacks(lifecycleDetector);
    }
}
