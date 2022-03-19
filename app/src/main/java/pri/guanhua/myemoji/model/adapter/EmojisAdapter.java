package pri.guanhua.myemoji.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import pri.guanhua.myemoji.R;
import pri.guanhua.myemoji.model.bean.EmojisBean;

public class EmojisAdapter extends BaseAdapter {

    private Context context;
    private List<EmojisBean> list;

    public EmojisAdapter(Context context, List<EmojisBean> list) {
        this.context = context;
        this.list = list;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_emojis, parent, false);
            Holder holder = new Holder();
            holder.mImgEmoji = convertView.findViewById(R.id.img_emoji);
            holder.mImgEmoji.setClipToOutline(true);
            Glide.with(convertView)
                    .load(list.get(position).getEmojiUri())
                    .into(holder.mImgEmoji);
            convertView.setTag(holder);
        }else {
            Holder holder = (Holder) convertView.getTag();
            Glide.with(convertView)
                    .load(list.get(position).getEmojiUri())
                    .into(holder.mImgEmoji);
        }
        return convertView;
    }

    class Holder{
        ImageView mImgEmoji = null;
    }

}
