package com.example.mobile2021_02_grupo03.presenter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.model.SongData;
import com.example.mobile2021_02_grupo03.view.SongPlayerActivity;

import java.util.ArrayList;

public class SongPlayerPresenter {

    private SongPlayerActivity activity;
    private boolean stopHandler;

    public SongPlayerPresenter(SongPlayerActivity activity) {
        this.activity = activity;

        stopHandler = false;

        activity.setSupportActionBar(activity.layout.toolbar);
        activity.getSupportActionBar().setTitle(R.string.app_name);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateLayout();
    }

    public void updateLayout(){
        activity.layout.setSong(SongData.selectedSong);
        if(SongData.selectedSong.getLayout().equals("all")){
            activity.layout.playerPlaylistName.setText(R.string.all_songs);
        } else if(SongData.selectedSong.getLayout().equals("streaming")){
            activity.layout.playerPlaylistName.setText(R.string.streaming_songs);
        } else if(SongData.selectedSong.getLayout().equals("favorite")){
            activity.layout.playerPlaylistName.setText(R.string.favorite_songs);
        }
        activity.layout.playerSeekbar.setMax(SongData.mediaPlayer.getDuration());
        activity.layout.playerDuration.setText(createTime(SongData.mediaPlayer.getDuration()));
        if(SongData.mediaPlayer.isPlaying()){
            activity.layout.playerPlay.setBackgroundResource(R.drawable.ic_play);
        } else{
            activity.layout.playerPlay.setBackgroundResource(R.drawable.ic_pause);
        }
        final Handler handler = new Handler();
        final int delay = 100;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.layout.playerTime.setText(createTime(SongData.mediaPlayer.getCurrentPosition()));
                activity.layout.playerSeekbar.setProgress(SongData.mediaPlayer.getCurrentPosition());
                if(SongData.mediaPlayer.getCurrentPosition() >= SongData.mediaPlayer.getDuration()-500){
                    nextPlayer();
                }
                if(!stopHandler){
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    public void createMediaPlayer(){
        SongData.mediaPlayer.stop();
        SongData.mediaPlayer.release();
        Uri uri = Uri.parse(SongData.selectedSongs.get(SongData.selectedPosition).getPath());
        SongData.mediaPlayer = MediaPlayer.create(activity, uri);
        SongData.mediaPlayer.start();
    }

    public void jumpToTime(){
        SongData.mediaPlayer.seekTo(activity.layout.playerSeekbar.getProgress());
    }

    public void rewindPlayer(){
        SongData.mediaPlayer.seekTo(SongData.mediaPlayer.getCurrentPosition()-10000);
    }

    public void prevPlayer(){
        if(SongData.selectedSongs.size() > 0 && !(SongData.favoriteSongs.size() == 0 && SongData.selectedSong.getLayout().equals("favorite"))){
            SongData.selectedPosition = ((SongData.selectedPosition-1)<0)?(SongData.selectedSongs.size()-1):(SongData.selectedPosition-1);
            SongData.selectedSong = SongData.selectedSongs.get(SongData.selectedPosition);
            insertRecentSong(SongData.selectedSong.getTitle(), SongData.selectedSong.getPath(), SongData.selectedSong.getLayout());
            createMediaPlayer();
            previousAnimation();
            updateLayout();
        }
    }

    public void playPlayer(){
        if(SongData.mediaPlayer.isPlaying()){
            SongData.mediaPlayer.pause();
            activity.layout.playerPlay.setBackgroundResource(R.drawable.ic_pause);
        } else {
            SongData.mediaPlayer.start();
            activity.layout.playerPlay.setBackgroundResource(R.drawable.ic_play);
        }
    }

    public void nextPlayer(){
        if(SongData.selectedSongs.size() > 0 && !(SongData.favoriteSongs.size() == 0 && SongData.selectedSong.getLayout().equals("favorite"))){
            SongData.selectedPosition = (SongData.selectedPosition+1)%SongData.selectedSongs.size();
            SongData.selectedSong = SongData.selectedSongs.get(SongData.selectedPosition);
            insertRecentSong(SongData.selectedSong.getTitle(), SongData.selectedSong.getPath(), SongData.selectedSong.getLayout());
            createMediaPlayer();
            nextAnimation();
            updateLayout();
        }
    }


    public void forwardPlayer(){
        SongData.mediaPlayer.seekTo(SongData.mediaPlayer.getCurrentPosition()+10000);
    }

    public void nextAnimation(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(activity.layout.playerImg, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public void previousAnimation(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(activity.layout.playerImg, "rotation", 360f, 0f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String createTime(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time += min+":";
        if(sec<10) {
            time += "0" + sec;
        }else{
            time += sec;
        }
        return time;
    }

    public void insertRecentSong(String name, String path, String playlist){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
        dbHelper.insert(dbwrite, MusicAppDBContract.recentSongsTable.TABLE_NAME, name, path, playlist);
    }

    public void stopHandler(){
        stopHandler = true;
    }
}