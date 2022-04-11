package pri.guanhua.myemoji.view.cloud;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import pri.guanhua.myemoji.R;

public class CloudEmojiActivity extends AppCompatActivity {

    private ImageView mBack = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_emoji);
        setStatusBar();
        initView();
        setBack();
    }

    private void initView(){
        mBack = findViewById(R.id.img_back);
    }

    /**
     * 设置状态栏
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setStatusBar(){
        //把状态栏的文字设置为黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //把状态栏设置透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * 设置返回
     */
    private void setBack(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
