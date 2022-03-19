package pri.guanhua.myemoji.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.adapter.EmojisAdapter;
import pri.guanhua.myemoji.model.bean.EmojisBean;
import pri.guanhua.myemoji.model.dao.EmojisDao;
import pri.guanhua.myemoji.model.database.AppDatabase;
import pri.guanhua.myemoji.model.entity.EmojisEntity;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

public class EmojisFragment extends Fragment {

    private View mView = null;

    private GridView mEmojisGridView = null;

    private AppViewModel model = null;

    private String mUserCurrentAlbum = "";

    private Handler mHandler = new Handler(Looper.myLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_emojis, container, false);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setEmojisGridView();
        updateUserPositionState();
        updateUserCurrentAlbum();
        setOnEmojiSaveComplete();
    }

    private void initView(){
        if (mEmojisGridView == null){
            mEmojisGridView = mView.findViewById(R.id.grid_emojis);
        }
    }
    /**
     * 更新用户的位置状态
     */
    private void updateUserPositionState(){
        if (model == null){
            model = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        }
        model.getUserPositionLiveData().setValue("EMOJIS");
    }

    /**
     * 当用户返回时更新用户的位置状态
     */
    private void updateUserPositionStateOnUserBack(){
        if (model == null){
            model = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        }
        model.getUserPositionLiveData().setValue("EMOJI_ALBUM");
    }

    @Override
    public void onPause() {
        super.onPause();
        updateUserPositionStateOnUserBack();
    }

    private void updateUserCurrentAlbum(){
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mUserCurrentAlbum = s;
            }
        };
        model.getUserCurrentAlbumLiveData().observe(getViewLifecycleOwner(), observer);
    }

    private void setOnEmojiSaveComplete(){
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setEmojisGridView();
            }
        };
        model.getOnHasSavedEmojiLiveData().observe(getViewLifecycleOwner(), observer);
    }

    private void setEmojisGridView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmojisDao emojisDao = AppDatabase.getInstance(getContext()).emojisDao();
                List<EmojisEntity> emojis = emojisDao.getEmojisByAlbum(mUserCurrentAlbum);
                List<EmojisBean> list = new ArrayList<>();
                for (int i = 0; i < emojis.size(); i++){
                    EmojisBean bean = new EmojisBean();
                    bean.setId(emojis.get(i).id);
                    bean.setEmojiUri(emojis.get(i).emojiUri);
                    list.add(bean);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        EmojisAdapter adapter = new EmojisAdapter(getContext(), list);
                        mEmojisGridView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }
}
