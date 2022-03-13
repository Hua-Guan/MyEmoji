package pri.guanhua.myemoji;

import android.annotation.SuppressLint;
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
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pri.guanhua.myemoji.model.adapter.EmojiAlbumAdapter;
import pri.guanhua.myemoji.model.bean.EmojiAlbumBean;
import pri.guanhua.myemoji.model.database.AppDatabase;
import pri.guanhua.myemoji.model.entity.EmojiAlbumEntity;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

public class MainActivity extends AppCompatActivity {

    private ContentFragment mContentFragment = null;
    private FrameLayout mContainer = null;
    //fragment管理器
    private FragmentManager mSupportFragmentManager = null;
    //Toolbar
    private Toolbar mToolbar = null;
    //抽屉容器
    private DrawerLayout mDrawerLayout = null;
    //handle
    private Handler mHandler = new Handler(Looper.myLooper());
    //viewModel
    private AppViewModel mAppViewModel = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setToolbar();
        setDrawerLayout();
        setContentFragment();
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
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //把状态栏的文字设置为黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //把状态栏设置透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    private void setDrawerLayout(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        toggle.syncState();
        mDrawerLayout.addDrawerListener(toggle);
    }

    private void setContentFragment(){
        if (mContentFragment == null){
            mContentFragment = new ContentFragment();
        }
        if (mSupportFragmentManager == null){
            mSupportFragmentManager = getSupportFragmentManager();
        }
        FragmentTransaction fragmentTransaction = mSupportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, mContentFragment).commit();
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