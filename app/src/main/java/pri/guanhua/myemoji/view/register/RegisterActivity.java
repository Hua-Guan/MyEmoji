package pri.guanhua.myemoji.view.register;

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
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pri.guanhua.myemoji.BR;
import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.databinding.ActivityRegisterBinding;
import pri.guanhua.myemoji.model.bean.UserBean;
import pri.guanhua.myemoji.view.UserConst;

public class RegisterActivity extends AppCompatActivity {

    private static final String URL = "http://172.20.10.9:8080/" + UserConst.USER_REGISTER;

    private Handler mHandler = new Handler(Looper.myLooper());

    private ActivityRegisterBinding mBinding = null;

    private UserBean mUserBean = new UserBean();

    private ImageView mBack = null;

    private EditText mEditAccount = null;
    private EditText mEditPassword = null;
    private EditText mEditConfirmPassword = null;
    private boolean isAccountMatch = false;
    private boolean isPasswordMatch = false;
    private boolean isTwicePasswordMatch = false;
    private Button mRegister = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mBinding.setUser(mUserBean);
        //setContentView(R.layout.activity_register);
        setStatusBar();
        initView();
        setRegister();
        setBack();
        setTextChangeListener();
    }

    private void initView(){
        mBack = findViewById(R.id.img_back);
        mEditAccount = findViewById(R.id.edit_account);
        mEditPassword = findViewById(R.id.edit_password);
        mEditConfirmPassword = findViewById(R.id.edit_confirm);
        mRegister = findViewById(R.id.btn_register);
    }

    private void setRegister(){
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTwicePasswordMatch = mBinding.editPassword.getText().toString().equals(mBinding.editConfirm.getText().toString());
                if (isTwicePasswordMatch&&isPasswordMatch&&isAccountMatch) {
                    //更新校验状态
                    mBinding.matchAccount.setText(" ");
                    mBinding.matchPassword.setText(" ");
                    mBinding.matchConfirmPassword.setText(" ");
                    //发送请求
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("uaccount", mEditAccount.getText().toString())
                            .add("upassword", mEditPassword.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url(URL)
                            .post(body)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            assert response.body() != null;
                            String responseStr = response.body().string();
                            if (responseStr.equals(UserConst.USER_REGISTER_ALREADY_EXIST)) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, "账号已存在", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (responseStr.equals(UserConst.USER_REGISTER_SUCCESS)) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }else if (!isTwicePasswordMatch){
                    mBinding.matchConfirmPassword.setText("两次密码不一致");
                }
            }
        });
    }

    private void setBack(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setStatusBar(){
        //把状态栏的文字设置为黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //把状态栏设置透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }


    /**
     * 验证密码和账号是否符合规则
     */
    private void setTextChangeListener(){
        mUserBean.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.account){
                    isAccountMatch = isMatchAccount(mUserBean.getAccount());
                    if (!isAccountMatch){
                        mBinding.matchAccount.setText("只能输入数字下划线英文字母或中文");
                    }else {
                        mBinding.matchAccount.setText(" ");
                    }
                }else if (propertyId == BR.password) {
                    isTwicePasswordMatch = mBinding.editPassword.getText().toString().equals(mBinding.editConfirm.getText().toString());
                    isPasswordMatch = isMatchPassword(mUserBean.getPassword());
                    if (!isPasswordMatch) {
                        mBinding.matchPassword.setText("只能输入英文字母和数字");
                    }else {
                        mBinding.matchPassword.setText(" ");
                    }
                    if (isTwicePasswordMatch){
                        mBinding.matchConfirmPassword.setText(" ");
                    }
                }else if (propertyId == BR.confirmPassword){
                    isTwicePasswordMatch = mBinding.editPassword.getText().toString().equals(mBinding.editConfirm.getText().toString());
                    if (!isTwicePasswordMatch){
                        mBinding.matchConfirmPassword.setText("两次密码不一致");
                    }else {
                        mBinding.matchConfirmPassword.setText(" ");
                    }
                }
            }
        });
    }

    /**
     * 通过正则表达式匹配账号，账号只能由中文英文字母和数字及下划线组成。
     * @return
     */
    private boolean isMatchAccount(String account){
        return Pattern.matches("^\\w+$", account);
    }

    /**
     * 通过正则表达式匹配密码，密码只能由26个英文字母和数字组成。
     * @return
     */
    private boolean isMatchPassword(String password){
        return Pattern.matches("^[A-Za-z0-9]+$", password);
    }

}
