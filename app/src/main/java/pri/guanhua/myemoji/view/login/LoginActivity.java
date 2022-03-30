package pri.guanhua.myemoji.view.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.view.UserConst;
import pri.guanhua.myemoji.view.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String URL = "http://172.20.10.9:8080/" + UserConst.USER_LOGIN;

    private ImageView mBack = null;
    private Button mLogin = null;
    private Button mRegister = null;
    private EditText mEditAccount = null;
    private EditText mEditPassword = null;

    private final Handler mHandler = new Handler(Looper.myLooper());

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBar();
        initView();
        setBack();
        setLogin();
        setRegisterListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setStatusBar(){
        //把状态栏的文字设置为黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //把状态栏设置透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    private void initView(){
        mBack = findViewById(R.id.img_back);
        mLogin = findViewById(R.id.btn_login);
        mRegister = findViewById(R.id.btn_register);
        mEditAccount = findViewById(R.id.edit_account);
        mEditPassword = findViewById(R.id.edit_password);
    }

    private void setBack(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setLogin(){
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("uaccount", mEditAccount.getText().toString())
                        .add("upassword", mEditPassword.getText().toString())
                        .build();
                Request request = new Request.Builder()
                        .url(URL)
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        assert response.body() != null;
                        String responseStr = response.body().string();
                        if (responseStr.equals(UserConst.USER_LOGIN_SUCCESS)){
                            SharedPreferences preferences = getSharedPreferences(UserConst.USER_DATA, MODE_PRIVATE);
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putString(UserConst.USER_LOGIN_STATE, UserConst.USER_LOGIN_TRUE);
                            edit.apply();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                            finish();
                        }else if (responseStr.equals(UserConst.USER_PASSWORD_MISTAKE)){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if (responseStr.equals(UserConst.USER_ACCOUNT_NOT_EXIST)){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "账号不存在", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void setRegisterListener(){
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}
