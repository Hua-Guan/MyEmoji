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
import pri.guanhua.myemoji.model.bean.EmojiMarketBean;

public class EmojiMarketAdapter extends BaseAdapter {

    private List<EmojiMarketBean> list;
    private Context context;

    public EmojiMarketAdapter(List<EmojiMarketBean> list, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_emoji_market, parent, false);
            Holder holder = new Holder();
            holder.mEmoji = convertView.findViewById(R.id.img_emoji);
            holder.mEmoji.setClipToOutline(true);
            Glide.with(convertView).load(list.get(position).getEmojiUri()).into(holder.mEmoji);
            convertView.setTag(holder);
        }else {
            Holder holder = (Holder) convertView.getTag();
            holder.mEmoji = convertView.findViewById(R.id.img_emoji);
            Glide.with(convertView).load(list.get(position).getEmojiUri()).into(holder.mEmoji);
        }
        return convertView;
    }

    private static class Holder{
        public ImageView mEmoji;
    }

}
