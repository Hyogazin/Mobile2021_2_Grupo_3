package com.example.mobile2021_02_grupo03.view;

import androidx.annotation.NonNull;
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
import android.provider.BaseColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    ListView listView;
    long[] musicIds;
    String[] musicNames;
    String[] musicPaths;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.musicListId);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();


        MusicAppDBHelper dbHelper = new MusicAppDBHelper(getApplicationContext());

        listView = findViewById(R.id.listViewSong);
        displaySongs(dbHelper);
    }

    void displaySongs(MusicAppDBHelper dbHelper){
        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();

        String[] colunas = {
                BaseColumns._ID,
                MusicAppDBContract.songsTable.COLUMN_NAME_NAME,
                MusicAppDBContract.songsTable.COLUMN_NAME_PATH
        };

        Cursor cursor = dbHelper.select(dbwrite, MusicAppDBContract.songsTable.TABLE_NAME, colunas);

        musicIds = new long[cursor.getCount()];
        musicNames = new String[cursor.getCount()];
        musicPaths = new String[cursor.getCount()];

        cursor.moveToFirst();
        for(int i = 0; i<cursor.getCount(); i++){
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable._ID));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable.COLUMN_NAME_NAME));
            String itemPath = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable.COLUMN_NAME_PATH));
            musicIds[i] = itemId;
            musicNames[i] = itemName;
            musicPaths[i] = itemPath;

            cursor.moveToNext();
        }

        cursor = dbHelper.select(dbwrite, MusicAppDBContract.recentSongsTable.TABLE_NAME, colunas);
        cursor.moveToFirst();
        for(int i = 0; i<cursor.getCount(); i++){
            int name = cursor.getColumnIndex(MusicAppDBContract.recentSongsTable.COLUMN_NAME_NAME);
            int path = cursor.getColumnIndex(MusicAppDBContract.recentSongsTable.COLUMN_NAME_PATH);
            Log.i("RECENT:", "#################");
            Log.i("RECENT:", cursor.getString(name));
            Log.i("RECENT:", cursor.getString(path));
            Log.i("RECENT:", "#################");

            cursor.moveToNext();
        }

        /*
        cursor = bancoDados.rawQuery("SELECT DISTINCT nome, path FROM recentSongs", null);
        indiceNome = cursor.getColumnIndex("nome");
        indicepath = cursor.getColumnIndex("path");

        cursor.moveToFirst();
        for(int i = 0; i<cursor.getCount(); i++){
            Log.i("RESULTADO:", cursor.getString(indiceNome));
            Log.i("RESULTADO:", cursor.getString(indicepath));
            cursor.moveToNext();
        }*/

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("songNames", musicNames).putExtra("songPaths", musicPaths).putExtra("pos", i));
                try {
                    dbHelper.insert(dbwrite, MusicAppDBContract.recentSongsTable.TABLE_NAME, musicNames[i], musicPaths[i]);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
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