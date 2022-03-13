package pri.guanhua.myemoji.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import pri.guanhua.myemoji.model.entity.EmojiAlbumEntity;

@Dao
public interface EmojiAlbumDao {

    @Query("SELECT * FROM EmojiAlbumEntity")
    List<EmojiAlbumEntity> getAll();

    @Insert
    void insertAll(EmojiAlbumEntity...entities);

}
