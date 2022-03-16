package pri.guanhua.myemoji.model.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pri.guanhua.myemoji.model.bean.EmojiAlbumBean;
import pri.guanhua.myemoji.model.bean.UserAlbumBean;
import pri.guanhua.myemoji.model.bean.UserImageBean;

public class AppViewModel extends ViewModel {

    private MutableLiveData<EmojiAlbumBean> emojiAlbumAddLiveData = null;
    private MutableLiveData<String> userPositionLiveData = null;
    private MutableLiveData<List<UserAlbumBean>> userAlbumListMutableLiveData = null;
    private MutableLiveData<List<UserImageBean>> userImageListLiveData = null;

    public MutableLiveData<EmojiAlbumBean> getEmojiAlbumAddLiveData() {

        if (emojiAlbumAddLiveData == null){
            emojiAlbumAddLiveData = new MutableLiveData<>();
        }
        return emojiAlbumAddLiveData;
    }

    public MutableLiveData<String> getUserPositionLiveData() {
        if (userPositionLiveData == null){
            userPositionLiveData = new MutableLiveData<>();
        }
        return userPositionLiveData;
    }

    public MutableLiveData<List<UserAlbumBean>> getUserAlbumListMutableLiveData() {
        if (userAlbumListMutableLiveData == null){
            userAlbumListMutableLiveData = new MutableLiveData<>();
        }
        return userAlbumListMutableLiveData;
    }

    public MutableLiveData<List<UserImageBean>> getUserImageListLiveData() {
        if (userImageListLiveData == null){
            userImageListLiveData = new MutableLiveData<>();
        }
        return userImageListLiveData;
    }
}
