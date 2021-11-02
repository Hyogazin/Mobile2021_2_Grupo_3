package com.example.mobile2021_02_grupo03.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
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

        /*mediaPlayer = MediaPlayer.create(this, R.raw.music);

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
        });*/

    }

    public void takePermissions(View view){
        if(isPermissionGranted()){
            Toast.makeText(this, "Permission Already Granted", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MusicListActivity.class);
            startActivity(intent);
        } else{
            takePermission();
        }
    }

    private boolean isPermissionGranted(){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            //For Android 11
            return Environment.isExternalStorageManager();
        } else{
            //For Below
            int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void takePermission(){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 100);
            } catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 100);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 100){
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
                    if(Environment.isExternalStorageManager()){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            if(requestCode == 101){
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(readExternalStorage){
                    Toast.makeText(this,"Read Permission is Granted in Android 10 or below", Toast.LENGTH_SHORT).show();
                }else{
                    takePermission();
                }
            }
        }
    }

}