package com.example.mobile2021_02_grupo03.view;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobile2021_02_grupo03.R;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity {
    ListView listView;
    String[] items;
    String[] musicNames;
    String[] musicPaths;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();



        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase("musicApp", MODE_PRIVATE, null);

            listView = findViewById(R.id.listViewSong);
            displaySongs(bancoDados);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void displaySongs(SQLiteDatabase bancoDados){
        Cursor cursor = bancoDados.rawQuery("SELECT nome, path FROM songs", null);
        int indiceNome = cursor.getColumnIndex("nome");
        int indicepath = cursor.getColumnIndex("path");

        musicNames = new String[cursor.getCount()];
        musicPaths = new String[cursor.getCount()];
        cursor.moveToFirst();
        for(int i = 0; i<cursor.getCount(); i++){
            musicNames[i] = cursor.getString(indiceNome);
            musicPaths[i] = cursor.getString(indicepath);
            cursor.moveToNext();
        }

        cursor = bancoDados.rawQuery("SELECT DISTINCT nome, path FROM recentSongs", null);
        indiceNome = cursor.getColumnIndex("nome");
        indicepath = cursor.getColumnIndex("path");

        cursor.moveToFirst();
        for(int i = 0; i<cursor.getCount(); i++){
            Log.i("RESULTADO:", cursor.getString(indiceNome));
            Log.i("RESULTADO:", cursor.getString(indicepath));
            cursor.moveToNext();
        }

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("songNames", musicNames).putExtra("songPaths", musicPaths).putExtra("pos", i));
                try {
                    bancoDados.execSQL("INSERT INTO recentSongs(nome,path) VALUES('" + musicNames[i] + "', '" + musicPaths[i] + "')");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return musicNames.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(musicNames[i]);
            return myView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            try {
                PlayerActivity.notificationManager.cancelAll();
            }catch (Exception e){
                e.printStackTrace();
            }



        }
    }
}