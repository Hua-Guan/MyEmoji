package pri.guanhua.myemoji.view;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.adapter.UserAlbumAdapter;
import pri.guanhua.myemoji.model.bean.UserAlbumBean;
import pri.guanhua.myemoji.model.bean.UserImageBean;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

public class UserAlbumFragment extends Fragment {

    private View mView = null;

    private AppViewModel model = null;

    private ListView mListView = null;

    private UserAlbumAdapter mAdapter = null;

    private List<UserAlbumBean> mUserAlbumBeanList;

    private List<UserImageBean> mUserImageList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_user_album, container, false);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        updateUserPositionState();
        setListView();
        setListViewOnClickListener();
    }

    /**
     * 更新用户的位置状态
     */
    private void updateUserPositionState(){
        if (model == null){
            model = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        }
        model.getUserPositionLiveData().setValue("USER_ALBUM");
    }

    private void initView(){
        if (mListView == null){
            mListView = mView.findViewById(R.id.list_user_album);
        }
    }

    private void setListView(){
        if (model == null){
            model = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        }
        Observer<List<UserAlbumBean>> observer = new Observer<List<UserAlbumBean>>() {
            @Override
            public void onChanged(List<UserAlbumBean> userAlbumBeans) {
                mUserAlbumBeanList = userAlbumBeans;
                UserAlbumAdapter adapter = new UserAlbumAdapter(userAlbumBeans, UserAlbumFragment.this.getContext());
                mListView.setAdapter(adapter);
            }
        };
        model.getUserAlbumListMutableLiveData().observe(getViewLifecycleOwner(), observer);
    }

    private void setListViewOnClickListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<UserImageBean> beanList = getImagesByAlbumTitle(mUserAlbumBeanList.get(position).getUserAlbumTitle());
                //把beanList传递给UserImagesFragment
                if (model == null){
                    model = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
                }
                model.getUserImageListLiveData().setValue(beanList);
                //导航到UserImagesFragment
                Navigation.findNavController(mListView).navigate(R.id.action_userAlbumFragment_to_userImagesFragment);
            }
        });
    }

    private List<UserImageBean> getImagesByAlbumTitle(String albumTitle){
        mUserImageList = new ArrayList<>();

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
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " == ?";
        String[] selectionArgs = new String[]{albumTitle};
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                null
        );
        while (cursor.moveToNext()){
            UserImageBean bean = new UserImageBean();
            //设置id
            bean.setImageId(cursor.getInt(0));
            //设置图片路径
            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getLong(0)
            );
            bean.setImageUri(contentUri.toString());
            mUserImageList.add(bean);
        }
        return mUserImageList;
    }

}
