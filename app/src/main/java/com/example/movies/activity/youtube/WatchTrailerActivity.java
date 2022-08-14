package com.example.movies.activity.youtube;

import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.movies.R;
import com.example.movies.databinding.ActivityWatchTrailerBinding;
import com.example.movies.model.videos.TrailerObject;
import com.example.movies.utils.Utils;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

public class WatchTrailerActivity extends YouTubeBaseActivity {

    YouTubePlayer mYouTubePlayer;
    TrailerObject.Trailer trailer;
    Bundle bundle;
    Intent intent;
    ActivityWatchTrailerBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        bundle = intent.getBundleExtra("bundle");
        trailer = (TrailerObject.Trailer) bundle.getSerializable("trailer");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch_trailer);
        binding.setItem(trailer);

        binding.youtubePlayerView.initialize(Utils.KEY_API_YOUTUBE, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.setFullscreen(true);
                youTubePlayer.loadVideo(trailer.getKey());
                mYouTubePlayer = youTubePlayer;
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mYouTubePlayer.release();
    }
}