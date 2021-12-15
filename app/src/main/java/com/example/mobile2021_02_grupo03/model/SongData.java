package com.example.mobile2021_02_grupo03.model;

import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.mobile2021_02_grupo03.adapter.SongsAdapter;
import com.example.mobile2021_02_grupo03.view.SongListActivity;

import java.util.ArrayList;

public class SongData{
    public static MediaPlayer mediaPlayer;
    public static ArrayList<Song> selectedSongs = new ArrayList<>();
    public static int selectedPosition;
    public static Song selectedSong = new Song("", "", "");
    public static String selectedLayout;
    public static ArrayList<Song> allSongs = new ArrayList<>();
    public static ArrayList<Song> streamingSongs = new ArrayList<>();
    public static ArrayList<Song> recentSongs = new ArrayList<>();
    public static ArrayList<Song> favoriteSongs = new ArrayList<>();
    public static boolean isLogged = false;
}
