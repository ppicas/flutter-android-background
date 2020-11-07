package com.example.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class Notifications {
    final static int NOTIFICATION_ID_BACKGROUND_SERVICE = 1;

    final static String CHANNEL_ID_BACKGROUND_SERVICE = "background_service";

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_BACKGROUND_SERVICE,
                "Background Service",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    public static Notification buildForegroundNotification(Context context) {
        return new NotificationCompat.Builder(context, CHANNEL_ID_BACKGROUND_SERVICE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Background Service")
            .setContentText("Keeps app process on foreground.")
            .build();
    }
}
