package com.example.mixtape;

import android.widget.TextView;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class DataClass {
    private String dataArtists;
    private String dataTracks;
    private String dataUsername;
    private String key;
    public Map<String, Boolean> stars = new HashMap<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataClass(String dataItems) {
        this.dataArtists = dataItems;
//        this.dataTracks = dataTracks;
//        this.dataUsername = dataUsername;
        //this.dataImage = dataImage;
    }

    public String getDataArtists() {
        return dataArtists;
    }

    public String getDataTracks() {
        return dataTracks;
    }
    public String getDataUsername() {
        return dataUsername;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("TOP ARTISTS", dataArtists);
        //result.put("TOP TRACKS", dataTracks);
        result.put("stars", stars);

        return result;
    }

    //public String getDataImage() {
        //

    public DataClass() {
        //pending
    }
}
