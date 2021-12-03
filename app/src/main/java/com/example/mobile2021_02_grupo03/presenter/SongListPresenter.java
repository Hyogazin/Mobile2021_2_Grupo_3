package com.example.mobile2021_02_grupo03.presenter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;
import com.example.mobile2021_02_grupo03.adapter.SongsAdapter;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.view.SongListActivity;
import com.example.mobile2021_02_grupo03.view.SongPlayerActivity;

import java.util.ArrayList;

public class SongListPresenter {

    public static MediaPlayer mediaPlayer;
    public static ArrayList<Song> selectedList;
    public static ArrayList<Song> songs = new ArrayList<>();
    public ArrayList<Song> recentSongs = new ArrayList<>();
    public static int selectedLayout;
    public static int selectedPosition;
    public static String selectedName = "";
    private SongListActivity songListActivity;
    private SongsAdapter allSongsAdapter = new SongsAdapter(songs, this);
    private SongsAdapter recentSongsAdapter = new SongsAdapter(recentSongs, this);

    public SongListPresenter(SongListActivity songListActivity) {
        this.songListActivity = songListActivity;
    }

    public void getAllSongsFromSQLite(){
        if(selectedLayout != 1){
            MusicAppDBHelper dbHelper = new MusicAppDBHelper(songListActivity.getApplicationContext());
            SQLiteDatabase dbread = dbHelper.getReadableDatabase();

            songs.clear();

            String[] colunas = {
                    BaseColumns._ID,
                    MusicAppDBContract.songsTable.COLUMN_NAME_NAME,
                    MusicAppDBContract.songsTable.COLUMN_NAME_PATH
            };

            Cursor cursor = dbHelper.select(dbread, MusicAppDBContract.songsTable.TABLE_NAME, colunas, MusicAppDBContract.songsTable.COLUMN_NAME_NAME);

            cursor.moveToFirst();
            for(int i = 0; i<cursor.getCount(); i++){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable.COLUMN_NAME_NAME));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable.COLUMN_NAME_PATH));
                Song song = new Song(id, name, path);
                songs.add(song);
                cursor.moveToNext();
            }
            selectedLayout = 1;
            selectedList = songs;
            songListActivity.getSupportActionBar().setTitle("Todas as Músicas");
            songListActivity.displaySongs(allSongsAdapter);
        }
    }

    public void getRecentSongsFromSQLite(){
        if(selectedLayout != 2) {
            MusicAppDBHelper dbHelper = new MusicAppDBHelper(songListActivity.getApplicationContext());
            SQLiteDatabase dbread = dbHelper.getReadableDatabase();

            recentSongs.clear();

            String[] colunas = {
                    BaseColumns._ID,
                    MusicAppDBContract.recentSongsTable.COLUMN_NAME_NAME,
                    MusicAppDBContract.recentSongsTable.COLUMN_NAME_PATH
            };
            Cursor cursor = dbHelper.select(dbread, MusicAppDBContract.recentSongsTable.TABLE_NAME, colunas);

            cursor.moveToLast();
            for (int i = 0; i < cursor.getCount(); i++) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MusicAppDBContract.recentSongsTable._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.recentSongsTable.COLUMN_NAME_NAME));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.recentSongsTable.COLUMN_NAME_PATH));
                Song song = new Song(id, name, path);
                recentSongs.add(song);
                cursor.moveToPrevious();
            }
            selectedLayout = 2;
            selectedList = recentSongs;
            songListActivity.getSupportActionBar().setTitle("Músicas Recentes");
            songListActivity.displaySongs(recentSongsAdapter);
        }
    }

    public void onItemClick(int position){
        if(selectedName.equals(selectedList.get(position).getTitle())){
            songListActivity.startActivity(new Intent(songListActivity, SongPlayerActivity.class));
        } else{
            mediaPlayerCreate(position);
            songListActivity.pauseButtonResource();
            selectedName = selectedList.get(position).getTitle();
        }
        insertRecentSong(selectedName, selectedList.get(position).getPath());
        songListActivity.updateLayout(selectedName);
        if(selectedLayout == 2){
            for(int i = 0; i < songs.size(); i++){
                if(songs.get(i).getTitle().equals(recentSongs.get(position).getTitle())){
                    selectedPosition = i;
                    break;
                }
            }
            getAllSongsFromSQLite();
        } else{
            selectedPosition = position;
        }
    }

    public void onPlayerClick(){
        songListActivity.startActivity(new Intent(songListActivity.getApplicationContext(), SongPlayerActivity.class).putExtra("mySongs", songs).putExtra("position", selectedPosition));
    }

    public void insertRecentSong(String name, String path){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(songListActivity.getApplicationContext());
        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
        dbHelper.insert(dbwrite, MusicAppDBContract.recentSongsTable.TABLE_NAME, name, path);
    }

    public void mediaPlayerCreate(int position){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Uri uri = Uri.parse(selectedList.get(position).getPath());
        mediaPlayer = MediaPlayer.create(songListActivity.getApplicationContext(), uri);
        mediaPlayer.start();
    }

    public int mediaPlayerPrevPosition(){
        return ((selectedPosition-1)<0)?(songs.size()-1):(selectedPosition-1);
    }

    public int mediaPlayerNextPosition(){
        return  ((selectedPosition+1)%songs.size());
    }
}