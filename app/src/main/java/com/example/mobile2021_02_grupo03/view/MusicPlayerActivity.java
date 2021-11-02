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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Button resetButton = findViewById(R.id.button3);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePermissions(view);
            }
        });
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
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return (readExternalStoragePermission == PackageManager.PERMISSION_GRANTED) && (recordAudioPermission == PackageManager.PERMISSION_GRANTED);
    }

    private void takePermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 101);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            if(requestCode == 101){
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean recordAudio = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if(readExternalStorage && recordAudio){
                    takePermissions(getCurrentFocus());
                }else{
                    takePermission();
                }
            }
        }
    }

}