package pri.guanhua.myemoji.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

public class UserAlbumFragment extends Fragment {

    private View mView = null;

    private AppViewModel model = null;

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
        updateUserPositionState();
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
}
