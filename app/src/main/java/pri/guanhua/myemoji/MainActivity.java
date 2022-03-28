package pri.guanhua.myemoji;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pri.guanhua.myemoji.model.bean.EmojiAlbumBean;
import pri.guanhua.myemoji.model.bean.UserAlbumBean;
import pri.guanhua.myemoji.model.dao.EmojisDao;
import pri.guanhua.myemoji.model.database.AppDatabase;
import pri.guanhua.myemoji.model.entity.EmojiAlbumEntity;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;
import pri.guanhua.myemoji.view.UserConst;
import pri.guanhua.myemoji.view.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mContainer = null;
    //Toolbar
    private Toolbar mToolbar = null;
    //抽屉容器
    private DrawerLayout mDrawerLayout = null;
    //handle
    private final Handler mHandler = new Handler(Looper.myLooper());
    //viewModel
    private AppViewModel mAppViewModel = null;
    //左边抽屉view
    NavigationView navView = null;
    /**
     * 当用户在emoji_album页面时，工具栏的添加按钮会添加收藏夹；
     * 当用户在emojis页面时，工具栏的添加按钮会进入浏览系统图片页面。
     * 此属性默认为emoji_album。
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
        //设置用户头像
        setUserAvatar();
        //设置用户登入
        setUserLogin();
        //设置左边抽屉式view的菜单的item的监听
        setNavigationItemSelectedListener();
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
        //把状态栏的文字设置为黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //把状态栏设置透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //把toolbar和导航图绑定
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration  build = new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(mDrawerLayout).build();
        NavigationUI.setupWithNavController(mToolbar, navController, build);
        //把抽屉和导航图绑定
        navView = findViewById(R.id.left_drawer);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
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
                //导航到下一页
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                assert navHostFragment != null;
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.action_emojisFragment_to_userAlbumFragment);
            }else if (mUserPosition.equals("USER_ALBUM")){

            }else if (mUserPosition.equals("USER_IMAGES")){
                //回退到表情包页
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                assert navHostFragment != null;
                NavController navController = navHostFragment.getNavController();
                navController.popBackStack(R.id.emojisFragment, false);
                //开始保存
                mAppViewModel.getSaveEmojiLiveData().setValue("SAVE_EMOJI");
            }
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
                        //弹出通知
                        mHandler.post(() -> Toast.makeText(getApplicationContext(), "新建成功", Toast.LENGTH_SHORT).show());
                        //关闭dialog更新gridview
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
        //获取所有的相册名
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
        //获取每个相册的第一张图的路径和图片数量
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
            int count = 0; //计数器
            bean.setUserAlbumTitle(item);
            bean.setUserAlbumId(id);
            while (cursorFistUriAndCount.moveToNext()){
                //设置相册第一张图片的路径
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
        //通过ViewModel把list传递给UserAlbumFragment
        if (mAppViewModel == null){
            mAppViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        }
        mAppViewModel.getUserAlbumListMutableLiveData().setValue(list);
    }

    private void setUserAvatar(){
        ImageView avatar = navView.getHeaderView(0).findViewById(R.id.user_avatar);
        avatar.setClipToOutline(true);
    }

    private void setUserLogin(){
        TextView login = navView.getHeaderView(0).findViewById(R.id.user_name);
        if (detectHasLoginState()){
            login.setOnClickListener(null);
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

    private void setNavigationItemSelectedListener(){
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.cloud_emoji){
                    Toast.makeText(getApplicationContext(), "测试", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    /**
     * 检测登入状态，用sharePreferences来储存状态。
     */
    private boolean detectHasLoginState(){
        SharedPreferences sp = getSharedPreferences(UserConst.USER_DATA, MODE_PRIVATE);
        String state = sp.getString(UserConst.USER_LOGIN_STATE, UserConst.USER_LOGIN_FALSE);
        //return state.equals(UserConst.USER_LOGIN_TRUE);
        return false;
    }

}