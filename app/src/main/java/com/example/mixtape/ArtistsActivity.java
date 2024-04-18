package com.example.mixtape;

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
    }
}
