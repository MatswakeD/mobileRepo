package com.example.dee_kay.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * Handles time notifications
 */

public class AlertReciever extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {

        createNofication(context,"Times up","5 seconds has passed", "Alert");
    }

    private void createNofication(Context context, String msg, String msgText, String msgAlert) {

        PendingIntent notifactionIntent = PendingIntent.getActivity(context,0,new Intent(context,NFCtag.class), 0);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);

        notificationBuilder.setContentIntent(notifactionIntent);
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        notificationBuilder.setAutoCancel(true);
        NotificationManager notifactioManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifactioManager.notify(1,notificationBuilder.build());
    }
}
