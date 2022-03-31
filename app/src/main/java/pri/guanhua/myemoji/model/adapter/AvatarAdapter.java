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
import pri.guanhua.myemoji.model.bean.AvatarBean;

public class AvatarAdapter extends BaseAdapter {

    private List<AvatarBean> list;
    private Context context;

    public AvatarAdapter(List<AvatarBean> list, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_choose_avatar, parent, false);
            Holder holder = new Holder();
            holder.mAvatar = convertView.findViewById(R.id.img_avatar);
            holder.mAvatar.setClipToOutline(true);
            Glide.with(convertView).load(list.get(position).getUri()).into(holder.mAvatar);

            convertView.setTag(holder);
        }else {
            Holder holder = (Holder) convertView.getTag();
            holder.mAvatar.setClipToOutline(true);
            Glide.with(convertView).load(list.get(position).getUri()).into(holder.mAvatar);
        }
        return convertView;
    }

    static class Holder{
        ImageView mAvatar;
    }

}
