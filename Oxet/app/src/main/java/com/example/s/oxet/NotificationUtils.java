package com.example.s.oxet;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

public class NotificationUtils extends ContextWrapper {
    private NotificationManager mManager;
    public static final String REMINDER_CHANNEL_ID = "REMINDER_ID";
    public static final String IOS_CHANNEL_ID = "IOS_ID";
    public static final String REMINDER_CHANNEL_NAME = "REMINDER CHANNEL";
    public static final String IOS_CHANNEL_NAME = "IOS CHANNEL";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {
        NotificationChannel reminderChannel = new NotificationChannel(REMINDER_CHANNEL_ID,
                REMINDER_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        reminderChannel.enableLights(true);
        reminderChannel.enableVibration(true);
        reminderChannel.setLightColor(Color.GREEN);
        reminderChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(reminderChannel);

//        NotificationChannel iosChannel = new NotificationChannel(IOS_CHANNEL_ID,
//                IOS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
//        iosChannel.enableLights(true);
//        iosChannel.enableVibration(true);
//        iosChannel.setLightColor(Color.GRAY);
//        iosChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//        getManager().createNotificationChannel(iosChannel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getReminderChannelNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        return new Notification.Builder(getApplicationContext(), REMINDER_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public Notification.Builder getIosChannelNotification(String title, String body) {
//        return new Notification.Builder(getApplicationContext(), IOS_CHANNEL_ID)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setSmallIcon(android.R.drawable.stat_notify_more)
//                .setCategory(NotificationCompat.CATEGORY_REMINDER)
//                .setAutoCancel(true);
//    }
}
