package tushar_sk.mytube;


import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Video;
import com.google.common.collect.Lists;
import com.google.api.client.auth.oauth2.Credential;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by TUSHAR_SK on 10/16/15.
 */
public class PlayList {


    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final String Key = "enter browser key here";

    private static final String Dev_Key = "enter android key here";

    private static YouTube youtube;

    private static final String playlist = "SJSU-CMPE-277";

    private String accessToken= "";

    public static String playID = "";


    private static Credential authorize(List<String> scopes) throws Exception {

        Reader reader = new InputStreamReader(PlayList.class.getResourceAsStream("/client_secret.json"));
        // Load client secrets.
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JSON_FACTORY, reader);

        // Checks that the defaults have been replaced (Default = "Enter X here").
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=youtube"
                            + "into youtube-cmdline-playlistupdates-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }

        // Set up file credential store.
        FileCredentialStore credentialStore =
                new FileCredentialStore(
                        new File(System.getProperty("user.home"),
                                ".credentials/youtube-api-playlistupdates.json"),
                        JSON_FACTORY);

        // Set up authorization code flow.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
                        JSON_FACTORY,
                        clientSecrets,
                        scopes)
                        .setCredentialStore(credentialStore).build();

        // Authorize.
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }



    public List<VideoData> search() {
        try {
            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            })
                    .setApplicationName("MyTube")
                    .build();

                // The user's default channel is the first item in the list.
                // Extract the playlist ID for the channel's videos from the
                // API response.
            playID = getFavoritePlaylist();// "PLBVXlIXkusofSOmX-5p6DhMJpG7ykncI2";

            Log.v("IDplay:",playID);
            // Define a list to store items in the list of uploaded videos.
            List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

            // Retrieve the playlist of the channel's uploaded videos.
            YouTube.PlaylistItems.List playlistItemRequest = youtube.playlistItems().list("id,contentDetails,snippet");
            playlistItemRequest.setPlaylistId(playID);
            playlistItemRequest.setKey(Key);

            playlistItemRequest.setFields(
                    "items(contentDetails/videoId,snippet/title,snippet/thumbnails/default/url,snippet/publishedAt),nextPageToken,pageInfo");

            String nextToken = "";


            do {
                playlistItemRequest.setPageToken(nextToken);
                PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

                playlistItemList.addAll(playlistItemResult.getItems());

                nextToken = playlistItemResult.getNextPageToken();
            } while (nextToken != null);

            // Prints information about the results.
            prettyPrint(playlistItemList.size(), playlistItemList.iterator());

            List<VideoData> items = new ArrayList<>();

            for (PlaylistItem result : playlistItemList) {

                VideoData item = new VideoData();
                item.setId(result.getContentDetails().getVideoId());
                item.setTitle(result.getSnippet().getTitle());
                item.setUri(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setDate(result.getSnippet().getPublishedAt());
                YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet, statistics").setId(item.getId());
                listVideosRequest.setKey(Key);
                Video video = listVideosRequest.execute().getItems().get(0);
                item.setViews(video.getStatistics().getViewCount());
                items.add(item);
            }
            return items;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }



    public static String getFavoritePlaylist() throws JSONException {


        String getPlayListItemsURL = "https://www.googleapis.com/youtube/v3/playlists";

        StringBuilder getPlayListURLBuilder = new StringBuilder();
        getPlayListURLBuilder.append("part").append("="+"id");
        getPlayListURLBuilder.append(",").append("snippet");
        getPlayListURLBuilder.append("&").append("mine").append("="+"true");

        String playlistParams = getPlayListURLBuilder.toString();
        ArrayList <String> playlistResponse = Util
                .getResponse(getPlayListItemsURL, playlistParams, ApplicationSettings.getSharedSettings().getAccessToken());

        JSONObject playlistJSON = new JSONObject(playlistResponse.get(0));

        Map<String, Object> playlistMap = toMap(playlistJSON);

        ArrayList <Object> playlistList = new ArrayList<Object>();
        playlistList.addAll((Collection<?>) playlistMap.get("items"));

        String id = (String)((HashMap) playlistList.get(0)).get("id");
        return id;
    }



    public void insertPlaylistItem(String videoId) throws Exception {

        List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE,YouTubeScopes.YOUTUBE_UPLOAD);
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            GoogleCredential credential1 = new GoogleCredential.Builder()
                    .setTransport(new NetHttpTransport())
                    .setJsonFactory(new JacksonFactory()).build();
            credential1.setAccessToken(ApplicationSettings.getSharedSettings().getAccessToken());

            Log.v("insert item token:",ApplicationSettings.getSharedSettings().getAccessToken());
            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY,credential1)
                    .setApplicationName("MyTube")
                    .build();

            Log.v("IDplay",playID);
            // Define a resourceId that identifies the video being added to the
            // playlist.
            ResourceId resourceId = new ResourceId();
            resourceId.setKind("youtube#video");
            resourceId.setVideoId(videoId);

            // Set fields included in the playlistItem resource's "snippet" part.
            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
            playlistItemSnippet.setPlaylistId(playID);
            playlistItemSnippet.setResourceId(resourceId);

            // Create the playlistItem resource and set its snippet to the
            // object created above.
            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setSnippet(playlistItemSnippet);

            // Call the API to add the playlist item to the specified playlist.
            // In the API call, the first argument identifies the resource parts
            // that the API response should contain, and the second argument is
            // the playlist item being inserted.
            YouTube.PlaylistItems.Insert playlistItemsInsertCommand =
                    youtube.playlistItems().insert("snippet,contentDetails", playlistItem);
//            playlistItemsInsertCommand.setKey(Key);
            playlistItemsInsertCommand.execute();

        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }

    }

    private void prettyPrint(int size, Iterator<PlaylistItem> playlistEntries) {
        System.out.println("=============================================================");
        System.out.println("\t\tTotal Videos Uploaded: " + size);
        System.out.println("=============================================================\n");

        while (playlistEntries.hasNext()) {
            PlaylistItem playlistItem = playlistEntries.next();
            System.out.println(" video name  = " + playlistItem.getSnippet().getTitle());
            System.out.println(" video id    = " + playlistItem.getContentDetails().getVideoId());
            System.out.println(" upload date = " + playlistItem.getSnippet().getPublishedAt());
            System.out.println("\n-------------------------------------------------------------\n");
        }
    }
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

}

