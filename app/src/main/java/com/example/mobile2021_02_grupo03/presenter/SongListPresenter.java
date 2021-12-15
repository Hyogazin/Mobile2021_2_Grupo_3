package com.example.mobile2021_02_grupo03.presenter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;
import com.example.mobile2021_02_grupo03.adapter.SongsAdapter;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.model.SongData;
import com.example.mobile2021_02_grupo03.view.LoginActivity;
import com.example.mobile2021_02_grupo03.view.SongListActivity;
import com.example.mobile2021_02_grupo03.view.SongPlayerActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class SongListPresenter implements Response.Listener<JSONObject>, Response.ErrorListener{

    public SongListActivity activity;
    public SongsAdapter songsAdapter;
    private boolean stopHandler = false;


    public SongListPresenter(SongListActivity activity) {
        this.activity = activity;

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0, 10);
        activity.layout.recyclerView.setLayoutParams(param);

        getFavoriteSongs();
        getAllSongsFromSQLite();
        songsAdapter = new SongsAdapter(this);
        activity.layout.recyclerView.setAdapter(songsAdapter);

        activity.setSupportActionBar(activity.layout.toolbar);
        activity.layout.navigationView.setNavigationItemSelectedListener(activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, activity.layout.drawerLayout, activity.layout.toolbar, R.string.open_drawer, R.string.close_drawer);
        activity.layout.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void onResumeLayout(){
        if(!SongData.isLogged){
            onLogoff();
        }

        stopHandler = false;
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SongData.mediaPlayer != null && SongData.mediaPlayer.getCurrentPosition() >= SongData.mediaPlayer.getDuration()-500){
                    onNextClick();
                }
                if(!stopHandler){
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
        songsAdapter.notifyDataSetChanged();
        openMiniPlayer();
        activity.layout.recyclerView.scrollToPosition(SongData.selectedPosition);
    }

    public void onItemClick(int position){
        activity.invalidateOptionsMenu();

        if(SongData.selectedSong.getTitle().equals(SongData.selectedSongs.get(position).getTitle()) && SongData.selectedSong.getLayout().equals(SongData.selectedLayout)/*QUALQUER COISA REMOVER ESSA SEGUNDA CONDICAO*/){
            onPlayerClick();
        } else {
            SongData.selectedSong = SongData.selectedSongs.get(position);
            if (SongData.selectedLayout.equals("recent")) {
                activity.layout.recyclerView.setAdapter(songsAdapter);
            }
            if (SongData.selectedSong.getLayout().equals("all")) {
                SongData.selectedLayout = "all";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.allSongs);
                activity.layout.toolbar.setTitle(R.string.all_songs);
            } else if (SongData.selectedSong.getLayout().equals("streaming")) {
                SongData.selectedLayout = "streaming";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.streamingSongs);
                activity.layout.toolbar.setTitle(R.string.streaming_songs);
            } else if (SongData.selectedSong.getLayout().equals("favorite")) {
                SongData.selectedLayout = "favorite";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.favoriteSongs);
                activity.layout.toolbar.setTitle(R.string.favorite_songs);
            }
            for (int i = 0; i < SongData.selectedSongs.size(); i++) {
                if (SongData.selectedSong.getTitle().equals(SongData.selectedSongs.get(i).getTitle())) {
                    SongData.selectedPosition = i;
                    break;
                }
            }
            createMediaPlayer();
            songsAdapter.notifyDataSetChanged();
            activity.layout.recyclerView.scrollToPosition(SongData.selectedPosition);
        }
    }

    public void createMediaPlayer(){
        insertRecentSong(SongData.selectedSong.getTitle(), SongData.selectedSong.getPath(), SongData.selectedSong.getLayout());
        if(SongData.mediaPlayer != null){
            SongData.mediaPlayer.stop();
            SongData.mediaPlayer.release();
        }
        Uri uri = Uri.parse(SongData.selectedSong.getPath());
        SongData.mediaPlayer = MediaPlayer.create(activity.getApplicationContext(), uri);
        SongData.mediaPlayer.start();
        openMiniPlayer();
    }

    public void onPlayerClick(){
        if(!SongData.selectedLayout.equals(SongData.selectedSong.getLayout())){
            if(SongData.selectedSong.getLayout().equals("all")){
                SongData.selectedLayout = "all";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.allSongs);
                activity.layout.toolbar.setTitle(R.string.all_songs);
            } else if(SongData.selectedSong.getLayout().equals("streaming")){
                SongData.selectedLayout = "streaming";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.streamingSongs);
                activity.layout.toolbar.setTitle(R.string.streaming_songs);
            } else if(SongData.selectedSong.getLayout().equals("favorite")){
                SongData.selectedLayout = "favorite";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.favoriteSongs);
                activity.layout.toolbar.setTitle(R.string.favorite_songs);
            }
            activity.layout.recyclerView.setAdapter(songsAdapter);
        }
        stopHandler = true;
        activity.layout.recyclerView.scrollToPosition(SongData.selectedPosition);
        Intent intent = new Intent(activity, SongPlayerActivity.class);
        activity.startActivity(intent);
    }

    public void openMiniPlayer(){
        if(SongData.selectedPosition != -1 && SongData.mediaPlayer != null){
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0, 8);
            activity.layout.recyclerView.setLayoutParams(param);
            activity.layout.listLayoutPlayer.setVisibility(View.VISIBLE);
            activity.layout.setSong(SongData.selectedSong);
            if(SongData.selectedSong.getLayout().equals("all")){
                activity.layout.listlayoutPlaylistName.setText(R.string.all_songs);
            } else if(SongData.selectedSong.getLayout().equals("streaming")){
                activity.layout.listlayoutPlaylistName.setText(R.string.streaming_songs);
            } else if(SongData.selectedSong.getLayout().equals("favorite")){
                activity.layout.listlayoutPlaylistName.setText(R.string.favorite_songs);
            }
            if(SongData.mediaPlayer.isPlaying()){
                activity.layout.listLayoutPlayBtn.setBackgroundResource(R.drawable.ic_play);
            } else{
                activity.layout.listLayoutPlayBtn.setBackgroundResource(R.drawable.ic_pause);
            }
        }
    }

    public void closeMiniPlayer(){
        Animation animation = AnimationUtils.loadAnimation(activity.layout.listLayoutPlayer.getContext(), android.R.anim.fade_out);
        animation.setDuration(100);
        activity.layout.listLayoutPlayer.startAnimation(animation);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0, 10);
                activity.layout.recyclerView.setLayoutParams(param);
                activity.layout.listLayoutPlayer.setVisibility(View.INVISIBLE);
            }
        }, 100);
    }

    public void onPrevClick(){
        if(SongData.selectedSongs.size() > 0 && !(SongData.favoriteSongs.size() == 0 && SongData.selectedSong.getLayout().equals("favorite"))){
            if(SongData.selectedSong.getLayout().equals("all") && !SongData.selectedLayout.equals("all")){
                SongData.selectedLayout = "all";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.allSongs);
                activity.layout.toolbar.setTitle(R.string.all_songs);
                activity.layout.recyclerView.setAdapter(songsAdapter);
            } else if(SongData.selectedSong.getLayout().equals("streaming") && !SongData.selectedLayout.equals("streaming")){
                SongData.selectedLayout = "streaming";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.streamingSongs);
                activity.layout.toolbar.setTitle(R.string.streaming_songs);
                activity.layout.recyclerView.setAdapter(songsAdapter);
            } else if(SongData.selectedSong.getLayout().equals("favorite") && !SongData.selectedLayout.equals("favorite")){
                SongData.selectedLayout = "favorite";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.favoriteSongs);
                activity.layout.toolbar.setTitle(R.string.favorite_songs);
                activity.layout.recyclerView.setAdapter(songsAdapter);
            }
            SongData.selectedPosition = ((SongData.selectedPosition-1)<0)?(SongData.selectedSongs.size()-1):(SongData.selectedPosition-1);
            SongData.selectedSong = SongData.selectedSongs.get(SongData.selectedPosition);
            createMediaPlayer();
            songsAdapter.notifyDataSetChanged();
            activity.layout.recyclerView.scrollToPosition(SongData.selectedPosition);
        }
    }

    public void playMiniPlayer(){
        if(SongData.mediaPlayer != null && SongData.mediaPlayer.isPlaying()){
            activity.layout.listLayoutPlayBtn.setBackgroundResource(R.drawable.ic_pause);
            SongData.mediaPlayer.pause();
        } else{
            activity.layout.listLayoutPlayBtn.setBackgroundResource(R.drawable.ic_play);
            SongData.mediaPlayer.start();
        }
    }

    public void onNextClick(){
        if(SongData.selectedSongs.size() > 0 && !(SongData.favoriteSongs.size() == 0 && SongData.selectedSong.getLayout().equals("favorite"))){
            if(SongData.selectedSong.getLayout().equals("all") && !SongData.selectedLayout.equals("all")){
                SongData.selectedLayout = "all";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.allSongs);
                activity.layout.toolbar.setTitle(R.string.all_songs);
                activity.layout.recyclerView.setAdapter(songsAdapter);
            } else if(SongData.selectedSong.getLayout().equals("streaming") && !SongData.selectedLayout.equals("streaming")){
                SongData.selectedLayout = "streaming";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.streamingSongs);
                activity.layout.toolbar.setTitle(R.string.streaming_songs);
                activity.layout.recyclerView.setAdapter(songsAdapter);
            } else if(SongData.selectedSong.getLayout().equals("favorite") && !SongData.selectedLayout.equals("favorite")){
                SongData.selectedLayout = "favorite";
                SongData.selectedSongs.clear();
                SongData.selectedSongs.addAll(SongData.favoriteSongs);
                activity.layout.toolbar.setTitle(R.string.favorite_songs);
                activity.layout.recyclerView.setAdapter(songsAdapter);
            }
            SongData.selectedPosition = (SongData.selectedPosition+1)%SongData.selectedSongs.size();
            SongData.selectedSong = SongData.selectedSongs.get(SongData.selectedPosition);
            createMediaPlayer();
            songsAdapter.notifyDataSetChanged();
            activity.layout.recyclerView.scrollToPosition(SongData.selectedPosition);
        }
    }

    public void addFavoriteSong(Song song){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();

        String songName = song.getTitle();
        String songPath = song.getPath();
        String songLayout = "favorite";
        boolean isFavorite = false;
        for(int i = 0; i < SongData.favoriteSongs.size(); i++){
            if(SongData.favoriteSongs.get(i).getTitle().equals(songName)){
                SongData.favoriteSongs.remove(i);
                if(SongData.selectedLayout.equals("favorite")){
                    SongData.selectedSongs.remove(i);
                }
                dbHelper.delete(dbwrite, MusicAppDBContract.favoriteSongsTable.TABLE_NAME, song.getTitle());
                isFavorite = true;
                break;
            }
        }
        if(!isFavorite){
            SongData.favoriteSongs.add(new Song(songName,songPath,songLayout));
            dbHelper.insert(dbwrite, MusicAppDBContract.favoriteSongsTable.TABLE_NAME, song.getTitle(), song.getPath());
        }
        songsAdapter.notifyDataSetChanged();
    }

    public void insertRecentSong(String name, String path, String playlist){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
        dbHelper.insert(dbwrite, MusicAppDBContract.recentSongsTable.TABLE_NAME, name, path, playlist);
    }

    public void getFavoriteSongs(){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
        SQLiteDatabase dbread = dbHelper.getReadableDatabase();

        SongData.favoriteSongs.clear();
        SongData.selectedSongs.clear();

        String[] colunas = {
                BaseColumns._ID,
                MusicAppDBContract.songsTable.COLUMN_NAME_NAME,
                MusicAppDBContract.songsTable.COLUMN_NAME_PATH
        };

        Cursor cursor = dbHelper.select(dbread, MusicAppDBContract.favoriteSongsTable.TABLE_NAME, colunas, MusicAppDBContract.songsTable.COLUMN_NAME_NAME);

        cursor.moveToFirst();
        for(int i = 0; i<cursor.getCount(); i++){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable.COLUMN_NAME_NAME));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable.COLUMN_NAME_PATH));
            String layout = "favorite";
            Song song = new Song(id, name, path, layout);
            SongData.favoriteSongs.add(song);
            cursor.moveToNext();
        }
        SongData.selectedLayout = "favorite";
        SongData.selectedSongs.addAll(SongData.favoriteSongs);
        activity.layout.toolbar.setTitle(R.string.favorite_songs);
        activity.layout.recyclerView.setAdapter(songsAdapter);
    }

    public void getAllSongsFromSQLite(){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
        SQLiteDatabase dbread = dbHelper.getReadableDatabase();

        SongData.allSongs.clear();
        SongData.selectedSongs.clear();

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
            String layout = "all";
            Song song = new Song(id, name, path, layout);
            SongData.allSongs.add(song);
            cursor.moveToNext();
        }
        SongData.selectedLayout = "all";
        SongData.selectedSongs.addAll(SongData.allSongs);
        activity.layout.toolbar.setTitle(R.string.all_songs);
        activity.layout.recyclerView.setAdapter(songsAdapter);
    }

    public void getRecentSongsFromSQLite(){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
        SQLiteDatabase dbread = dbHelper.getReadableDatabase();

        SongData.recentSongs.clear();
        SongData.selectedSongs.clear();

        String[] colunas = {
                BaseColumns._ID,
                MusicAppDBContract.recentSongsTable.COLUMN_NAME_NAME,
                MusicAppDBContract.recentSongsTable.COLUMN_NAME_PATH,
                MusicAppDBContract.recentSongsTable.COLUMN_NAME_PLAYLIST
        };
        Cursor cursor = dbHelper.select(dbread, MusicAppDBContract.recentSongsTable.TABLE_NAME, colunas);

        cursor.moveToLast();
        for (int i = 0; i < cursor.getCount(); i++) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MusicAppDBContract.recentSongsTable._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.recentSongsTable.COLUMN_NAME_NAME));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.recentSongsTable.COLUMN_NAME_PATH));
            String layout = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.recentSongsTable.COLUMN_NAME_PLAYLIST));
            Song song = new Song(id, name, path, layout);
            SongData.recentSongs.add(song);
            cursor.moveToPrevious();
        }
        SongData.selectedLayout = "recent";
        SongData.selectedSongs.addAll(SongData.recentSongs);
        activity.layout.toolbar.setTitle(R.string.recent_songs);
        activity.layout.recyclerView.setAdapter(songsAdapter);
    }

    public void getStreamingSongs() {
        RequestQueue queue = Volley.newRequestQueue(activity);

        JsonObjectRequest requisition = new JsonObjectRequest(Request.Method.GET,
                "https://api.jsonbin.io/b/61b5ebfd62ed886f915ec6c0/1", null,
                this, this);

        queue.add(requisition);
        activity.layout.recyclerView.setVisibility(View.GONE);
        activity.layout.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(JSONObject response) {
        SongData.streamingSongs.clear();
        SongData.selectedSongs.clear();
        try {
            for (int i = 0; i < response.getJSONArray("array").length(); i++) {
                SongData.streamingSongs.add(new Song(response.getJSONArray("array").getJSONObject(i)));
            }
            SongData.selectedLayout = "streaming";
            SongData.selectedSongs.addAll(SongData.streamingSongs);
            activity.layout.toolbar.setTitle(R.string.streaming_songs);
            activity.layout.recyclerView.setAdapter(songsAdapter);
            activity.layout.recyclerView.setVisibility(View.VISIBLE);
            activity.layout.progressBar.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("ERROR: ",error.toString());
    }

    public void onLogoff(){
        stopHandler = true;
        SongData.isLogged = false;
        if(SongData.mediaPlayer != null){
            SongData.mediaPlayer.pause();
        }
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }
}