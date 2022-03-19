package pri.guanhua.myemoji.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import pri.guanhua.myemoji.model.database.AppDatabase;
import pri.guanhua.myemoji.model.entity.EmojiAlbumEntity;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

public class EmojiAlbumFragment extends Fragment {

    private View mView = null;

    private final Handler mHandler = new Handler(Looper.myLooper());

    //表情包专辑
    private GridView mGridEmojiAlbum = null;
    //表情包专辑适配器
    private EmojiAlbumAdapter mAdapter = null;
    private List<EmojiAlbumBean> mList = new ArrayList<>();
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
    }

    private void initView(){
        if (mGridEmojiAlbum == null){
            mGridEmojiAlbum = mView.findViewById(R.id.grid_emoji_album);
        }
    }

    private void setGridEmojiAlbum(){
        if (mAdapter == null){
            EmojiAlbumDao emojiAlbumDao = AppDatabase.getInstance(getContext()).emojiAlbumDao();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<EmojiAlbumEntity> all = emojiAlbumDao.getAll();
                    for (int i = 0; i < all.size(); i++){
                        EmojiAlbumBean bean = new EmojiAlbumBean();
                        bean.setEmojiAlbumUri(null);
                        bean.setEmojiAlbumTitle(all.get(i).emojiAlbumTitle);
                        bean.setEmojiAlbumCount(null);
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
    }

    private void setOnEmojiAlbumAddObserver(){
        //要做一个判空，不然会设置多个观察者。
        if (model == null) {
            model = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
            Observer<EmojiAlbumBean> observer = new Observer<EmojiAlbumBean>() {
                @Override
                public void onChanged(EmojiAlbumBean emojiAlbumBean) {
                    mList.add(emojiAlbumBean);
                    mAdapter.notifyDataSetChanged();
                }
            };
            model.getEmojiAlbumAddLiveData().observe(getViewLifecycleOwner(), observer);
        }
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
                Navigation.findNavController(mGridEmojiAlbum).navigate(R.id.action_emojiAlbumFragment_to_emojisFragment);
            }
        });
    }
}
