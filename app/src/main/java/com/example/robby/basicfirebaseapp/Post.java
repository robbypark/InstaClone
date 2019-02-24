package com.example.robby.basicfirebaseapp;

import android.util.Base64;

public class Post {
    private String title;
    private long time;
    private String image;

    // empty constructor for Firebase
    public Post() {
    }

    public Post(String title, long time, String image) {
        this.title = title;
        this.time = time;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
