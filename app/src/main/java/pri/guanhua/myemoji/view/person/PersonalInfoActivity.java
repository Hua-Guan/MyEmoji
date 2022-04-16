package pri.guanhua.myemoji.view.person;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.view.UserConst;

public class PersonalInfoActivity extends AppCompatActivity {

    private static final String URL = UserConst.URL + UserConst.USER_PERSONAL_INFO;

    private ImageView mBack;
    private ImageView mAvatar;
    private TextView mAccount;
    private TextView mEmojisCount;
    private Button mQuit;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        setStatusBar();
        initView();
        setBack();
        setQuitLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBaseInfo();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setStatusBar(){
        //把状态栏的文字设置为黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //把状态栏设置透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * 初始化View
     */
    private void initView(){
        mBack = findViewById(R.id.img_back);
        mAvatar = findViewById(R.id.img_avatar);
        mAccount = findViewById(R.id.text_account);
        mEmojisCount = findViewById(R.id.text_emojis_count);
        mQuit = findViewById(R.id.quit_login);
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

    /**
     * 设置用户基本信息
     */
    private void setBaseInfo(){
        //设置头像
        String path = getExternalFilesDir("Avatar").getPath();
        File file = new File(path, "default.jpg");
        if (file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            mAvatar.setImageBitmap(bitmap);
            mAvatar.setClipToOutline(true);
        }
        //设置账号
        Intent intent = getIntent();
        String account = intent.getStringExtra(UserConst.USER_ACCOUNT);
        String emojisCount = intent.getStringExtra(UserConst.USER_EMOJIS_COUNT);
        mAccount.setText(account);
        mEmojisCount.setText(emojisCount);
    }

    /**
     * 设置退出登入
     */
    private void setQuitLogin(){
        mQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(UserConst.USER_DATA, MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(UserConst.USER_LOGIN_STATE, UserConst.USER_LOGIN_FALSE);
                //edit.putString(UserConst.USER_ACCOUNT, mEditAccount.getText().toString());
                //edit.putString(UserConst.USER_PASSWORD, mEditPassword.getText().toString());
                edit.apply();
                finish();
            }
        });
    }

    /**
     * 获取用户的登入信息
     * @return 用户账号名
     */
    private String getUserAccount(){
        SharedPreferences preferences = getSharedPreferences(UserConst.USER_DATA, MODE_PRIVATE);
        return preferences.getString(UserConst.USER_ACCOUNT, " ");
    }

}
