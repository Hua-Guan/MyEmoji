package pri.guanhua.myemoji.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.adapter.UserAlbumAdapter;
import pri.guanhua.myemoji.model.bean.UserAlbumBean;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

public class UserAlbumFragment extends Fragment {

    private View mView = null;

    private AppViewModel model = null;

    private ListView mListView = null;

    private UserAlbumAdapter mAdapter = null;

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
                UserAlbumAdapter adapter = new UserAlbumAdapter(userAlbumBeans, UserAlbumFragment.this.getContext());
                mListView.setAdapter(adapter);
            }
        };
        model.getUserAlbumListMutableLiveData().observe(getViewLifecycleOwner(), observer);
    }

}
