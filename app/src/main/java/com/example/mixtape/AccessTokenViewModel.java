package com.example.mixtape;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.ViewModel;

public class AccessTokenViewModel extends ViewModel {

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private SharedPreferences sharedPreferences;

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public void setAccessToken(String accessToken) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
    }

    public void setRefreshToken(String refreshToken) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply();
    }
}
