package pri.guanhua.myemoji.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

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
import pri.guanhua.myemoji.utils.MyUtils;

/**
 * 这个fragment会展示收藏夹所有的表情包
 */
public class EmojisFragment extends Fragment {

    private View mView = null;

    private GridView mEmojisGridView = null;

    private EmojisAdapter mAdapter;

    private AppViewModel model = null;

    private String mUserCurrentAlbum = "";

    private List<EmojisBean> list;

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
        updateUserPositionState();
        updateUserCurrentAlbum();
        setOnEmojiSaveComplete();
        initGridView();
        setEmojisGridViewOnClickListener();
        setGridViewLongClickDelete();
        setGridViewMargin();
    }

    private void initGridView(){
        if (mEmojisGridView == null){
            mEmojisGridView = mView.findViewById(R.id.grid_emojis);
            setEmojisGridView();
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

    /**
     * 为gridview填充数据
     */
    private void setEmojisGridView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmojisDao emojisDao = AppDatabase.getInstance(getActivity()).emojisDao();
                List<EmojisEntity> emojis = emojisDao.getAll();
                list = new ArrayList<>();
                for (int i = 0; i < emojis.size(); i++){
                    if (emojis.get(i).emojiAlbum.equals(mUserCurrentAlbum)){
                        EmojisBean bean = new EmojisBean();
                        bean.setId(emojis.get(i).id);
                        bean.setEmojiUri(emojis.get(i).emojiUri);
                        list.add(bean);
                    }
                }
                mAdapter = new EmojisAdapter(getActivity(), list);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mEmojisGridView.setAdapter(mAdapter);
                    }
                });
            }
        }).start();
    }

    /**
     * 设置当长按gridview的item时删除
     */
    private void setGridViewLongClickDelete(){

        mEmojisGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteEmojiDialog(id, position);
                //返回值为true拦截点击事件
                return true;
            }
        });
    }

    /**
     * 创建移除表情包弹窗
     */
    private void showDeleteEmojiDialog(long id, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.create();
        View dialogView = View.inflate(getActivity(), R.layout.dialog_delete_emoji, null);
        //当用户按下yes按钮时删除表情包
        dialogView.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //从表中删除item
                        EmojisDao emojisDao = AppDatabase.getInstance(getContext()).emojisDao();
                        emojisDao.deleteEmoji((int) id);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //刷新数据
                                list.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
                dialog.cancel();
            }
        });
        //当用户点击no按钮时取消弹窗
        dialogView.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setView(dialogView);
        dialog.show();
    }

    /**
     * 当用户点击表情包时分享给qq
     */
    private void setEmojisGridViewOnClickListener(){
        mEmojisGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setPackage("com.tencent.mobileqq");
                intent.putExtra(Intent.EXTRA_STREAM, list.get(position).getEmojiUri());
                intent.setType("image/*");
                intent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");//QQ
                getContext().startActivity(intent);
            }
        });
    }

    /**
     * 为gridview设置边距
     */
    private void setGridViewMargin(){
        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        float mar = ((float) width - MyUtils.dp2px(getContext(), 331))/2;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) mar,10, (int) mar,10);
        mEmojisGridView.setLayoutParams(params);
    }

}
