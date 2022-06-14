package com.example.mindly;

public class Post {
    String textPost, username, userID;
    String images;

    public Post(String textPost, String username, String userID, String images) {
        this.textPost = textPost;
        this.username = username;
        this.userID = userID;
        this.images = images;
    }

    public Post(){

    }

    public String getImages() {
        return images;
    }

    public String getTextPost() {
        return textPost;
    }

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    public void setTextPost(String text_post) {
        this.textPost = text_post;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
