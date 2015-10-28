package tushar_sk.mytube;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubeInitializationResult;

/**
 * Created by TUSHAR_SK on 10/16/15.
 */
public class Player extends YouTubeBaseActivity  implements
        YouTubePlayer.OnInitializedListener{

    private final String KEY = "AIzaSyCMRwKQiG3j1QbF5LjvJdbYqnlq5H2zQ0w";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView)findViewById(R.id.player);
        youTubePlayerView.initialize(KEY, this);

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason){

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored){
        if( !wasRestored ){
            player.cueVideo(getIntent().getStringExtra("video"));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
