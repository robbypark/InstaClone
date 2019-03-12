package com.example.robby.basicfirebaseapp.model;

public class User {

    private String username;
    private String email;
    private String image;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return email != null ? email.equals(user.email) : user.email == null;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

//    public User(String username, String email) {
//        this.username = username;
//        this.email = email;
//    }

    public User(String username, String email, String image) {
        this.username = username;
        this.email = email;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
