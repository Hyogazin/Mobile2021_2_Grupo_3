package com.example.mobile2021_02_grupo03.view;

public class Song {
    private String title;
    private int image;

    public Song(String title, String artist, int image){
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
