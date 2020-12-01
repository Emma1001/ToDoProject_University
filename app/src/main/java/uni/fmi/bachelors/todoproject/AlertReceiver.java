package uni.fmi.bachelors.todoproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("Title");
        String desc = intent.getStringExtra("Description");
        int index = intent.getIntExtra("Index",0);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title,desc);
        notificationHelper.getManager().notify(index,nb.build());
    }
}
