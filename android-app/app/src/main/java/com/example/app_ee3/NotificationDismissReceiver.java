//bron: ChatGPT

package com.example.app_ee3;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            int notificationId = intent.getIntExtra("notification_id", 0);
            notificationManager.cancel(notificationId);
        }
    }
}