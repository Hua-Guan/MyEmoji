package pri.guanhua.myemoji.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.dao.EmojisDao;
import pri.guanhua.myemoji.model.database.AppDatabase;
import pri.guanhua.myemoji.model.entity.EmojisEntity;
import pri.guanhua.myemoji.utils.MyUtils;
import pri.guanhua.myemoji.view.UserConst;

public class EmojiUploadService extends Service {
    private static final String CHANNEL_ID = "1";
    private static final String URL = UserConst.URL + UserConst.USER_UPLOAD_EMOJIS;
    private static final String URL_UPDATE_USER_ALBUM = UserConst.URL + UserConst.USER_UPDATE_EMOJI;

    private Handler mHandler = new Handler(Looper.myLooper());

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
    public int onStartCommand(Intent intent, int flags, int startId){
        setNotification();
        try {
            setUploading();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * 上传图片
     */
    private void setUploading() throws Exception{
        //获取数据库实例
        AppDatabase database = AppDatabase.getInstance(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从数据库获取图片信息
                EmojisDao dao = database.emojisDao();
                List<EmojisEntity> list;
                list = dao.getAll();
                List<EmojisEntity> finalList = list;
                for (int i = 0; i< finalList.size(); i++){
                    EmojisEntity entity = finalList.get(i);
                    //获取图片字节流
                    byte[] bytes;
                    try {
                        bytes = getByteStreamByUri(Uri.parse(entity.emojiUri)).toByteArray();
                        //开始上传
                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart(UserConst.USER_ACCOUNT, getUserAccount())
                                .addFormDataPart(UserConst.USER_EMOJI_MD5, MyUtils.getMD5(bytes))
                                .addFormDataPart("file", String.valueOf(entity.id),
                                        RequestBody.create(MediaType.parse("multipart/form-data"), bytes))
                                .build();
                        Request request = new Request.Builder()
                                .url(URL)
                                .post(body)
                                .build();
                        client.newCall(request).execute();
                        updateUserEmoji(entity.emojiAlbum, String.valueOf(entity.id), bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EmojiUploadService.this, "上传完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    /**
     * 根据uri获取字节流
     * @param uri
     * @return
     * @throws Exception
     */
    private ByteArrayOutputStream getByteStreamByUri(Uri uri) throws Exception{
        //获取字节流
        ContentResolver resolver = getApplicationContext()
                .getContentResolver();
        InputStream stream = resolver.openInputStream(uri);
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = stream.read(buffer)) != -1){
            bos.write(buffer, 0, len);
        }
        bos.flush();
        stream.close();
        return bos;
    }

    private String getUserAccount(){
        SharedPreferences preferences = getSharedPreferences(UserConst.USER_DATA, MODE_PRIVATE);
        String account = preferences.getString(UserConst.USER_ACCOUNT, "无");
        return account;
    }

    private void updateUserEmoji(String album, String emojiName, byte[] bytes) throws IOException, NoSuchAlgorithmException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add(UserConst.USER_ACCOUNT, getUserAccount())
                .add(UserConst.USER_ALBUM, album)
                .add(UserConst.USER_EMOJI_TITLE, emojiName)
                .add(UserConst.USER_EMOJI_MD5, MyUtils.getMD5(bytes))
                .build();
        Request request = new Request.Builder()
                .url(URL_UPDATE_USER_ALBUM)
                .post(body)
                .build();
        client.newCall(request).execute();
    }

}
