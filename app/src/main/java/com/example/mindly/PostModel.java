package com.example.mindly;

import android.media.Image;

public class PostModel {
    String text_post, username, at_username;
    Image image_post;

    public PostModel(String text_post, String username, String at_username, Image image_post) {
        this.text_post = text_post;
        this.username = username;
        this.at_username = at_username;
        this.image_post = image_post;
    }

    public String getText_post() {
        return text_post;
    }

    public String getUsername() {
        return username;
    }

    public String getAt_username() {
        return at_username;
    }

    public Image getImage_post() {
        return image_post;
    }
}
