package com.example.robby.basicfirebaseapp;

public class Post {
    String title;
    long time;

    // empty constructor for Firebase
    public Post() {
    }

    public Post(String title, long time) {
        this.title = title;
        this.time = time;
    }
}
