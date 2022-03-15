package pri.guanhua.myemoji.view;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.viewmodel.AppViewModel;

public class EmojisFragment extends Fragment {

    private View mView = null;

    private GridView mEmojisGridView = null;

    private AppViewModel model = null;

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
        updateUserPositionState();
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

}
