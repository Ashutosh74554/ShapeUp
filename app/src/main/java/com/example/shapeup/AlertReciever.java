package com.example.shapeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificatonHelper notificatonHelper=new NotificatonHelper(context);
        NotificationCompat.Builder nb= notificatonHelper.getChannelNotification();
        notificatonHelper.getManager().notify(1, nb.build());
    }
}
