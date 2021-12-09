package com.example.mobile2021_02_grupo03.presenter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;
import com.example.mobile2021_02_grupo03.adapter.SongsAdapterDataBinding;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.view.SongListActivity;
import com.example.mobile2021_02_grupo03.view.SongPlayerActivity;

import java.util.ArrayList;

public class SongListPresenter {

    public static MediaPlayer mediaPlayer;
    public static int selectedPosition = -1;
    public static String selectedName = "";
    public ArrayList<Song> selectedList = new ArrayList<>();
    public ArrayList<Song> recentSongs = new ArrayList<>();
    public ArrayList<Song> songs = new ArrayList<>();
    public ArrayList<Song> allSongs = new ArrayList<>();
    public SongListActivity activity;
    public int selectedLayout = 0;
    public SongsAdapterDataBinding songsAdapter;


    public SongListPresenter(SongListActivity activity) {
        this.activity = activity;

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0, 10);
        activity.layout.recyclerView.setLayoutParams(param);

        getAllSongsFromSQLite();
        songsAdapter = new SongsAdapterDataBinding(this, songs);
        activity.layout.recyclerView.setAdapter(songsAdapter);

        activity.setSupportActionBar(activity.layout.toolbar);
        activity.layout.navigationView.setNavigationItemSelectedListener(activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, activity.layout.drawerLayout, activity.layout.toolbar, R.string.open_drawer, R.string.close_drawer);
        activity.layout.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void onItemClick(int position){
        activity.invalidateOptionsMenu();

        if(selectedName.equals(selectedList.get(position).getTitle())){
            activity.startActivity(new Intent(activity, SongPlayerActivity.class).putExtra("songs", allSongs));
        } else{
            mediaPlayerCreate(position);
            activity.layout.listLayoutPlayBtn.setBackgroundResource(R.drawable.ic_play);
            selectedName = selectedList.get(position).getTitle();
        }
        insertRecentSong(selectedName, selectedList.get(position).getPath());
        if(selectedLayout == 2){
            getAllSongsFromSQLite();
        } else{
            selectedLayout = 0;
        }
        for(int i = 0; i < allSongs.size(); i++){
            if(allSongs.get(i).getTitle().equals(selectedName)){
                selectedPosition = i;
                break;
            }
        }
        activity.layout.recyclerView.scrollToPosition(selectedPosition);
        openMiniPlayer();

    }

    public void openMiniPlayer(){
        if(selectedPosition != -1){
            activity.layout.recyclerView.scrollToPosition(selectedPosition);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0, 8);
            activity.layout.recyclerView.setLayoutParams(param);
            activity.layout.listLayoutPlayer.setVisibility(View.VISIBLE);
            activity.layout.setSong(allSongs.get(selectedPosition));
            if(mediaPlayer.isPlaying()){
                activity.layout.listLayoutPlayBtn.setBackgroundResource(R.drawable.ic_play);
            } else{
                activity.layout.listLayoutPlayBtn.setBackgroundResource(R.drawable.ic_pause);
            }
        }
    }

    public void closeMiniPlayer(){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0, 10);
        activity.layout.recyclerView.setLayoutParams(param);
        activity.layout.listLayoutPlayer.setVisibility(View.INVISIBLE);
    }

    public void mediaPlayerCreate(int position){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Uri uri = Uri.parse(selectedList.get(position).getPath());
        mediaPlayer = MediaPlayer.create(activity.getApplicationContext(), uri);
        mediaPlayer.start();
    }

    public void prevMiniPlayer(){
        int position = ((selectedPosition-1)<0)?(songs.size()-1):(selectedPosition-1);
        if(selectedLayout == 2){
            getAllSongsFromSQLite();
        }
        activity.layout.recyclerView.scrollToPosition(position);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.layout.recyclerView.findViewHolderForAdapterPosition(position).itemView.performClick();
            }
        }, 100);
    }

    public void playMiniPlayer(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            activity.layout.listLayoutPlayBtn.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.pause();
        } else{
            activity.layout.listLayoutPlayBtn.setBackgroundResource(R.drawable.ic_play);
            mediaPlayer.start();
        }
    }

    public void nextMiniPlayer(){
        int position = ((selectedPosition+1)%songs.size());
        if(selectedLayout == 2){
            getAllSongsFromSQLite();
        }
        activity.layout.recyclerView.scrollToPosition(position);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.layout.recyclerView.findViewHolderForAdapterPosition(position).itemView.performClick();
            }
        }, 100);
    }

    public void getAllSongsFromSQLite(){
        if(selectedLayout != 1){
            MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
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
            allSongs.addAll(songs);
            selectedLayout = 1;
            selectedList = songs;
            activity.layout.toolbar.setTitle(R.string.all_songs);
            activity.layout.recyclerView.setAdapter(songsAdapter);
        }
    }

    public void getRecentSongsFromSQLite(){
        if(selectedLayout != 2) {
            MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
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
            activity.layout.toolbar.setTitle(R.string.recent_songs);
            activity.layout.recyclerView.setAdapter(songsAdapter);
        }
    }

    public void insertRecentSong(String name, String path){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
        dbHelper.insert(dbwrite, MusicAppDBContract.recentSongsTable.TABLE_NAME, name, path);
    }
}