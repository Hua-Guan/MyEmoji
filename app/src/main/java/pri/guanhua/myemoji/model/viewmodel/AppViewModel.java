package pri.guanhua.myemoji.model.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pri.guanhua.myemoji.model.bean.EmojiAlbumBean;

public class AppViewModel extends ViewModel {

    private MutableLiveData<EmojiAlbumBean> emojiAlbumAddLiveData = null;

    public MutableLiveData<EmojiAlbumBean> getEmojiAlbumAddLiveData() {

        if (emojiAlbumAddLiveData == null){
            emojiAlbumAddLiveData = new MutableLiveData<>();
        }
        return emojiAlbumAddLiveData;
    }
}
