package pri.guanhua.myemoji.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.bean.UserAlbumBean;

public class UserAlbumAdapter extends BaseAdapter {

    private List<UserAlbumBean> list;
    private Context context;

    public UserAlbumAdapter(List<UserAlbumBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getUserAlbumId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user_album_list, parent, false);
            Holder holder = new Holder();
            holder.mUserAlbumImage = convertView.findViewById(R.id.img_user_album);
            holder.mUserAlbumImage.setClipToOutline(true);
            holder.mTextUserAlbumTitle = convertView.findViewById(R.id.text_user_album_title);
            holder.mTextUserAlbumCount = convertView.findViewById(R.id.text_user_album_count);

            UserAlbumBean bean = list.get(position);
            if (bean.getUserAlbumUri() != null){
                Glide.with(convertView)
                        .load(bean.getUserAlbumUri())
                        .into(holder.mUserAlbumImage);
            }
            if (bean.getUserAlbumTitle() != null){
                holder.mTextUserAlbumTitle.setText(bean.getUserAlbumTitle());
            }
            if (bean.getUserAlbumCount() != null){
                holder.mTextUserAlbumCount.setText(bean.getUserAlbumCount());
            }
            convertView.setTag(holder);
        }else {
            Holder holder = (Holder) convertView.getTag();
            UserAlbumBean bean = list.get(position);
            if (bean.getUserAlbumUri() != null){
                Glide.with(convertView)
                        .load(bean.getUserAlbumUri())
                        .into(holder.mUserAlbumImage);
            }
            if (bean.getUserAlbumTitle() != null){
                holder.mTextUserAlbumTitle.setText(bean.getUserAlbumTitle());
            }
            if (bean.getUserAlbumCount() != null){
                holder.mTextUserAlbumCount.setText(bean.getUserAlbumCount());
            }
        }
        return convertView;
    }

    private static class Holder{
        ImageView mUserAlbumImage = null;
        TextView mTextUserAlbumTitle = null;
        TextView mTextUserAlbumCount = null;
    }

}
