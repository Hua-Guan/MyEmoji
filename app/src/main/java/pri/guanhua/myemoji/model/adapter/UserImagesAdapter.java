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
import pri.guanhua.myemoji.model.bean.UserImageBean;

public class UserImagesAdapter extends BaseAdapter {

    private List<UserImageBean> list;
    private Context context;

    public UserImagesAdapter(List<UserImageBean> list, Context context) {
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
        return list.get(position).getImageId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user_image, parent, false);
            Holder holder = new Holder();
            holder.mUserImageView = convertView.findViewById(R.id.img_user);
            holder.mUserImageView.setClipToOutline(true);
            holder.mUserImageViewSelect = convertView.findViewById(R.id.img_select);
            holder.mUserImageViewSelect.setClipToOutline(true);
            UserImageBean bean = list.get(position);
            //加载图片
            Glide.with(convertView)
                    .load(bean.getImageUri())
                    .into(holder.mUserImageView);
            if (bean.isSelected()){
                holder.mUserImageViewSelect.setImageResource(R.drawable.ic_choose);
            }else {
                holder.mUserImageViewSelect.setImageDrawable(null);
            }

            convertView.setTag(holder);
        }else {
            Holder holder = (Holder) convertView.getTag();
            UserImageBean bean = list.get(position);
            //加载图片
            Glide.with(convertView)
                    .load(bean.getImageUri())
                    .into(holder.mUserImageView);
            if (bean.isSelected()){
                holder.mUserImageViewSelect.setImageResource(R.drawable.ic_choose);
            }else {
                holder.mUserImageViewSelect.setImageDrawable(null);
            }
        }
        return convertView;
    }

    private static class Holder{
        public ImageView mUserImageView = null;
        public ImageView mUserImageViewSelect = null;
    }

}
