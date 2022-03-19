package pri.guanhua.myemoji.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.adapter.UserAlbumAdapter;
import pri.guanhua.myemoji.model.adapter.UserImagesAdapter;
import pri.guanhua.myemoji.model.bean.UserAlbumBean;
import pri.guanhua.myemoji.model.bean.UserImageBean;
import pri.guanhua.myemoji.model.dao.EmojisDao;
import pri.guanhua.myemoji.model.database.AppDatabase;
import pri.guanhua.myemoji.model.entity.EmojisEntity;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

public class UserImagesFragment extends Fragment {

    private View mView = null;

    private GridView mGridView = null;

    private AppViewModel model = null;

    private List<UserImageBean> mList = null;

    private String mUserCurrentSelectedAlbum = "";

    private Handler mHandler = new Handler(Looper.myLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_user_images, container, false);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setGridView();
        setGridViewOnClickListener();
        setUserPosition();
        updateUserCurrentAlbumTitle();
        saveEmojisInDatabase();
    }

    private void initView(){
        if (mGridView == null){
            mGridView = mView.findViewById(R.id.grid_user_image);
        }
    }

    private void setGridView(){
        if (model == null){
            model = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

            Observer<List<UserImageBean>> observer = new Observer<List<UserImageBean>>() {
                @Override
                public void onChanged(List<UserImageBean> userImageBeans) {
                    mList = userImageBeans;
                    UserImagesAdapter adapter = new UserImagesAdapter(mList, UserImagesFragment.this.getContext());
                    mGridView.setAdapter(adapter);
                }
            };
            model.getUserImageListLiveData().observe(getViewLifecycleOwner(), observer);
        }
    }

    private void setGridViewOnClickListener(){
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserImageBean bean = mList.get(position);
                if (bean.isSelected()){
                    bean.setSelected(false);
                }else {
                    bean.setSelected(true);
                }
                UserImagesAdapter adapter = (UserImagesAdapter) mGridView.getAdapter();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setUserPosition(){
        model.getUserPositionLiveData().setValue("USER_IMAGES");
    }

    private void updateUserCurrentAlbumTitle(){
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mUserCurrentSelectedAlbum = s;
            }
        };
        model.getUserCurrentAlbumLiveData().observe(getViewLifecycleOwner(), observer);
    }

    private void saveEmojisInDatabase(){
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EmojisDao emojisDao = AppDatabase.getInstance(getContext()).emojisDao();
                        for (int i = 0 ; i < mList.size(); i++){
                            UserImageBean bean = mList.get(i);
                            if (bean.isSelected()){
                                EmojisEntity entity = new EmojisEntity();
                                entity.id = 0;
                                entity.emojiUri = bean.getImageUri();
                                entity.emojiAlbum = mUserCurrentSelectedAlbum;
                                emojisDao.insertEmojis(entity);
                            }
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //通知表情包保存已经完成
                                model.getOnHasSavedEmojiLiveData().setValue("EMOJI_SAVE_COMPLETE");
                            }
                        });
                    }
                }).start();
            }
        };
        model.getSaveEmojiLiveData().observe(getViewLifecycleOwner(), observer);
    }

}
