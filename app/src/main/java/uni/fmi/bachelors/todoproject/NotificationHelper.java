package uni.fmi.bachelors.todoproject;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

    public static final String CHANNEL = "channel";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
           createChannels();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel channel1 = new NotificationChannel(CHANNEL,"Channel", NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.colorPrimary);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

       getManager().createNotificationChannel(channel1);
    }

    public NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getChannelNotification(String title, String description){
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_alarm)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
}
