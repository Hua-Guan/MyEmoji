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

    private static final String URL = UserConst.URL + UserConst.USER_REGISTER;

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
                    //??????????????????
                    mBinding.matchAccount.setText(" ");
                    mBinding.matchPassword.setText(" ");
                    mBinding.matchConfirmPassword.setText(" ");
                    //????????????
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
                                    Toast.makeText(RegisterActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(RegisterActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (responseStr.equals(UserConst.USER_REGISTER_SUCCESS)) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }else if (!isTwicePasswordMatch){
                    mBinding.matchConfirmPassword.setText("?????????????????????");
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
        //????????????????????????????????????
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //????????????????????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }


    /**
     * ???????????????????????????????????????
     */
    private void setTextChangeListener(){
        mUserBean.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.account){
                    isAccountMatch = isMatchAccount(mUserBean.getAccount());
                    if (!isAccountMatch){
                        mBinding.matchAccount.setText("????????????????????????????????????????????????");
                    }else {
                        mBinding.matchAccount.setText(" ");
                    }
                }else if (propertyId == BR.password) {
                    isTwicePasswordMatch = mBinding.editPassword.getText().toString().equals(mBinding.editConfirm.getText().toString());
                    isPasswordMatch = isMatchPassword(mUserBean.getPassword());
                    if (!isPasswordMatch) {
                        mBinding.matchPassword.setText("?????????????????????????????????");
                    }else {
                        mBinding.matchPassword.setText(" ");
                    }
                    if (isTwicePasswordMatch){
                        mBinding.matchConfirmPassword.setText(" ");
                    }
                }else if (propertyId == BR.confirmPassword){
                    isTwicePasswordMatch = mBinding.editPassword.getText().toString().equals(mBinding.editConfirm.getText().toString());
                    if (!isTwicePasswordMatch){
                        mBinding.matchConfirmPassword.setText("?????????????????????");
                    }else {
                        mBinding.matchConfirmPassword.setText(" ");
                    }
                }
            }
        });
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????
     * @return
     */
    private boolean isMatchAccount(String account){
        return Pattern.matches("^\\w+$", account);
    }

    /**
     * ???????????????????????????????????????????????????26?????????????????????????????????
     * @return
     */
    private boolean isMatchPassword(String password){
        return Pattern.matches("^[A-Za-z0-9]+$", password);
    }

}
