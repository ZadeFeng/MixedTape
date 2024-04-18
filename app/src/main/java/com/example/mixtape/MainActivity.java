package com.example.mixtape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public static final String CLIENT_ID = "6fce362aa36d46fda30fd2de1d4d4f86";
    public static final String CLIENT_SECRET = "0108c9ce7f9148a48436baf94c522918";
    public static final String REDIRECT_URI = "com.example.mixtape://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private String mRefreshToken = "placeholder";

    private Call mCall;

    Button login;
    private TextView text_home;
    private TextView text_track;
    private TextView text_recs;
    private TextView text_start;
    private TextView text_start2;
    private String artists = "artists";
    private AccessTokenViewModel accessTokenViewModel;

    private MediaPlayer mediaPlayer;
    private Button playButton;
    private Button getArtists;
    private Button getRecs;
    public boolean isPlaying = false;
    private boolean isPreparingMediaPlayer = false;
    private String time_range = "short_term";
    private int limit = 5; // Number of items per page
    private int offset = 0; // Initial offset
    private int total = 5;
    EditText uploadUsername;
    private String artistID;
    private List<String> genres = new ArrayList<>();
    private String trackID;
    private String imageURL;
    private String trackURL;
    private int recAmmount = 3;
    private MainActivity mainActivity;
//    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

//        firestore = FirebaseFirestore.getInstance();

        login = findViewById(R.id.loginButton);
        uploadUsername = findViewById(R.id.username);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(R.layout.activity_start);
                text_start = (TextView) findViewById(R.id.text_start);
                text_start2 = (TextView) findViewById(R.id.text_start2);
                Button next1 = findViewById(R.id.next1);

                next1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //getToken(MainActivity.this);

                        setContentView(R.layout.fragment_artists);

                        Button shortButton = findViewById(R.id.shortterm);
                        shortButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                time_range = "short_term";
                            }
                        });

                        // Click listener for the "medium" button
                        Button mediumButton = findViewById(R.id.mediumterm);
                        mediumButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                time_range = "medium_term";
                            }
                        });

                        // Click listener for the "long" button
                        Button longButton = findViewById(R.id.longterm);
                        longButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                time_range = "long_term";
                            }
                        });
                        Button next2 = findViewById(R.id.nextTwo);
                        next2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getToken(MainActivity.this);
                                setContentView(R.layout.fragment_tracks);

                                text_home = (TextView) findViewById(R.id.text_home);
                                accessTokenViewModel = new ViewModelProvider(MainActivity.this).get(AccessTokenViewModel.class);

//                                Retrieve access token from ViewModel
                                String savedAccessToken = accessTokenViewModel.getAccessToken();
                                if (savedAccessToken != null) {
                                    // Access token already set, no need to request a new one
                                    mAccessToken = savedAccessToken;
                                }

                                getArtists = findViewById(R.id.get_profile);
                                getArtists.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onGetUserProfileClickedA(MainActivity.this);
                                    }
                                });
                                Button getTracks = findViewById(R.id.get_tracks);
                                getTracks.setOnClickListener(((View view) -> {
                                    if (mainActivity != null) {
                                        mainActivity.onGetUserProfileClickedT(MainActivity.this);
                                    }
                                }));
                                Button next3 = findViewById(R.id.nextThree);
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
                                getRecs = findViewById(R.id.get_recs);
                                text_recs = findViewById(R.id.text_recs);
                                getRecs.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Track[] getRecView = getRecommendations();
                                        StringBuilder recBuilder = new StringBuilder();

                                        if (getRecView != null && getRecView.length > 0) {
                                            for (int i = 0; i < recAmmount; i++) {
                                                Track track = getRecView[i];
                                                String song = track.getName();
                                                ArtistSimplified[] artists = track.getArtists();
                                                String artist = "";

                                                if (artists != null && artists.length > 0) {
                                                    artist = artists[0].getName();
                                                }

                                                String result = artist + " - " + song;
                                                recBuilder.append(result);
                                                recBuilder.append("\n");
                                            }


                                            setTextAsync(recBuilder.toString(), text_recs);
                                        } else {
                                            setTextAsync("No recommendations available", text_recs);
                                        }
                                    }
                                });



                                next3.setOnClickListener(new View.OnClickListener() {
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
            ;

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
            if (mRefreshToken == null) {
                Toast.makeText(activity, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            } else {
                performRefreshTokenRequest(buildRefreshTokenRequest(mRefreshToken));
                Log.d("MyApp", mAccessToken);
            }
            return;
        }

        accessTokenViewModel = new ViewModelProvider(this).get(AccessTokenViewModel.class);

        // Retrieve access token from ViewModel
//        String savedAccessToken = accessTokenViewModel.getAccessToken();
//        if (savedAccessToken != null) {
//            // Access token already set, no need to request a new one
//            mAccessToken = savedAccessToken;
//        }

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

                    artistID = itemsArray.getJSONObject(0).getString("id");
                    JSONObject firstArtistObject = itemsArray.getJSONObject(0);
                    JSONArray genreArray = firstArtistObject.getJSONArray("genres");
                    genres = new ArrayList<>();

                    for (int j = 0; j < genreArray.length(); j++) {
                        String genre = genreArray.getString(j);
                        genres.add(genre);
                    }

                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject artistObject = itemsArray.getJSONObject(i);
                        String artistName = artistObject.getString("name");
                        stringBuilder.append(artistName).append("\n"); // Append each artist name to the StringBuilder
                    }

                    JSONArray imageArray = firstArtistObject.getJSONArray("images");
                    if (imageArray.length() > 0) {
                        JSONObject imageObject = imageArray.getJSONObject(0);
                        imageURL = imageObject.getString("url");
                    }

                    //uploadData(stringBuilder.toString());
                    artists = stringBuilder.toString();
                    // Update UI on the main thread
                    activity.runOnUiThread(() -> { setTextAsync(stringBuilder.toString(), text_home);

                        if (imageURL != null){
                            ImageView imageView = findViewById(R.id.artistImage);
                            Picasso.get()
                                    .load(imageURL)
                                    .resize(200, 200) // Set the desired width and height
                                    .centerCrop()     // Crop the image to fit the ImageView
                                    .into(imageView);
                        }
                    });


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
        text_track = findViewById(R.id.text_track);
        if (mAccessToken == null) {
            Toast.makeText(activity, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable the button to prevent multiple clicks
        playButton.setEnabled(false);

        accessTokenViewModel = new ViewModelProvider(this).get(AccessTokenViewModel.class);

        // Retrieve access token from ViewModel
//        String savedAccessToken = accessTokenViewModel.getAccessToken();
//        if (savedAccessToken != null) {
//            // Access token already set, no need to request a new one
//            mAccessToken = savedAccessToken;
//        }


        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=" + limit + "&offset=" + offset + "&total=" + total + "&time_range=" + time_range)
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
                    trackID = itemsArray.getJSONObject(0).getString("id");
                    JSONObject one = itemsArray.getJSONObject(0);
                    JSONObject album = one.getJSONObject("album");

                    JSONArray imageArray = album.getJSONArray("images");
                    if (imageArray.length() > 0) {
                        JSONObject imageObject = imageArray.getJSONObject(0);
                        trackURL = imageObject.getString("url");
                    }

                    // Prepare a list of preview URLs
                    List<String> previewUrls = new ArrayList<>();
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject song = itemsArray.optJSONObject(i);
                        JSONObject trackObject = itemsArray.getJSONObject(i);
                        String trackName = trackObject.getString("name");
                        stringBuilder.append(trackName).append("\n");

                        activity.runOnUiThread(() -> {
                            setTextAsync(stringBuilder.toString(), text_track);
                            // Enable the button again
                            if (trackURL != null){
                                ImageView imageView = findViewById(R.id.trackImage);
                                Picasso.get()
                                        .load(trackURL)
                                        .resize(200, 200) // Set the desired width and height
                                        .centerCrop()     // Crop the image to fit the ImageView
                                        .into(imageView);
                            }

                            playButton.setEnabled(true);
                        });

                        if (song != null) {
                            String previewUrl = song.optString("preview_url");
                            if (previewUrl != null && !previewUrl.isEmpty()) {
                                previewUrls.add(previewUrl);
                            }
                        }
                    }
                    uploadData(stringBuilder.toString());

                    // Ensure there are songs to play
                    if (!previewUrls.isEmpty()) {
                        // Start playing the first song
                        playTracks(activity, previewUrls, 0);
                    } else {
                        // No playable tracks found
                        activity.runOnUiThread(() -> Toast.makeText(activity, "No playable tracks found.", Toast.LENGTH_SHORT).show());
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

    // Method to play tracks
    // Method to play tracks
    private void playTracks(Activity activity, List<String> previewUrls, int currentIndex) {
        // Check if currentIndex is within bounds
        if (currentIndex >= 0 && currentIndex < previewUrls.size()) {
            String previewUrl = previewUrls.get(currentIndex);

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());

                mediaPlayer.setDataSource(previewUrl);

                mediaPlayer.setOnPreparedListener(mp -> {
                    // MediaPlayer is prepared, start playback
                    mediaPlayer.start();
                    isPlaying = true;

                    // Set up a listener for when the song completes
                    mediaPlayer.setOnCompletionListener(mp1 -> {
                        // Release the MediaPlayer resources
                        mediaPlayer.release();
                        mediaPlayer = null;

                        // Check if there are more tracks to play
                        if (currentIndex + 1 < previewUrls.size()) {
                            // Play the next track
                            playTracks(activity, previewUrls, currentIndex + 1);
                        } else {
                            // Reset currentIndex to 0 and play the first track again
                            playTracks(activity, previewUrls, 0);
                        }
                    });
                });

                mediaPlayer.prepareAsync(); // Prepare the MediaPlayer asynchronously

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("MediaPlayer", "Failed to set data source: " + e.getMessage());
                Toast.makeText(activity, "Failed to set data source for MediaPlayer.", Toast.LENGTH_SHORT).show();
                // Enable the button again
                playButton.setEnabled(true);
            }
        } else {
            // All tracks have been played, enable the button again
            activity.runOnUiThread(() -> playButton.setEnabled(true));
        }
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
                .setRedirectUri(URI.create(REDIRECT_URI))
                .setClientId(CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
                .build();
        mAccessToken = spotifyApi.getAccessToken();
        mRefreshToken = spotifyApi.getRefreshToken();
        Log.d("myapp", mRefreshToken);

        String combinedGenres = String.join(",", genres);

        GetRecommendationsRequest getRecommendationsRequest = spotifyApi
                .getRecommendations()
                .seed_artists(artistID) // Example artist ID
                .seed_genres(combinedGenres) // Example genres
                .seed_tracks(trackID) // Example track ID
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

            Log.d("Song",recommendations.getTracks()[1].toString());
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
        String username = uploadUsername.getText().toString();
        //String artists = "artists";

        DataClass dataClass = new DataClass(artists, username, text);
        //String currentDate = String.valueOf(Calendar.getInstance().getTime());

        FirebaseDatabase.getInstance().getReference("mixtape").child(username)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
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

//        firestore.collection("mixtape").document(username)
//                .set(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(MainActivity.this, "Saved in Firestore", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(MainActivity.this, "Failed to save in Firestore", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Overrid
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    private Request buildRefreshTokenRequest (String refreshToken){
        // Creating the request body
        RequestBody body = new FormBody.Builder()
                .add("refresh_token", refreshToken)
                .add("grant_type", "refresh_token")
                .add("client_id", CLIENT_ID)
                .build();

        // Creating the Authorization header
        String pre64 = String.format("%s:%s", CLIENT_ID, CLIENT_SECRET);
        String post64 = Base64.getEncoder().encodeToString(pre64.getBytes());

        // Creating the request
        return new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + post64)
                .build();
    }

    private void performRefreshTokenRequest(Request request) {
        // Performing the request
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("HTTP", "Failed to refresh access token: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    // Store the access token in the provided String parameter
                    mAccessToken = jsonObject.getString("access_token");
                    mRefreshToken = jsonObject.getString("refresh_token");
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}