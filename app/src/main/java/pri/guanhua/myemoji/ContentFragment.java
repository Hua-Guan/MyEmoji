package pri.guanhua.myemoji;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import pri.guanhua.myemoji.model.adapter.EmojiAlbumAdapter;
import pri.guanhua.myemoji.model.bean.EmojiAlbumBean;

public class ContentFragment extends Fragment {

    private View mView = null;

    //表情包专辑
    private GridView mGridEmojiAlbum = null;
    //表情包专辑适配器
    private EmojiAlbumAdapter mAdapter = null;

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
    }

    private void initView(){
        if (mGridEmojiAlbum == null){
            mGridEmojiAlbum = mView.findViewById(R.id.grid_emoji_album);
        }
    }

    private void setGridEmojiAlbum(){
        if (mAdapter == null){
            List<EmojiAlbumBean> list = new ArrayList<>();
            EmojiAlbumBean bean = new EmojiAlbumBean();
            bean.setEmojiAlbumUri(null);
            bean.setEmojiAlbumTitle(null);
            bean.setEmojiAlbumCount(null);
            list.add(bean);
            for (int i = 0; i< 23; i++){
                list.add(bean);
            }
            mAdapter = new EmojiAlbumAdapter(list, getContext());
            mGridEmojiAlbum.setAdapter(mAdapter);
        }
    }
}
