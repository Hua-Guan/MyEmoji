package pri.guanhua.myemoji;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.textclassifier.TextLinks;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pri.guanhua.myemoji.model.bean.EmojiAlbumBean;
import pri.guanhua.myemoji.model.bean.UserAlbumBean;
import pri.guanhua.myemoji.model.dao.EmojisDao;
import pri.guanhua.myemoji.model.database.AppDatabase;
import pri.guanhua.myemoji.model.entity.EmojiAlbumEntity;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;
import pri.guanhua.myemoji.service.EmojiUploadService;
import pri.guanhua.myemoji.view.UserConst;
import pri.guanhua.myemoji.view.avatar.ChooseAvatarActivity;
import pri.guanhua.myemoji.view.cloud.CloudEmojiActivity;
import pri.guanhua.myemoji.view.login.LoginActivity;
import pri.guanhua.myemoji.view.market.EmojiMarketActivity;
import pri.guanhua.myemoji.view.person.PersonalInfoActivity;

public class MainActivity extends AppCompatActivity {

    private static final String URL_CLOUD_EMOJIS_COUNT = UserConst.URL + UserConst.USER_EMOJIS_COUNT;

    private FrameLayout mContainer = null;
    //Toolbar
    private Toolbar mToolbar = null;
    //????????????
    private DrawerLayout mDrawerLayout = null;
    //handle
    private final Handler mHandler = new Handler(Looper.myLooper());
    //viewModel
    private AppViewModel mAppViewModel = null;
    //????????????view
    NavigationView navView = null;
    //???????????????
    private Menu mMenu = null;
    //???????????????
    private TextView mEmojisCount;
    /**
     * ????????????emoji_album?????????????????????????????????????????????????????????
     * ????????????emojis????????????????????????????????????????????????????????????????????????
     * ??????????????????emoji_album???
     */
    private String mUserPosition = "EMOJI_ALBUM";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setToolbar();
        setUserPositionObserver();
        //?????????????????????view????????????item?????????
        setNavigationItemSelectedListener();
        //?????????????????????
        getPermission();
    }

    private void initView(){
        if (mContainer == null) {
            mContainer = findViewById(R.id.content_frame);
        }
        if (mToolbar == null){
            mToolbar = findViewById(R.id.toolbar);
        }
        if (mDrawerLayout == null){
            mDrawerLayout = findViewById(R.id.drawer_layout);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setToolbar(){
        setSupportActionBar(mToolbar);
        //????????????????????????????????????
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //????????????????????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //???toolbar??????????????????
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration  build = new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(mDrawerLayout).build();
        NavigationUI.setupWithNavController(mToolbar, navController, build);
        //???????????????????????????
        navView = findViewById(R.id.left_drawer);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        mMenu = menu;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add){
            if (mUserPosition.equals("EMOJI_ALBUM")){
                showCreateEmojiAlbumDialog();
            }else if (mUserPosition.equals("EMOJIS")){
                getUserAlbum();
                //??????????????????
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                assert navHostFragment != null;
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.action_emojisFragment_to_userAlbumFragment);
            }else if (mUserPosition.equals("USER_ALBUM")){

            }else if (mUserPosition.equals("USER_IMAGES")){
                //?????????????????????
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                assert navHostFragment != null;
                NavController navController = navHostFragment.getNavController();
                navController.popBackStack(R.id.emojisFragment, false);
                //????????????
                mAppViewModel.getSaveEmojiLiveData().setValue("SAVE_EMOJI");
            }
        }else if (item.getItemId() == R.id.upload){
            Intent intent = new Intent(this, EmojiUploadService.class);
            startService(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showCreateEmojiAlbumDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_create_emoji_album, null);
        dialog.setView(dialogView);
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        EditText editText = dialogView.findViewById(R.id.edit_emoji_title);
        dialogView.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDatabase instance = AppDatabase.getInstance(MainActivity.this);
                EmojiAlbumEntity entity = new EmojiAlbumEntity();
                entity.id = 0;
                entity.emojiAlbumTitle = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        instance.emojiAlbumDao().insertAll(entity);
                        //????????????
                        mHandler.post(() -> Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show());
                        //??????dialog??????gridview
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                                if (mAppViewModel == null){
                                    mAppViewModel = new ViewModelProvider(MainActivity.this).get(AppViewModel.class);
                                }
                                mAppViewModel.getEmojiAlbumAddLiveData().setValue("UPDATE_EMOJI_ALBUM");
                            }
                        });
                    }
                }).start();
            }
        });
        dialog.show();
    }

    private void setUserPositionObserver(){
        if (mAppViewModel == null){
            mAppViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        }
        Observer<String> observer = new Observer<String>(){
            @Override
            public void onChanged(String s) {
                mUserPosition = s;
                upDateMenuState();
            }
        };
        mAppViewModel.getUserPositionLiveData().observe(this, observer);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void getUserAlbum(){
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
        //????????????????????????
        Cursor cursor = getApplicationContext().getContentResolver().query(
                collection,
                projection,
                null,
                null,
                null
        );
        HashSet<String> userAlbum = new HashSet<>();

        while (cursor.moveToNext()){
            userAlbum.add(cursor.getString(1));
        }
        //?????????????????????????????????????????????????????????
        List<UserAlbumBean> list = new ArrayList<>();
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " == ?";
        int id = 0;
        for (String item : userAlbum){
            String[] selectionArgs = new String[]{item};
            Cursor cursorFistUriAndCount = getApplicationContext().getContentResolver().query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );
            UserAlbumBean bean = new UserAlbumBean();
            int count = 0; //?????????
            bean.setUserAlbumTitle(item);
            bean.setUserAlbumId(id);
            while (cursorFistUriAndCount.moveToNext()){
                //????????????????????????????????????
                if (bean.getUserAlbumUri() == null){
                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            cursorFistUriAndCount.getLong(0)
                    );
                    bean.setUserAlbumUri(contentUri.toString());
                }
                count++;
                bean.setUserAlbumCount(String.valueOf(count));
            }
            id++;
            list.add(bean);
        }
        //??????ViewModel???list?????????UserAlbumFragment
        if (mAppViewModel == null){
            mAppViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        }
        mAppViewModel.getUserAlbumListMutableLiveData().setValue(list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserAvatar();
        setUserName();
        setEmojiCount();
        //??????????????????
        setUserLogin();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(UserConst.USER_POSITION, mUserPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUserPosition = savedInstanceState.getString(UserConst.USER_POSITION);
    }

    /**
     * ????????????
     */
    private void setUserAvatar(){
        ImageView avatar = navView.getHeaderView(0).findViewById(R.id.user_avatar);
        avatar.setClipToOutline(true);
        String path = getExternalFilesDir("Avatar").getPath();
        File file = new File(path, "default.jpg");
        if (file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            avatar.setImageBitmap(bitmap);
        }
        //??????????????????activity
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????????????????????????????????????????
                if (!detectHasLoginState()){
                    Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, ChooseAvatarActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void setEmojiCount(){
        mEmojisCount = navView.getHeaderView(0).findViewById(R.id.emojis_count);
        if (detectHasLoginState()){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add(UserConst.USER_ACCOUNT, getUserAccount())
                    .build();
            Request request = new Request.Builder()
                    .url(URL_CLOUD_EMOJIS_COUNT)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mEmojisCount.setText(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * ???????????????
     */
    private void setUserName(){
        TextView account = navView.getHeaderView(0).findViewById(R.id.user_name);
        if (detectHasLoginState()){
            account.setText(getUserAccount());
        }else {
            account.setText("???????????????");
        }
    }

    /**
     * ???????????????????????????
     * @return ???????????????
     */
    private String getUserAccount(){
        SharedPreferences preferences = getSharedPreferences(UserConst.USER_DATA, MODE_PRIVATE);
        return preferences.getString(UserConst.USER_ACCOUNT, " ");
    }

    /**
     * ?????????????????????????????????
     */
    private void setUserLogin(){
        TextView login = navView.getHeaderView(0).findViewById(R.id.user_name);
        if (detectHasLoginState()){
            //login.setOnClickListener(null);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                    intent.putExtra(UserConst.USER_ACCOUNT, getUserAccount());
                    intent.putExtra(UserConst.USER_EMOJIS_COUNT, mEmojisCount.getText().toString());
                    startActivity(intent);
                }
            });
        }else {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * ???????????????????????????????????????
     */
    private void setNavigationItemSelectedListener(){
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.cloud_emoji){
                    if (detectHasLoginState()){
                        Intent intent = new Intent(MainActivity.this, CloudEmojiActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                }else if (item.getItemId() == R.id.emoji_market){
                    if (detectHasLoginState()){
                        Intent intent = new Intent(MainActivity.this, EmojiMarketActivity.class);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
    }

    /**
     * ????????????????????????sharePreferences??????????????????
     */
    private boolean detectHasLoginState(){
        SharedPreferences sp = getSharedPreferences(UserConst.USER_DATA, MODE_PRIVATE);
        String state = sp.getString(UserConst.USER_LOGIN_STATE, UserConst.USER_LOGIN_FALSE);
        return state.equals(UserConst.USER_LOGIN_TRUE);
    }

    /**
     * ?????????????????????
     */
    private void upDateMenuState(){
        if (mUserPosition.equals("EMOJI_ALBUM")){
            mMenu.findItem(R.id.upload).setVisible(true);
            mMenu.findItem(R.id.add).setVisible(true);
            mMenu.findItem(R.id.add).setIcon(R.drawable.ic_add);
        }else if (mUserPosition.equals("EMOJIS")){
            mMenu.findItem(R.id.upload).setVisible(false);
            mMenu.findItem(R.id.add).setVisible(true);
            mMenu.findItem(R.id.add).setIcon(R.drawable.ic_add);
        }else if (mUserPosition.equals("USER_ALBUM")){
            mMenu.findItem(R.id.upload).setVisible(false);
            mMenu.findItem(R.id.add).setVisible(false);
        }else if (mUserPosition.equals("USER_IMAGES")){
            mMenu.findItem(R.id.upload).setVisible(false);
            mMenu.findItem(R.id.add).setVisible(true);
            mMenu.findItem(R.id.add).setIcon(R.drawable.ic_done);
        }
    }

    /**
     * ?????????????????????
     */
    private void getPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

}