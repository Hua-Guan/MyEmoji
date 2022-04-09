package pri.guanhua.myemoji.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import pri.guanhua.myemoji.MainActivity;
import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.adapter.EmojiAlbumAdapter;
import pri.guanhua.myemoji.model.bean.EmojiAlbumBean;
import pri.guanhua.myemoji.model.dao.EmojiAlbumDao;
import pri.guanhua.myemoji.model.dao.EmojisDao;
import pri.guanhua.myemoji.model.database.AppDatabase;
import pri.guanhua.myemoji.model.entity.EmojiAlbumEntity;
import pri.guanhua.myemoji.model.entity.EmojisEntity;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;
import pri.guanhua.myemoji.utils.MyUtils;

public class EmojiAlbumFragment extends Fragment {

    private View mView = null;

    private final Handler mHandler = new Handler(Looper.myLooper());

    //表情包专辑
    private GridView mGridEmojiAlbum = null;
    //表情包专辑适配器
    private EmojiAlbumAdapter mAdapter = null;
    private List<EmojiAlbumBean> mList;
    private AppViewModel model = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_content, container, false);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setGridEmojiAlbum();
        setOnEmojiAlbumAddObserver();
        setOnGridViewItemClickListener();
        setGridEmojiAlbumMargin();
    }

    private void initView(){
        if (mGridEmojiAlbum == null){
            mGridEmojiAlbum = mView.findViewById(R.id.grid_emoji_album);
        }
    }

    private void setGridEmojiAlbum(){
        if (mAdapter == null){
            setAdapter();
        }
    }

    private void setOnEmojiAlbumAddObserver(){
        //要做一个判空，不然会设置多个观察者。
        if (model == null) {
            model = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        }
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setAdapter();
            }
        };
        model.getEmojiAlbumAddLiveData().observe(getViewLifecycleOwner(), observer);
    }

    private void setOnGridViewItemClickListener(){
        mGridEmojiAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ContextCompat.checkSelfPermission(EmojiAlbumFragment.this.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EmojiAlbumFragment.this.requireActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
                //更新用户选择的收藏夹
                model.getUserCurrentAlbumLiveData().setValue(mList.get(position).getEmojiAlbumTitle());
                //导航到下一页
                Navigation.findNavController(mGridEmojiAlbum).navigate(R.id.action_emojiAlbumFragment_to_emojisFragment);
            }
        });
    }

    private void setAdapter(){
        mList = new ArrayList<>();
        EmojiAlbumDao emojiAlbumDao = AppDatabase.getInstance(getContext()).emojiAlbumDao();
        EmojisDao emojisDao = AppDatabase.getInstance(getContext()).emojisDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<EmojiAlbumEntity> all = emojiAlbumDao.getAll();
                for (int i = 0; i < all.size(); i++){
                    List<EmojisEntity> emojis = emojisDao.getEmojisByAlbum(all.get(i).emojiAlbumTitle);
                    EmojiAlbumBean bean = new EmojiAlbumBean();
                    if (emojis.size()>0){
                        bean.setEmojiAlbumUri(emojis.get(0).emojiUri);
                        bean.setEmojiAlbumCount(String.valueOf(emojis.size()));
                    }else {
                        bean.setEmojiAlbumUri(null);
                        bean.setEmojiAlbumCount(null);
                    }
                    bean.setEmojiAlbumTitle(all.get(i).emojiAlbumTitle);
                    mList.add(bean);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new EmojiAlbumAdapter(mList, getContext());
                        mGridEmojiAlbum.setAdapter(mAdapter);
                    }
                });
            }
        }).start();
    }

    /**
     * 设置gridview的外边距
     */
    private void setGridEmojiAlbumMargin(){
        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        float mar = ((float) width - MyUtils.dp2px(getContext(), 328))/2;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) mar,10, (int) mar,10);
        mGridEmojiAlbum.setLayoutParams(params);
    }

}
