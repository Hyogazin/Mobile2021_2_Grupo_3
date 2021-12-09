package com.example.mobile2021_02_grupo03.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.mobile2021_02_grupo03.R;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    takePermissions();
                }
            }
        };
        timer.start();
    }

    public void takePermissions(){
        if(isPermissionGranted()){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else{
            takePermission();
        }
    }

    private boolean isPermissionGranted(){
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        //int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        //int internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        return (readExternalStoragePermission == PackageManager.PERMISSION_GRANTED);
                // &&(recordAudioPermission == PackageManager.PERMISSION_GRANTED);
        //&& (internetPermission == PackageManager.PERMISSION_GRANTED);
    }

    private void takePermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE/*, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET*/}, 101);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            if(requestCode == 101){
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                //boolean recordAudio = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                //boolean internet = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                if(readExternalStorage /*&& recordAudio*/ /*&& internet*/){
                    takePermissions();
                }else{
                    takePermission();
                }
            }
        }
    }
}