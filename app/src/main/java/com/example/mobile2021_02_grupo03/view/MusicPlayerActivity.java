package com.example.mobile2021_02_grupo03.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Button playButton = findViewById(R.id.button2);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //PASSAR ESSE CODIGO PRA DPS DO LOGIN
                MusicAppDBHelper dbHelper = new MusicAppDBHelper(getApplicationContext());
                SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
                SQLiteDatabase dbread = dbHelper.getReadableDatabase();

                dbHelper.dropTables(dbwrite);
                dbHelper.createTables(dbwrite);

                ArrayList<File> mySongs = getSongsFromStorage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                for (int i = 0; i<mySongs.size(); i++) {
                    String name = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
                    String path = mySongs.get(i).toString();
                    dbHelper.insert(dbwrite, MusicAppDBContract.songsTable.TABLE_NAME, name, path);
                }

                /*String url = "https://www.dropbox.com/s/9fop0kpaznprd77/Novo%20Tom%20%26%20Leonardo%20Gon%C3%A7alves%20-%20Brilhar%20por%20Ti.mp3?dl=0#"; // your URL here
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes
                                .Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build());
                try {
                    mediaPlayer.setDataSource(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();*/
            }
        });

        Button resetButton = findViewById(R.id.button3);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePermissions(view);
            }
        });
    }

    public ArrayList<File> getSongsFromStorage (File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile: files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.addAll(getSongsFromStorage(singleFile));
            } else{
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }

    public void takePermissions(View view){
        if(isPermissionGranted()){
            Toast.makeText(this, "Permission Already Granted", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), RecentMusicListActivity.class);
            startActivity(intent);
        } else{
            takePermission();
        }
    }

    private boolean isPermissionGranted(){
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        //int internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        return (readExternalStoragePermission == PackageManager.PERMISSION_GRANTED) &&
                (recordAudioPermission == PackageManager.PERMISSION_GRANTED);
                //&& (internetPermission == PackageManager.PERMISSION_GRANTED);
    }

    private void takePermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO/*, Manifest.permission.INTERNET*/}, 101);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            if(requestCode == 101){
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean recordAudio = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                //boolean internet = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                if(readExternalStorage && recordAudio /*&& internet*/){
                    takePermissions(getCurrentFocus());
                }else{
                    takePermission();
                }
            }
        }
    }

}