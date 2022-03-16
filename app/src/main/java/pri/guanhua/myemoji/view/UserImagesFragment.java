package pri.guanhua.myemoji.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

public class UserImagesFragment extends Fragment {

    private View mView = null;

    private GridView mGridView = null;

    private AppViewModel model = null;

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
    }

    private void initView(){
        if (mGridView == null){
            mGridView = mView.findViewById(R.id.grid_user_image);
        }
    }

    private void setGridView(){
        if (model == null){
            model = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        }
        Observer<List<UserImageBean>> observer = new Observer<List<UserImageBean>>() {
            @Override
            public void onChanged(List<UserImageBean> userImageBeans) {
                UserImagesAdapter adapter = new UserImagesAdapter(userImageBeans, UserImagesFragment.this.getContext());
                mGridView.setAdapter(adapter);
            }
        };
        model.getUserImageListLiveData().observe(getViewLifecycleOwner(), observer);
    }

}
