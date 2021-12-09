package com.example.mobile2021_02_grupo03.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;

import java.io.File;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button botao = findViewById(R.id.button);
        botao.setOnClickListener(new View.OnClickListener() {
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
                    String name = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "").replace("'", "");
                    String path = mySongs.get(i).toString();
                    dbHelper.insert(dbwrite, MusicAppDBContract.songsTable.TABLE_NAME, name, path);
                }


                Intent intent = new Intent(getApplicationContext(), SongListActivity.class);
                startActivity(intent);
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
}