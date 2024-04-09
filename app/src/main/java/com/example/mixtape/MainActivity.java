package com.example.mixtape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mixtape.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.neovisionaries.i18n.CountryCode;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.apache.hc.core5.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public static final String CLIENT_ID = "6fce362aa36d46fda30fd2de1d4d4f86";
    public static final String REDIRECT_URI = "com.example.mixtape://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;

    Button login;
    private TextView text_home;
    private TextView text_track;
    private TextView text_start;
    private TextView text_start2;
    private String tracks;
    private AccessTokenViewModel accessTokenViewModel;

    private MediaPlayer mediaPlayer;
    private Button playButton;
    private Button getArtists;
    public boolean isPlaying = false;
    private boolean isPreparingMediaPlayer = false;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        login = findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToken(MainActivity.this);

                setContentView(R.layout.activity_start);
                text_start = (TextView) findViewById(R.id.text_start);
                text_start2 = (TextView) findViewById(R.id.text_start2);
                Button next1 = findViewById(R.id.next1);

                next1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setContentView(R.layout.fragment_tracks);
                        text_home = (TextView) findViewById(R.id.text_home);
                        accessTokenViewModel = new ViewModelProvider(MainActivity.this).get(AccessTokenViewModel.class);

                        // Retrieve access token from ViewModel
                        String savedAccessToken = accessTokenViewModel.getAccessToken();
                        if (savedAccessToken != null) {
                            // Access token already set, no need to request a new one
                            mAccessToken = savedAccessToken;
                        }

                        Button getTracks = findViewById(R.id.get_tracks);
                        getTracks.setOnClickListener(((View view) -> {
                            if (mainActivity != null) {
                                mainActivity.onGetUserProfileClickedT(MainActivity.this);
                            }
                        }));
                        getArtists = findViewById(R.id.get_artists);
                        getArtists.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    onGetUserProfileClickedA(MainActivity.this);
                            }
                        });
                        Button next2 = findViewById(R.id.nextTwo);
                        text_track = (TextView) findViewById(R.id.text_track);
                        playButton = findViewById(R.id.get_tracks);

                        playButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!isPlaying) {
                                    // Start or resume audio playback
                                    if (mediaPlayer == null) {
                                        // If mediaPlayer is null and not preparing, start playback by calling onGetUserProfileClicked
                                        if (!isPreparingMediaPlayer) {
                                            onGetUserProfileClickedT(MainActivity.this);
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
                        //uploadData();

                        next2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                binding = ActivityMainBinding.inflate(getLayoutInflater());
                                setContentView(binding.getRoot());

                                BottomNavigationView navView = findViewById(R.id.nav_view);
                                // Passing each menu ID as a set of Ids because each
                                // menu should be considered as top level destinations.
                                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                                        R.id.navigation_home, R.id.navigation_past, R.id.navigation_profile)
                                        .build();
                                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
                                NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
                                NavigationUI.setupWithNavController(binding.navView, navController);
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken(Activity activity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(activity, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode(Activity activity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(activity, AUTH_CODE_REQUEST_CODE, request);
    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
        }

    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClickedA(Activity activity) {
        if (mAccessToken == null) {
            Toast.makeText(activity, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        accessTokenViewModel = new ViewModelProvider(this).get(AccessTokenViewModel.class);

        // Retrieve access token from ViewModel
        String savedAccessToken = accessTokenViewModel.getAccessToken();
        if (savedAccessToken != null) {
            // Access token already set, no need to request a new one
            mAccessToken = savedAccessToken;
        }

        int limit = 5; // Number of items per page
        int offset = 0; // Initial offset
        int total = 5;

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?limit=" + limit + "&offset=" + offset + "&total=" + total)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network failure
                Log.e("HTTP", "Failed to fetch data: " + e.getMessage());
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "Failed to fetch data. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // Handle unsuccessful response
                    Log.e("HTTP", "Response code: " + response.code());
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Failed to fetch data. Response not successful.", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // Process successful response
                String responseData = response.body().string();
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray itemsArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject artistObject = itemsArray.getJSONObject(i);
                        String artistName = artistObject.getString("name");
                        stringBuilder.append(artistName).append("\n"); // Append each artist name to the StringBuilder
                    }

                    uploadData(stringBuilder.toString());
                    // Update UI on the main thread
                    activity.runOnUiThread(() -> setTextAsync(stringBuilder.toString(), text_home));


                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    Log.e("HTTP", "Failed to parse JSON: " + e.getMessage());
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Failed to fetch data. Error parsing JSON response.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
    public void onGetUserProfileClickedT(Activity activity) {
        text_track = (TextView) findViewById(R.id.text_track);
        if (mAccessToken == null) {
            Toast.makeText(activity, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable the button to prevent multiple clicks
        playButton.setEnabled(false);

        accessTokenViewModel = new ViewModelProvider(this).get(AccessTokenViewModel.class);

        // Retrieve access token from ViewModel
        String savedAccessToken = accessTokenViewModel.getAccessToken();
        if (savedAccessToken != null) {
            // Access token already set, no need to request a new one
            mAccessToken = savedAccessToken;
        }

        int limit = 5; // Number of items per page
        int offset = 0; // Initial offset
        int total = 5;

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=" + limit + "&offset=" + offset + "&total=" + total)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network failure
                Log.e("HTTP", "Failed to fetch data: " + e.getMessage());
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "Failed to fetch data. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    // Enable the button again
                    playButton.setEnabled(true);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // Handle unsuccessful response
                    Log.e("HTTP", "Response code: " + response.code());
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Failed to fetch data. Response not successful.", Toast.LENGTH_SHORT).show();
                        // Enable the button again
                        playButton.setEnabled(true);
                    });
                    return;
                }

                // Process successful response
                String responseData = response.body().string();
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray itemsArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject artistObject = itemsArray.getJSONObject(i);
                        String artistName = artistObject.getString("name");
                        stringBuilder.append(artistName).append("\n"); // Append each artist name to the StringBuilder
                    }

                    //upload tracks to firebase realtime database
                    //uploadData(stringBuilder.toString());

                    // Update UI on the main thread
                    activity.runOnUiThread(() -> {
                        setTextAsync(stringBuilder.toString(), text_track);
                        // Enable the button again
                        playButton.setEnabled(true);
                    });

                    // Create MediaPlayer and set data source
                    JSONObject song = itemsArray.optJSONObject(1);
                    if (song != null) {
                        String previewUrl = song.optString("preview_url");
                        if (previewUrl != null && !previewUrl.isEmpty()) {
                            Log.d("Preview URL", previewUrl);
                            runOnUiThread(() -> Toast.makeText(activity, "Preview URL: " + previewUrl, Toast.LENGTH_SHORT).show());

                            // Check if mediaPlayer is null or preparing
                            if (mediaPlayer == null || isPreparingMediaPlayer) {
                                try {
                                    mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .setUsage(AudioAttributes.USAGE_MEDIA)
                                            .build());

                                    mediaPlayer.setDataSource(previewUrl);

                                    isPreparingMediaPlayer = true; // Set flag to true while preparing MediaPlayer

                                    // Inside onGetUserProfileClicked method
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            // MediaPlayer is prepared, start playback
                                            mediaPlayer.start();
                                            isPlaying = true;
                                            isPreparingMediaPlayer = false; // Reset flag after preparation
                                        }
                                    });

                                    mediaPlayer.prepareAsync(); // Prepare the MediaPlayer asynchronously

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.e("MediaPlayer", "Failed to set data source: " + e.getMessage());
                                    Toast.makeText(activity, "Failed to set data source for MediaPlayer.", Toast.LENGTH_SHORT).show();
                                    isPreparingMediaPlayer = false; // Reset flag on error
                                }
                            } else {
                                // Resume playback if mediaPlayer is not null and not preparing
                                if (!mediaPlayer.isPlaying()) {
                                    mediaPlayer.start();
                                    isPlaying = true;
                                }
                            }
                        } else {
                            Log.e("Preview URL", "Preview URL is null or empty");
                            runOnUiThread(() -> Toast.makeText(activity, "Preview URL is null or empty.", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Log.e("Preview URL", "No song object found");
                        runOnUiThread(() -> Toast.makeText(activity, "No song object found.", Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    Log.e("HTTP", "Failed to parse JSON: " + e.getMessage());
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Failed to fetch data. Error parsing JSON response.", Toast.LENGTH_SHORT).show();
                        // Enable the button again
                        playButton.setEnabled(true);
                    });
                }
            }
        });
    }


    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
        //uploadData(text);
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] {"user-top-read"}) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    private void stopAudioPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    private Track[] getRecommendations(){
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(mAccessToken)
                .build();

        GetRecommendationsRequest getRecommendationsRequest = spotifyApi
                .getRecommendations()
                .seed_artists("4NHQUGzhtTLFvgF5SZesLK") // Example artist ID
                .seed_genres("classical,country") // Example genres
                .seed_tracks("0c6xIDDpzE81m2q797ordA") // Example track ID
                .market(CountryCode.valueOf("US")) // Specify the market (United States)
                .build();
        Log.d("MyApp", getRecommendationsRequest.toString());

        try {
            Log.d("MyApp", getRecommendationsRequest.toString());
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            Recommendations recommendations = getRecommendationsRequest.execute();

            // Verify access token validity and permissions
            if (recommendations.getTracks().length == 0) {
                // Handle empty recommendations
                Log.d("MyApp","No recommendations available.");
                // Optionally, suggest fallback recommendations here
            }

            Log.d("MyApp",recommendations.getTracks()[1].toString());
            return recommendations.getTracks();
        } catch (SpotifyWebApiException e) {
            Log.d("MyApp","Spotify API error: " + e.getMessage());
        } catch (IOException e) {
            Log.d("MyApp","Network or I/O error: " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void uploadData(String text) {
        //String tracks = uploadTracks.getText().toString();

        DataClass dataClass = new DataClass(text);
        String currentDate = String.valueOf(Calendar.getInstance().getTime());

        FirebaseDatabase.getInstance().getReference("mixtape").child(currentDate)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            //finish();
                        } else {
                            Toast.makeText(MainActivity.this, "help", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}