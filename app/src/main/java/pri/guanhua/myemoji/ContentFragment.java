package pri.guanhua.myemoji;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import pri.guanhua.myemoji.model.adapter.EmojiAlbumAdapter;
import pri.guanhua.myemoji.model.bean.EmojiAlbumBean;
import pri.guanhua.myemoji.model.dao.EmojiAlbumDao;
import pri.guanhua.myemoji.model.database.AppDatabase;
import pri.guanhua.myemoji.model.entity.EmojiAlbumEntity;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

public class ContentFragment extends Fragment {

    private View mView = null;

    private final Handler mHandler = new Handler(Looper.myLooper());

    //表情包专辑
    private GridView mGridEmojiAlbum = null;
    //表情包专辑适配器
    private EmojiAlbumAdapter mAdapter = null;
    List<EmojiAlbumBean> mList = new ArrayList<>();
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
        if (model == null){
            model = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        }
        Observer<EmojiAlbumBean> observer = new Observer<EmojiAlbumBean>(){
            @Override
            public void onChanged(EmojiAlbumBean emojiAlbumBean) {
                mList.add(emojiAlbumBean);
                mAdapter.notifyDataSetChanged();
            }
        };
        model.getEmojiAlbumAddLiveData().observe(getViewLifecycleOwner(), observer);
    }

}
