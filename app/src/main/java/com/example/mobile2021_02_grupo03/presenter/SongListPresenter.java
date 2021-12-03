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
    public static ArrayList<Song> songs = new ArrayList<>();
    public static int selectedLayout;
    public static int selectedPosition;
    public static String selectedName = "";
    private SongListActivity songListActivity;
    private SongsAdapter adapter = new SongsAdapter(songs, this);

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
            songListActivity.displaySongs(adapter);
        }
    }

    public void getRecentSongsFromSQLite(){
        if(selectedLayout != 2) {
            MusicAppDBHelper dbHelper = new MusicAppDBHelper(songListActivity.getApplicationContext());
            SQLiteDatabase dbread = dbHelper.getReadableDatabase();

            songs.clear();

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
                songs.add(song);
                cursor.moveToPrevious();
            }
            selectedLayout = 2;
            songListActivity.displaySongs(adapter);
        }
    }

    public void onItemClick(int position){
        if(selectedName.equals(songs.get(position).getTitle())){
            songListActivity.startActivity(new Intent(songListActivity, SongPlayerActivity.class));
        } else{
            mediaPlayerCreate(position);
            songListActivity.pauseButtonResource();
            selectedName = songs.get(position).getTitle();
        }
        insertRecentSong(selectedName, songs.get(position).getPath());
        songListActivity.updateLayout(songs.get(position));
        if(selectedLayout == 2){
            getAllSongsFromSQLite();
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
        Uri uri = Uri.parse(songs.get(position).getPath());
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