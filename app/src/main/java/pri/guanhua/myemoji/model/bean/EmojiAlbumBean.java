package pri.guanhua.myemoji.model.bean;

public class EmojiAlbumBean {

    private int Id = 0;
    private String emojiAlbumUri = "";
    private String emojiAlbumTitle = "";
    private String emojiAlbumCount = "";

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getEmojiAlbumUri() {
        return emojiAlbumUri;
    }

    public void setEmojiAlbumUri(String emojiAlbumUri) {
        this.emojiAlbumUri = emojiAlbumUri;
    }

    public String getEmojiAlbumTitle() {
        return emojiAlbumTitle;
    }

    public void setEmojiAlbumTitle(String emojiAlbumTitle) {
        this.emojiAlbumTitle = emojiAlbumTitle;
    }

    public String getEmojiAlbumCount() {
        return emojiAlbumCount;
    }

    public void setEmojiAlbumCount(String emojiAlbumCount) {
        this.emojiAlbumCount = emojiAlbumCount;
    }
}
