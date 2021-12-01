package com.example.mobile2021_02_grupo03.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.adapter.MusicAdapter;
import com.example.mobile2021_02_grupo03.model.Song;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btnplay, btnnext, btnprevious, btnforward, btnrewind;
    TextView txtsname, txtsstart, txtsstop;
    SeekBar seekmusic;
    ImageView imageView;

    String sname;
    public static MediaPlayer mediaPlayer ;
    int position;
    ArrayList<Song> mySongs;

    private Toolbar toolbar;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        mySongs = bundle.getParcelableArrayList("mySongs");
        position = bundle.getInt("position", 0);
        txtsname.setSelected(true);

        updateLayout(true);
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

    public void updateLayout(boolean onCreate){
        if(!onCreate){
            if(mediaPlayer == null){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    createChannel();
                    registerReceiver(broadcastReceiver, new IntentFilter("SONGS"));
                }
            } else {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            Uri uri = Uri.parse(mySongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        sname = mySongs.get(position).getTitle();
        //CreateNotification.createNotification(PlayerActivity.this, mySongs, position, R.drawable.ic_pause);

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
        this.position = ((position-1)<0)?(mySongs.size()-1):(position-1);

        MusicListActivity.selectedName = mySongs.get(position).getTitle();
        MusicListActivity.selectedPosition = position;
        MusicListActivity.txtsname.setText(mySongs.get(position).getTitle());
        MusicListActivity.recyclerView.getAdapter().notifyDataSetChanged();

        updateLayout(false);

        btnplay.setBackgroundResource(R.drawable.ic_pause);
        MusicListActivity.btnplay.setBackgroundResource(R.drawable.ic_pause);

        //CreateNotification.createNotification(PlayerActivity.this, mySongs, position, R.drawable.ic_pause);
        previousAnimation(imageView);
    }

    public void playMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            btnplay.setBackgroundResource(R.drawable.ic_play);
            MusicListActivity.btnplay.setBackgroundResource(R.drawable.ic_play);
            //CreateNotification.createNotification(PlayerActivity.this, mySongs, position, R.drawable.ic_play);
        } else {
            btnplay.setBackgroundResource(R.drawable.ic_pause);
            MusicListActivity.btnplay.setBackgroundResource(R.drawable.ic_pause);
            //CreateNotification.createNotification(PlayerActivity.this, mySongs, position, R.drawable.ic_pause);
            mediaPlayer.start();
        }
    }

    public void nextMusic(){
        this.position = ((position+1)%mySongs.size());

        MusicListActivity.selectedName = mySongs.get(position).getTitle();
        MusicListActivity.selectedPosition = position;
        MusicListActivity.txtsname.setText(mySongs.get(position).getTitle());
        MusicListActivity.recyclerView.getAdapter().notifyDataSetChanged();

        updateLayout(false);

        btnplay.setBackgroundResource(R.drawable.ic_pause);
        MusicListActivity.btnplay.setBackgroundResource(R.drawable.ic_pause);

        //CreateNotification.createNotification(PlayerActivity.this, mySongs, position, R.drawable.ic_pause);
        nextAnimation(imageView);
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