package tushar_sk.mytube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;


/**
 * Created by TUSHAR_SK on 10/15/15.
 */
public class Connector {

    /**
     * Global instance Developer Key.
     */
   /*
   * TODO: Replace key AJzbXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX with your key.  If you don't, you
   * will get a 400 service error (bad request).
   */
//    private static final String DEV_KEY = "AIzaSyCMRwKQiG3j1QbF5LjvJdbYqnlq5H2zQ0w"; //Andriod Key
      private static final String DEV_KEY = "AIzaSyDBs7JvtRMXkhyRLjd1YdNnlUFFfa7mGw4"; // Browser Key

    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * Global instance of the max number of videos we want returned (50 = upper limit per page).
     */
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    /**
     * Global instance of Youtube object to make all API requests.
     */
    private static YouTube youtube;

    /**
     * Initializes YouTube object to search for videos on YouTube (Youtube.Search.List).  The
     * program then prints the names and thumbnails of each of the videos (only first 50 videos).
     *
     * @param args command line args.
     */
    /*
       * The YouTube object is used to make all API requests.  The last argument is required, but
       * because we don't need anything initialized when the HttpRequest is initialized, we
       * override the interface and provide a no-op function.
       */

    public List<VideoData> searchQuery(String args) {
        try {
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            })
                    .setApplicationName("MyTube")
                    .build();

            // Get query term from user.
            String queryTerm = args;

            YouTube.Search.List search = youtube.search().list("id,snippet");
              /*
               * It is important to set your developer key from the Google Developer Console for
               * non-authenticated requests (found under the API Access tab at this link:
               * code.google.com/apis/). This is good practice and increased your quota.
               */
            search.setKey(DEV_KEY);
            search.setQ(queryTerm);
              /*
               * We are only searching for videos (not playlists or channels).  If we were searching for
               * more, we would add them as a string like this: "video,playlist,channel".
               */
            search.setType("video");
              /*
               * This method reduces the info returned to only the fields we need and makes calls more efficient.
               */
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet/publishedAt)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            SearchListResponse searchResponse = search.execute();

            List<SearchResult> searchResultList = searchResponse.getItems();

            if(searchResultList != null) {
                prettyPrint(searchResultList.iterator(), queryTerm);
            }

            if (searchResultList != null) {

                List<VideoData> items = new ArrayList<>();

                for (SearchResult result : searchResultList) {

                    VideoData item = new VideoData();
                    item.setId(result.getId().getVideoId());
                    item.setTitle(result.getSnippet().getTitle());
                    item.setUri(result.getSnippet().getThumbnails().getDefault().getUrl());
                    item.setDate(result.getSnippet().getPublishedAt());
                    YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet, statistics").setId(item.getId());
                    listVideosRequest.setKey(DEV_KEY);
                    Video video = listVideosRequest.execute().getItems().get(0);
                    item.setViews(video.getStatistics().getViewCount());
                    items.add(item);
                }
                return items;

            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println("   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if(!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while(iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Double checks the kind is video.
            if(rId.getKind().equals("youtube#video")) {

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }

}