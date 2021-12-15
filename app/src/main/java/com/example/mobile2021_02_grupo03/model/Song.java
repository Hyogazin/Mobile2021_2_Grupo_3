package com.example.mobile2021_02_grupo03.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Song implements Parcelable {
    private int id;
    private String title;
    private String path;
    private String layout;

    public Song(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.title = json.getString("title");
            this.path = path = json.getString("path");
            this.layout = "streaming";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Song(String title, String path, String layout) {
        this.title = title;
        this.path = path;
        this.layout = layout;
    }

    public Song(int id, String title, String path, String layout) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.layout = layout;
    }

    protected Song(Parcel in) {
        id = in.readInt();
        title = in.readString();
        path = in.readString();
        layout = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public Song(Song song) {
        this.id = song.id;
        this.title = song.title;
        this.path = song.path;
        this.layout = song.layout;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(path);
    }
}
