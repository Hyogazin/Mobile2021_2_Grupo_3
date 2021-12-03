package com.example.mobile2021_02_grupo03.presenter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.view.CreateNotification;
import com.example.mobile2021_02_grupo03.view.MusicListActivity;
import com.example.mobile2021_02_grupo03.view.SongListActivity;
import com.example.mobile2021_02_grupo03.view.SongPlayerActivity;

import java.util.ArrayList;

public class SongPlayerPresenter {

    private SongPlayerActivity songPlayerActivity;
    private NotificationManager notificationManager;

    public SongPlayerPresenter(SongPlayerActivity songPlayerActivity) {
        this.songPlayerActivity = songPlayerActivity;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action_name");

            switch (action){
                case CreateNotification.ACTION_PREVIOUS:
                    break;
                case CreateNotification.ACTION_PLAY:
                    break;
                case CreateNotification.ACTION_NEXT:
                    break;
            }
        }
    };

    public void updateLayout(boolean onCreate){
        if(!onCreate){
            if(SongListPresenter.mediaPlayer != null){
                SongListPresenter.mediaPlayer.stop();
                SongListPresenter.mediaPlayer.release();
            }
            Uri uri = Uri.parse(SongListPresenter.songs.get(SongListPresenter.selectedPosition).getPath());
            SongListPresenter.mediaPlayer = MediaPlayer.create(songPlayerActivity, uri);
            SongListPresenter.mediaPlayer.start();
        }
        String time = createTime(SongListPresenter.mediaPlayer.getDuration());
        songPlayerActivity.updateLayout(SongListPresenter.songs.get(SongListPresenter.selectedPosition), SongListPresenter.mediaPlayer, time);

        if(SongListPresenter.mediaPlayer.isPlaying()){
            songPlayerActivity.pauseButtonResource();
        } else{
            songPlayerActivity.playButtonResource();
        }
    }

    public void previousMusic(){
        int position = SongListPresenter.selectedPosition;
        position = ((position-1)<0)?(SongListPresenter.songs.size()-1):(position-1);
        ArrayList<Song> songs = SongListPresenter.songs;

        SongListPresenter.selectedPosition = position;
        SongListPresenter.selectedName = songs.get(position).getTitle();
        SongListPresenter.selectedPosition = position;

        updateLayout(false);
        songPlayerActivity.pauseButtonResource();
        songPlayerActivity.previousAnimation();
        SongListActivity.recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void playMusic(){
        if(SongListPresenter.mediaPlayer.isPlaying()){
            songPlayerActivity.playButtonResource();
            SongListPresenter.mediaPlayer.pause();
            SongListActivity.btnPlay.setBackgroundResource(R.drawable.ic_play);
        } else {
            songPlayerActivity.pauseButtonResource();
            SongListPresenter.mediaPlayer.start();
            SongListActivity.btnPlay.setBackgroundResource(R.drawable.ic_pause);
        }
    }

    public void nextMusic(){
        int position = SongListPresenter.selectedPosition;
        position = ((position+1)%SongListPresenter.songs.size());
        ArrayList<Song> songs = SongListPresenter.songs;

        SongListPresenter.selectedPosition = position;
        SongListPresenter.selectedName = songs.get(position).getTitle();
        SongListPresenter.selectedPosition = position;

        updateLayout(false);
        songPlayerActivity.pauseButtonResource();
        songPlayerActivity.nextAnimation();
        SongListActivity.recyclerView.getAdapter().notifyDataSetChanged();
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

}