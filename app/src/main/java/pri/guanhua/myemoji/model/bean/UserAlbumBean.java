package pri.guanhua.myemoji.model.bean;

public class UserAlbumBean {

    private int UserAlbumId = 0;
    private String UserAlbumUri = null;
    private String UserAlbumTitle = null;
    private String UserAlbumCount = null;

    public int getUserAlbumId() {
        return UserAlbumId;
    }

    public void setUserAlbumId(int userAlbumId) {
        UserAlbumId = userAlbumId;
    }

    public String getUserAlbumUri() {
        return UserAlbumUri;
    }

    public void setUserAlbumUri(String userAlbumUri) {
        UserAlbumUri = userAlbumUri;
    }

    public String getUserAlbumTitle() {
        return UserAlbumTitle;
    }

    public void setUserAlbumTitle(String userAlbumTitle) {
        UserAlbumTitle = userAlbumTitle;
    }

    public String getUserAlbumCount() {
        return UserAlbumCount;
    }

    public void setUserAlbumCount(String userAlbumCount) {
        UserAlbumCount = userAlbumCount;
    }
}
