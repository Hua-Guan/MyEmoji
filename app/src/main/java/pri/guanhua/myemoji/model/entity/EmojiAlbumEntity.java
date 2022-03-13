package pri.guanhua.myemoji.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EmojiAlbumEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo
    public String emojiAlbumTitle;

}
