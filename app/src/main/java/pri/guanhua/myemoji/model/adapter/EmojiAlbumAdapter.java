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
import pri.guanhua.myemoji.model.bean.EmojiAlbumBean;

public class EmojiAlbumAdapter extends BaseAdapter {

    private List<EmojiAlbumBean> list = null;
    private Context context = null;

    public EmojiAlbumAdapter(List<EmojiAlbumBean> list, Context context) {
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
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_emoji_album, parent, false);
            Holder holder = new Holder();
            holder.mEmojiAlbum = convertView.findViewById(R.id.emoji_album);
            holder.mEmojiAlbumTitle = convertView.findViewById(R.id.emoji_title);
            holder.mEmojiAlbumCount = convertView.findViewById(R.id.emoji_count);
            //
            EmojiAlbumBean bean = list.get(position);
            //设置圆角
            holder.mEmojiAlbum.setClipToOutline(true);
            if (bean.getEmojiAlbumUri() != null) {
                //加载封面
                Glide.with(convertView).load(list.get(position).getEmojiAlbumUri()).into(holder.mEmojiAlbum);
            }
            if (bean.getEmojiAlbumTitle() != null) {
                //设置标题
                holder.mEmojiAlbumTitle.setText(list.get(position).getEmojiAlbumTitle());
            }
            if (bean.getEmojiAlbumCount() != null) {
                //设置数量
                holder.mEmojiAlbumCount.setText(list.get(position).getEmojiAlbumCount());
            }
            //
            convertView.setTag(holder);
        }else {
            Holder holder = (Holder) convertView.getTag();
            EmojiAlbumBean bean = list.get(position);
            if (bean.getEmojiAlbumUri() != null) {
                //加载封面
                Glide.with(convertView).load(list.get(position).getEmojiAlbumUri()).into(holder.mEmojiAlbum);
            }
            if (bean.getEmojiAlbumTitle() != null) {
                //设置标题
                holder.mEmojiAlbumTitle.setText(list.get(position).getEmojiAlbumTitle());
            }
            if (bean.getEmojiAlbumCount() != null) {
                //设置数量
                holder.mEmojiAlbumCount.setText(list.get(position).getEmojiAlbumCount());
            }
        }
        return convertView;
    }

    private static class Holder{

        public ImageView mEmojiAlbum = null;
        public TextView mEmojiAlbumTitle = null;
        public TextView mEmojiAlbumCount = null;

    }

}
