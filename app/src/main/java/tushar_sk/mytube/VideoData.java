package tushar_sk.mytube;

import com.google.api.client.util.DateTime;

import java.math.BigInteger;


/**
 * Created by TUSHAR_SK on 10/9/15.
 */
public class VideoData {

    private String Id;

    private String Title;

    private String Uri;

    private DateTime date;

    private BigInteger views;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public BigInteger getViews() {
        return views;
    }

    public void setViews(BigInteger views) {
        this.views = views;
    }
}
