package com.example.mobile2021_02_grupo03.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile2021_02_grupo03.R;

public class MusicPlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        mediaPlayer = MediaPlayer.create(this, R.raw.music);

        TextView textView = findViewById(R.id.textView3);
        String minutes = "" + mediaPlayer.getDuration()/1000/60;
        String seconds = "" + (mediaPlayer.getDuration()/1000)%60;
        if(minutes.length() == 1){
            minutes = "0" + minutes;
        }
        if(seconds.length() == 1){
            seconds = "0" + seconds;
        }
        textView.setText("" + minutes + ":" + seconds);
        Button playButton = findViewById(R.id.button2);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }else if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
            }
        });

        Button resetButton = findViewById(R.id.button3);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MusicListActivity.class);
                startActivity(intent);
            }
        });

    }

}