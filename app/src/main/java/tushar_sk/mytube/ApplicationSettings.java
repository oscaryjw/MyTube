package tushar_sk.mytube;

/**
 * Created by TUSHAR_SK on 10/18/15.
 */
public class ApplicationSettings {

    private static ApplicationSettings sharedSettings = null;

    private String accessToken;
    private String favoritePlaylistId;
    private String oAuth2ClientId;
    private ApplicationSettings() {

        accessToken = "";
        favoritePlaylistId = "";
    }

    public static ApplicationSettings getSharedSettings() {

        if (sharedSettings == null) {

            sharedSettings = new ApplicationSettings();
        }

        return sharedSettings;
    }


    public String getAccessToken() {

        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }

    public String getFavoritePlaylistId () {

        return this.favoritePlaylistId;
    }

    public void setFavoritePlaylistId (String favoritePlaylistId) {

        this.favoritePlaylistId = favoritePlaylistId;
    }
}
