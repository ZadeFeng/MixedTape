package com.example.mixtape;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mixtape.ui.home.HomeFragment;

public class TracksActivity extends AppCompatActivity {
    private AccessTokenViewModel accessTokenViewModel;
    private MainActivity mainActivity;
    private TextView text_tracks;
    private TextView text_home;
    private String mAccessToken;
    private MediaPlayer mediaPlayer;
    private Button playButton;
    private Button getTracks;
    private Button getArtists;
    public boolean isPlaying = false;
    private boolean isPreparingMediaPlayer = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tracks);

        Button getTracks = findViewById(R.id.get_tracks);
        Button getArtists = findViewById(R.id.get_artists);
        getArtists.setOnClickListener(((View view) -> {
            if (mainActivity != null) {
                mainActivity.onGetUserProfileClickedA(mainActivity);
            }
        }));
        getTracks.setOnClickListener(((View view) -> {
            if (mainActivity != null) {
                mainActivity.onGetUserProfileClickedT(mainActivity);
            }
        }));
        text_tracks = (TextView) findViewById(R.id.text_track);
        text_home = (TextView) findViewById(R.id.text_home);
        playButton = findViewById(R.id.get_tracks);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    // Start or resume audio playback
                    if (mediaPlayer == null) {
                        // If mediaPlayer is null and not preparing, start playback by calling onGetUserProfileClicked
                        if (!isPreparingMediaPlayer) {
                            mainActivity.onGetUserProfileClickedT(mainActivity);
                        }
                    } else {
                        // If mediaPlayer is not null, resume playback
                        mediaPlayer.start();
                    }
                    isPlaying = true;
                } else {
                    // Pause audio playback
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    isPlaying = false;
                }
            }
        });

        accessTokenViewModel = new ViewModelProvider(mainActivity).get(AccessTokenViewModel.class);

        // Retrieve access token from ViewModel
        String savedAccessToken = accessTokenViewModel.getAccessToken();
        if (savedAccessToken != null) {
            // Access token already set, no need to request a new one
            mAccessToken = savedAccessToken;
        }

        Button next2 = findViewById(R.id.nextTwo);

        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TracksActivity.this, HomeFragment.class);
                startActivity(intent);
            }
        });
    }
}
