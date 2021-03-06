package pri.guanhua.myemoji.model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pri.guanhua.myemoji.model.dao.EmojiAlbumDao;
import pri.guanhua.myemoji.model.dao.EmojisDao;
import pri.guanhua.myemoji.model.entity.EmojiAlbumEntity;
import pri.guanhua.myemoji.model.entity.EmojisEntity;

@Database(entities = {EmojiAlbumEntity.class, EmojisEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract EmojiAlbumDao emojiAlbumDao();

    public abstract EmojisDao emojisDao();

    public static AppDatabase getInstance(Context context){
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_data.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
