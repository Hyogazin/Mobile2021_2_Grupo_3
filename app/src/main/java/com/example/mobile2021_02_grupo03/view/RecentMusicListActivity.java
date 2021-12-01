package com.example.mobile2021_02_grupo03.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
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

public class RecentMusicListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    long[] musicIds;
    String[] musicNames;
    String[] musicPaths;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_music_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.musicListId);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        MusicAppDBHelper dbHelper = new MusicAppDBHelper(getApplicationContext());

        displaySongs(dbHelper);

        customAdapter customAdapter = new customAdapter();

        listView = findViewById(R.id.recyclerView);
        listView.setAdapter(customAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("songNames", musicNames).putExtra("songPaths", musicPaths).putExtra("pos", i));

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.insert(db, MusicAppDBContract.recentSongsTable.TABLE_NAME, musicNames[i], musicPaths[i]);

                displaySongs(dbHelper);

                listView.invalidateViews();
            }
        });
    }
    void displaySongs(MusicAppDBHelper dbHelper){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] colunas = {
                BaseColumns._ID,
                MusicAppDBContract.songsTable.COLUMN_NAME_NAME,
                MusicAppDBContract.songsTable.COLUMN_NAME_PATH
        };

        Cursor cursor = dbHelper.select(db, MusicAppDBContract.recentSongsTable.TABLE_NAME, colunas);

        musicIds = new long[cursor.getCount()];
        musicNames = new String[cursor.getCount()];
        musicPaths = new String[cursor.getCount()];

        cursor.moveToLast();
        for(int i = 0; i<cursor.getCount(); i++){
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable._ID));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable.COLUMN_NAME_NAME));
            String itemPath = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable.COLUMN_NAME_PATH));
            musicIds[i] = itemId;
            musicNames[i] = itemName;
            musicPaths[i] = itemPath;
            cursor.moveToPrevious();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    class customAdapter extends BaseAdapter {

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
            TextView textsong = myView.findViewById(R.id.txtSongName);
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