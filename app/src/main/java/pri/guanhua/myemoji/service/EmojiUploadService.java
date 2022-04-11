package pri.guanhua.myemoji.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import pri.guanhua.myemoji.R;

public class EmojiUploadService extends Service {
    private static final String CHANNEL_ID = "1";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 创建通知渠道
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setNotification(){
        //自定义通知
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_upload);
        //创建基本通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_upload)
                .setContentTitle("jj")
                .setContentText("22")
                .setOngoing(true)
                .setCustomContentView(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //启动通知
        startForeground(Integer.parseInt(CHANNEL_ID), builder.build());

    }
}
