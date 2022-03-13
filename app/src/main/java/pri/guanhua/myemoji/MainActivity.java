package pri.guanhua.myemoji;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import pri.guanhua.myemoji.model.bean.EmojiAlbumBean;
import pri.guanhua.myemoji.model.database.AppDatabase;
import pri.guanhua.myemoji.model.entity.EmojiAlbumEntity;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setToolbar();
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
        NavigationView navView = findViewById(R.id.left_drawer);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add){
            showCreateEmojiAlbumDialog();
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
                                EmojiAlbumBean bean = new EmojiAlbumBean();
                                bean.setEmojiAlbumUri(null);
                                bean.setEmojiAlbumTitle(editText.getText().toString());
                                bean.setEmojiAlbumCount(null);
                                mAppViewModel.getEmojiAlbumAddLiveData().setValue(bean);
                            }
                        });
                    }
                }).start();
            }
        });
        dialog.show();
    }
}