package com.example.mixtape;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mixtape.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ArtistsActivity  extends AppCompatActivity {
    private AccessTokenViewModel accessTokenViewModel;
    private ActivityMainBinding binding;
    private MainActivity mainActivity;
    private TextView text_home;
    private String mAccessToken;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_artists);

        Button getTracks = findViewById(R.id.get_artists);
        getTracks.setOnClickListener(((View view) -> {
            if (mainActivity != null) {
                mainActivity.onGetUserProfileClickedA(mainActivity);
            }
        }));
        text_home = findViewById(R.id.text_home);
        accessTokenViewModel = new ViewModelProvider(mainActivity).get(AccessTokenViewModel.class);

        // Retrieve access token from ViewModel
        String savedAccessToken = accessTokenViewModel.getAccessToken();
        if (savedAccessToken != null) {
            // Access token already set, no need to request a new one
            mAccessToken = savedAccessToken;
        }

        Button next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ArtistsActivity.this, TracksActivity.class);
//                startActivity(intent);
                binding = ActivityMainBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());

                BottomNavigationView navView = findViewById(R.id.nav_view);
                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_home, R.id.navigation_past, R.id.navigation_profile)
                        .build();
                NavController navController = Navigation.findNavController(mainActivity, R.id.nav_host_fragment_activity_main);
                NavigationUI.setupActionBarWithNavController(mainActivity, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(binding.navView, navController);
            }
        });

    }
}
