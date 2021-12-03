package com.example.mobile2021_02_grupo03.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.presenter.SongListPresenter;
import com.example.mobile2021_02_grupo03.presenter.SongPlayerPresenter;

import java.util.ArrayList;

public class SongPlayerActivity extends AppCompatActivity {

    private Button btnPlay, btnNext, btnPrev, btnForward, btnRewind;
    private TextView txtsname, txtsstart, txtsstop;
    private SeekBar seekmusic;
    private ImageView imageView;
    private String sname;
    private Toolbar toolbar;
    private SongPlayerPresenter songPlayerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        toolbar = findViewById(R.id.toolbar);
        txtsname = findViewById(R.id.txtsn);
        imageView = findViewById(R.id.imageview);
        txtsstop = findViewById(R.id.txtsstop);
        seekmusic = findViewById(R.id.seekbar);
        txtsstart = findViewById(R.id.txtsstart);
        btnRewind = findViewById(R.id.btnrewind);
        btnPrev = findViewById(R.id.btnprevious);
        btnPlay = findViewById(R.id.playbtn);
        btnNext = findViewById(R.id.btnnext);
        btnForward = findViewById(R.id.btnforward);

        songPlayerPresenter = new SongPlayerPresenter(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtsname.setSelected(true);

        songPlayerPresenter.updateLayout(true);

        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SongListPresenter.mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        final Handler handler = new Handler();
        final int delay = 100;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = songPlayerPresenter.createTime(SongListPresenter.mediaPlayer.getCurrentPosition());
                txtsstart.setText(currentTime);
                int currentPosition = SongListPresenter.mediaPlayer.getCurrentPosition();
                seekmusic.setProgress(currentPosition);
                if(SongListPresenter.mediaPlayer.getCurrentPosition() >= SongListPresenter.mediaPlayer.getDuration()-500){
                    btnNext.performClick();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongListPresenter.mediaPlayer.seekTo(SongListPresenter.mediaPlayer.getCurrentPosition()-10000);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseButtonResource();
                songPlayerPresenter.previousMusic();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songPlayerPresenter.playMusic();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseButtonResource();
                songPlayerPresenter.nextMusic();
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongListPresenter.mediaPlayer.seekTo(SongListPresenter.mediaPlayer.getCurrentPosition()+10000);
            }
        });
    }

    public void updateLayout(Song song, MediaPlayer mediaPlayer, String endTime){
        sname = song.getTitle();
        txtsname.setText(sname);
        txtsstop.setText(endTime);
        seekmusic.setMax(mediaPlayer.getDuration());
    }

    public void pauseButtonResource(){
        btnPlay.setBackgroundResource(R.drawable.ic_pause);
    }

    public void playButtonResource(){
        btnPlay.setBackgroundResource(R.drawable.ic_play);
    }

    public void nextAnimation(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public void previousAnimation(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 360f, 0f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}