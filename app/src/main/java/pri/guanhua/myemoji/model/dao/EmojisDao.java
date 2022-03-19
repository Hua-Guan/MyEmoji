package pri.guanhua.myemoji.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import pri.guanhua.myemoji.model.entity.EmojisEntity;

@Dao
public interface EmojisDao {

    @Query("SELECT * FROM EmojisEntity WHERE emojiAlbum = (:emojiAlbum)")
    List<EmojisEntity> getEmojisByAlbum(String emojiAlbum);

    @Insert
    void insertEmojis(EmojisEntity...emojisEntities);
}
