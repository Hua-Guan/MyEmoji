package pri.guanhua.myemoji.view.avatar;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.textclassifier.TextLinks;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
import pri.guanhua.myemoji.model.adapter.AvatarAdapter;
import pri.guanhua.myemoji.model.bean.AvatarBean;
import pri.guanhua.myemoji.utils.MyUtils;
import pri.guanhua.myemoji.view.UserConst;

public class ChooseAvatarActivity extends AppCompatActivity {

    private static final String URL_UPLOAD_AVATAR = UserConst.URL + UserConst.USER_UPLOAD_AVATAR;
    private static final String URL_UPDATE_AVATAR = UserConst.URL + UserConst.USER_UPDATE_AVATAR;

    private Handler mHandler = new Handler(Looper.myLooper());
    private GridView mGridView = null;
    private ImageView mBack = null;
    private List<AvatarBean> list = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_choose_avatar);
        setStatusBar();
        initView();
        setGridView();
        setBack();
        try {
            setUploadAvatar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setGridViewMargin();
    }

    private void initView(){
        mGridView = findViewById(R.id.grid_choose_avatar);
        mBack = findViewById(R.id.img_back);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setStatusBar(){
        //把状态栏的文字设置为黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //把状态栏设置透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    private void setGridView(){
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME,
        };
        //获取所有的相册名
        Cursor cursor = getApplicationContext().getContentResolver().query(
                collection,
                projection,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            AvatarBean bean = new AvatarBean();
            bean.setId((int) cursor.getLong(0));
            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getLong(0)
            );
            bean.setUri(contentUri.toString());
            list.add(bean);
        }
        //
        AvatarAdapter adapter = new AvatarAdapter(list, this);
        mGridView.setAdapter(adapter);
    }

    /**
     * 设置上传头像，并把数据同步到数据库
     */
    private void setUploadAvatar(){
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContentResolver resolver = getApplicationContext()
                        .getContentResolver();
                try {
                    //获取字节数组
                    InputStream stream = resolver.openInputStream(Uri.parse(list.get(position).getUri()));
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    while ((len = stream.read(buffer)) != -1){
                        bos.write(buffer, 0, len);
                    }
                    bos.flush();
                    stream.close();
                    //开启上传
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(UserConst.USER_ACCOUNT, getUserAccount())
                            .addFormDataPart("file", "user_avatar.jpg",
                                    RequestBody.create(MediaType.parse("multipart/form-data"), bos.toByteArray()))
                            .build();
                    Request request = new Request.Builder()
                            .url(URL_UPLOAD_AVATAR)
                            .post(requestBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            bos.close();
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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

    private void setBack(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 设置gridview的外边距
     */
    private void setGridViewMargin(){
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        float mar = ((float) width - MyUtils.dp2px(this, 323))/2;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) mar,10, (int) mar,10);
        mGridView.setLayoutParams(params);
    }

}
