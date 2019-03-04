package com.example.robby.basicfirebaseapp.model;

public class Comment {
    private String uid;
    private long time;
    private String comment;

    // for Firebase
    public Comment() {
    }

    public Comment(String uid, String comment) {
        this.uid = uid;
        this.comment = comment;
        this.time = System.currentTimeMillis();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
