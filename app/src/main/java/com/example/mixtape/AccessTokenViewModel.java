package com.example.mixtape;

import androidx.lifecycle.ViewModel;

public class AccessTokenViewModel extends ViewModel {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
