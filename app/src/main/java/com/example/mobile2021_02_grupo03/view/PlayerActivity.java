package com.example.mobile2021_02_grupo03.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.media.AudioManager;
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

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.services.OnClearFromRecentService;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btnplay, btnnext, btnprevious, btnforward, btnrewind;
    TextView txtsname, txtsstart, txtsstop;
    SeekBar seekmusic;
    ImageView imageView;

    String sname;
    static MediaPlayer mediaPlayer;
    int position;

    //ArrayList<File> mySongs;
    String[] songPaths;
    String[] songNames;

    static NotificationManager notificationManager;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action_name");

            switch (action){
                case CreateNotification.ACTION_PREVIOUS:
                    previousMusic();
                    break;
                case CreateNotification.ACTION_PLAY:
                    playMusic();
                    break;
                case CreateNotification.ACTION_NEXT:
                    nextMusic();
                    break;
            }
        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        /*
        //texto barra superior
        getSupportActionBar().setTitle("Now Playing");
        //botao voltar barra superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        btnplay = findViewById(R.id.playbtn);
        btnnext = findViewById(R.id.btnnext);
        btnprevious = findViewById(R.id.btnprevious);
        btnforward = findViewById(R.id.btnforward);
        btnrewind = findViewById(R.id.btnrewind);
        txtsname = findViewById(R.id.txtsn);
        txtsstart = findViewById(R.id.txtsstart);
        txtsstop = findViewById(R.id.txtsstop);
        seekmusic = findViewById(R.id.seekbar);
        imageView = findViewById(R.id.imageview);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

//        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
//        position = bundle.getInt("pos", 0);
//        isNotificationClick = bundle.getInt("isNotificationClick", 0);
//        txtsname.setSelected(true);
//        Uri uri = Uri.parse(mySongs.get(position).toString());

        songPaths = bundle.getStringArray("songPaths");
        songNames = bundle.getStringArray("songNames");
        position = bundle.getInt("pos", 0);
        txtsname.setSelected(true);


       updateLayout();

       if(!mediaPlayer.isPlaying()){
           btnplay.setBackgroundResource(R.drawable.ic_play);
       }



        final Handler handler = new Handler();
        final int delay = 100;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtsstart.setText(currentTime);
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekmusic.setProgress(currentPosition);
                if(mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()-500){
                    btnnext.performClick();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.MULTIPLY);
        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMusic();
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMusic();
            }
        });

        btnprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMusic();
            }
        });

        btnforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        btnrewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });
    }

    public void updateLayout(){
        if(mediaPlayer == null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                createChannel();
                //CreateNotification.createNotification(PlayerActivity.this, mySongs, position, R.drawable.ic_pause);
                registerReceiver(broadcastReceiver, new IntentFilter("SONGS"));
                startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
            }
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Uri uri = Uri.parse(songPaths[position]);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        CreateNotification.createNotification(PlayerActivity.this, songNames, songPaths, position, R.drawable.ic_pause);
        sname = songNames[position];
        txtsname.setText(sname);
        String endTime = createTime(mediaPlayer.getDuration());
        txtsstop.setText(endTime);
        seekmusic.setMax(mediaPlayer.getDuration());
    }

    public void createChannel(){
        CharSequence name = "channel_name";
        String description = "channel_description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, name, importance);
        channel.setDescription(description);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void previousMusic(){
        this.position = ((position-1)<0)?(songNames.length-1):(position-1);
        updateLayout();
        btnplay.setBackgroundResource(R.drawable.ic_pause);
        CreateNotification.createNotification(PlayerActivity.this, songNames, songPaths, position, R.drawable.ic_pause);
        previousAnimation(imageView);

//        mediaPlayer.stop();
//        mediaPlayer.release();
//        this.position = ((position-1)<0)?(songNames.length-1):(position-1);
//        Uri u = Uri.parse(songPaths[position]);
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
//        String endTime = createTime(mediaPlayer.getDuration());
//        txtsstop.setText(endTime);
//        seekmusic.setMax(mediaPlayer.getDuration());
//        sname = songNames[position];
//        txtsname.setText(sname);
//        mediaPlayer.start();
//        btnplay.setBackgroundResource(R.drawable.ic_pause);
//        CreateNotification.createNotification(PlayerActivity.this, songNames, songPaths, position, R.drawable.ic_pause);
//        previousAnimation(imageView);
    }

    public void playMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            btnplay.setBackgroundResource(R.drawable.ic_play);
            CreateNotification.createNotification(PlayerActivity.this, songNames, songPaths, position, R.drawable.ic_play);
        } else {
            btnplay.setBackgroundResource(R.drawable.ic_pause);
            CreateNotification.createNotification(PlayerActivity.this, songNames, songPaths, position, R.drawable.ic_pause);
            mediaPlayer.start();
        }
    }

    public void nextMusic(){
        this.position = ((position+1)%songNames.length);
        updateLayout();
        btnplay.setBackgroundResource(R.drawable.ic_pause);
        CreateNotification.createNotification(PlayerActivity.this, songNames, songPaths, position, R.drawable.ic_pause);
        nextAnimation(imageView);

//        mediaPlayer.stop();
//        mediaPlayer.release();
//        this.position = ((position+1)%songNames.length);
//        Uri u = Uri.parse(songPaths[position]);
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
//        sname = songNames[position];
//        //createNotification();
//        txtsname.setText(sname);
//        String endTime = createTime(mediaPlayer.getDuration());
//        txtsstop.setText(endTime);
//        seekmusic.setMax(mediaPlayer.getDuration());
//        mediaPlayer.start();
//        btnplay.setBackgroundResource(R.drawable.ic_pause);
//        CreateNotification.createNotification(PlayerActivity.this, songNames, songPaths, position, R.drawable.ic_pause);
//        nextAnimation(imageView);
    }

    public void nextAnimation(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public void previousAnimation(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 360f, 0f);
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
}