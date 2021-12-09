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
import com.example.mobile2021_02_grupo03.view.SongPlayerActivityDataBinding;

import java.util.ArrayList;

public class SongPlayerPresenterDataBinding {

    private SongPlayerActivityDataBinding activity;
    private ArrayList<Song> songs;
    private boolean stopHandler;

    public SongPlayerPresenterDataBinding(SongPlayerActivityDataBinding activity) {
        this.activity = activity;

        stopHandler = false;

        Bundle bundle = activity.getIntent().getExtras();
        songs = bundle.getParcelableArrayList("songs");

        activity.setSupportActionBar(activity.layout.toolbar);
        activity.getSupportActionBar().setTitle(R.string.app_name);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateLayout();
    }

    public void updateLayout(){
        activity.layout.setSong(songs.get(SongListPresenterDataBinding.selectedPosition));

        activity.layout.playerSeekbar.setMax(SongListPresenterDataBinding.mediaPlayer.getDuration());
        activity.layout.playerDuration.setText(createTime(SongListPresenterDataBinding.mediaPlayer.getDuration()));

        if(SongListPresenterDataBinding.mediaPlayer.isPlaying()){
            activity.layout.playerPlay.setBackgroundResource(R.drawable.ic_play);
        } else{
            activity.layout.playerPlay.setBackgroundResource(R.drawable.ic_pause);
        }

        final Handler handler = new Handler();
        final int delay = 100;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.layout.playerTime.setText(createTime(SongListPresenterDataBinding.mediaPlayer.getCurrentPosition()));
                activity.layout.playerSeekbar.setProgress(SongListPresenterDataBinding.mediaPlayer.getCurrentPosition());
                if(SongListPresenterDataBinding.mediaPlayer.getCurrentPosition() >= SongListPresenterDataBinding.mediaPlayer.getDuration()-500){
                    nextPlayer();
                }
                if(!stopHandler){
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    public void jumpToTime(){
        SongListPresenterDataBinding.mediaPlayer.seekTo(activity.layout.playerSeekbar.getProgress());
    }

    public void rewindPlayer(){
        SongListPresenterDataBinding.mediaPlayer.seekTo(SongListPresenterDataBinding.mediaPlayer.getCurrentPosition()-10000);
    }

    public void prevPlayer(){
        int position = SongListPresenterDataBinding.selectedPosition;
        position = ((position-1)<0)?(songs.size()-1):(position-1);
        SongListPresenterDataBinding.selectedPosition = position;
        SongListPresenterDataBinding.selectedName = songs.get(position).getTitle();

        SongListPresenterDataBinding.mediaPlayer.stop();
        SongListPresenterDataBinding.mediaPlayer.release();
        Uri uri = Uri.parse(songs.get(position).getPath());
        SongListPresenterDataBinding.mediaPlayer = MediaPlayer.create(activity.getApplicationContext(), uri);
        SongListPresenterDataBinding.mediaPlayer.start();

        insertRecentSong(songs.get(position).getTitle(), songs.get(position).getPath());

        updateLayout();
        previousAnimation();
    }

    public void previousAnimation(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(activity.layout.playerImg, "rotation", 360f, 0f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public void playPlayer(){
        if(SongListPresenterDataBinding.mediaPlayer.isPlaying()){
            SongListPresenterDataBinding.mediaPlayer.pause();
            activity.layout.playerPlay.setBackgroundResource(R.drawable.ic_pause);
        } else {
            SongListPresenterDataBinding.mediaPlayer.start();
            activity.layout.playerPlay.setBackgroundResource(R.drawable.ic_play);
        }
    }

    public void nextPlayer(){
        int position = SongListPresenterDataBinding.selectedPosition;
        position = (position+1)%songs.size();
        SongListPresenterDataBinding.selectedPosition = position;
        SongListPresenterDataBinding.selectedName = songs.get(position).getTitle();

        SongListPresenterDataBinding.mediaPlayer.stop();
        SongListPresenterDataBinding.mediaPlayer.release();
        Uri uri = Uri.parse(songs.get(position).getPath());
        SongListPresenterDataBinding.mediaPlayer = MediaPlayer.create(activity.getApplicationContext(), uri);
        SongListPresenterDataBinding.mediaPlayer.start();

        insertRecentSong(songs.get(position).getTitle(), songs.get(position).getPath());

        updateLayout();
        nextAnimation();
    }
    public void nextAnimation(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(activity.layout.playerImg, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public void forwardPlayer(){
        SongListPresenterDataBinding.mediaPlayer.seekTo(SongListPresenterDataBinding.mediaPlayer.getCurrentPosition()+10000);
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

    public void insertRecentSong(String name, String path){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity);
        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
        dbHelper.insert(dbwrite, MusicAppDBContract.recentSongsTable.TABLE_NAME, name, path);
    }

    public void stopHandler(){
        stopHandler = true;
    }

}