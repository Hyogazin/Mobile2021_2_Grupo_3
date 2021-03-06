package com.example.mobile2021_02_grupo03.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;

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

            MusicAppDBHelper dbHelper = new MusicAppDBHelper(getApplicationContext());
            SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();

            dbHelper.dropTables(dbwrite);
            dbHelper.createTables(dbwrite);

            ArrayList<File> mySongs = getSongsFromStorage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            for (int i = 0; i<mySongs.size(); i++) {
                String name = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "").replace("'", "");
                String path = mySongs.get(i).toString();
                dbHelper.insert(dbwrite, MusicAppDBContract.songsTable.TABLE_NAME, name, path);
            }

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else{
            takePermission();
        }
    }

    private boolean isPermissionGranted(){
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return (readExternalStoragePermission == PackageManager.PERMISSION_GRANTED);
    }

    private void takePermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            if(requestCode == 101){
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(readExternalStorage){
                    takePermissions();
                }else{
                    takePermission();
                }
            }
        }
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



}