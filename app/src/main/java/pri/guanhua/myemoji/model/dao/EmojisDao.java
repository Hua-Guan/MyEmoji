package pri.guanhua.myemoji.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import pri.guanhua.myemoji.model.entity.EmojisEntity;

@Dao
public interface EmojisDao {

    @Query("SELECT * FROM EmojisEntity WHERE emojiAlbum =:emojiAlbum")
    List<EmojisEntity> getEmojisByAlbum(String emojiAlbum);

    @Query("SELECT * FROM EmojisEntity")
    List<EmojisEntity> getAll();

    @Query("DELETE FROM EmojisEntity WHERE id =:id")
    void deleteEmoji(int id);

    @Insert
    void insertEmojis(EmojisEntity...emojisEntities);
}
