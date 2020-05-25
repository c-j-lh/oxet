package com.example.s.oxet;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 120;
//    private static SharedPreferences sharedPreferences;
    private static MainActivity mainActivity;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean notificationWanted = mainActivity.preferences == null || mainActivity.preferences.getBoolean("notification", true);
        Log.d("notification","notification wanted? "+notificationWanted);
        if(notificationWanted && mainActivity.gamIO.ar[0]!=0){
            Notification.Builder nb = mainActivity.mNotificationUtils.
                    getReminderChannelNotification("Oxet practice reminder", "Remember to practice your languages on Oxet!");

            mainActivity.mNotificationUtils.getManager().notify(NOTIFICATION_ID, nb.build());
        }
    }

    public static void setMainActivity(MainActivity mMainActivity){
        mainActivity = mMainActivity;
    }
}
