package pri.guanhua.myemoji.view.cloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.adapter.CloudEmojiAdapter;
import pri.guanhua.myemoji.model.bean.CloudEmojiBean;
import pri.guanhua.myemoji.utils.MyUtils;
import pri.guanhua.myemoji.view.UserConst;

public class CloudEmojiActivity extends AppCompatActivity {

    private static final String URL_CLOUD_EMOJI = UserConst.URL + UserConst.USER_CLOUD_EMOJI;

    private ImageView mBack = null;
    private GridView mGridView = null;

    private final Handler mHandler = new Handler(Looper.myLooper());

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_emoji);
        setStatusBar();
        initView();
        setBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGridView();
        setGridViewMargin();
    }

    private void initView(){
        mBack = findViewById(R.id.img_back);
        mGridView = findViewById(R.id.grid_emojis);
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

    /**
     * 从服务器获取用户上传的表情包，并显示在gridview中。
     */
    private void setGridView(){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add(UserConst.USER_ACCOUNT, getUserAccount())
                .build();
        Request request = new Request.Builder()
                .url(URL_CLOUD_EMOJI)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    List<CloudEmojiBean> list = new ArrayList<>();
                    assert response.body() != null;
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    Gson gson = new Gson();
                    for (int i = 0; i < jsonArray.length(); i++){
                        CloudEmojiBean bean = gson.fromJson(jsonArray.getString(i), CloudEmojiBean.class);
                        list.add(bean);
                    }
                    //
                    CloudEmojiAdapter adapter = new CloudEmojiAdapter(list, CloudEmojiActivity.this);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mGridView.setAdapter(adapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    /**
     * 设置gridview的边距
     */
    private void setGridViewMargin(){
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        float mar = ((float) width - MyUtils.dp2px(this, 331))/2;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) mar,10, (int) mar,10);
        mGridView.setLayoutParams(params);
    }

}
