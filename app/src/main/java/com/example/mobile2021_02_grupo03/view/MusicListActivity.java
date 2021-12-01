package com.example.mobile2021_02_grupo03.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;
import com.example.mobile2021_02_grupo03.adapter.MusicAdapter;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.services.MediaPlayerService;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static RecyclerView recyclerView;

    public static String selectedName = "";
    public static int selectedPosition = -1;
    public static int selectedLayout = 1;
    static TextView txtsname;
    static Button btnplay;
    ArrayList<Song> songs = new ArrayList<>();
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        if (PlayerActivity.mediaPlayer != null){
            findViewById(R.id.listLayoutPlayer).setVisibility(View.VISIBLE);
        }

        btnplay = findViewById(R.id.listLayoutPlayBtn);
        Button btnnext = findViewById(R.id.listLayoutBtnNext);
        Button btnprevious = findViewById(R.id.listLayoutBtnPrevious);
        txtsname = findViewById(R.id.listLayoutSongName);
        findViewById(R.id.listLayoutPlayer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recyclerView.findViewHolderForAdapterPosition(selectedPosition).itemView.performClick();

                findViewById(R.id.listLayoutPlayer).setVisibility(View.VISIBLE);

                recyclerView.scrollToPosition(selectedPosition);
                txtsname.setText(songs.get(selectedPosition).getTitle());
                txtsname.setSelected(true);

                if(selectedName.equals(songs.get(selectedPosition).getTitle())){
                    startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("mySongs", songs).putExtra("position", selectedPosition));
                } else{
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    if(PlayerActivity.mediaPlayer != null){
                        PlayerActivity.mediaPlayer.stop();
                        PlayerActivity.mediaPlayer.release();
                    }
                    Uri uri = Uri.parse(songs.get(selectedPosition).getPath());
                    PlayerActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    PlayerActivity.mediaPlayer.start();
                }
                selectedName = songs.get(selectedPosition).getTitle();

                MusicAppDBHelper dbHelper = new MusicAppDBHelper(getApplicationContext());
                SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
                dbHelper.insert(dbwrite, MusicAppDBContract.recentSongsTable.TABLE_NAME, songs.get(selectedPosition).getTitle(), songs.get(selectedPosition).getPath());
                if(selectedLayout == 2){
                    selectedLayout = 1;
                    selectList(selectedLayout);
                } else{
                    selectedLayout = 0;
                    selectedPosition = selectedPosition;
                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.musicListId);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        MusicAppDBHelper dbHelper = new MusicAppDBHelper(getApplicationContext());

        selectedLayout = 1;
        selectList(selectedLayout);


        btnprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = ((selectedPosition-1)<0)?(songs.size()-1):(selectedPosition-1);
                btnplay.setBackgroundResource(R.drawable.ic_pause);
                recyclerView.findViewHolderForAdapterPosition(selectedPosition).itemView.performClick();
            }
        });

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PlayerActivity.mediaPlayer.isPlaying()){
                    PlayerActivity.mediaPlayer.pause();
                    btnplay.setBackgroundResource(R.drawable.ic_play);
                    //CreateNotification.createNotification(PlayerActivity.this, mySongs, position, R.drawable.ic_play);
                } else {
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    //CreateNotification.createNotification(PlayerActivity.this, mySongs, position, R.drawable.ic_pause);
                    PlayerActivity.mediaPlayer.start();
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnplay.setBackgroundResource(R.drawable.ic_pause);
                selectedPosition = ((selectedPosition+1)%songs.size());
                recyclerView.findViewHolderForAdapterPosition(selectedPosition).itemView.performClick();
            }
        });
    }

    void selectList(int option){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(getApplicationContext());
        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
        songs.clear();
        String[] colunas;
        Cursor cursor;
        switch (option){
            case 2:
                colunas = new String[]{
                        BaseColumns._ID,
                        MusicAppDBContract.recentSongsTable.COLUMN_NAME_NAME,
                        MusicAppDBContract.recentSongsTable.COLUMN_NAME_PATH
                };
                cursor = dbHelper.select(dbwrite, MusicAppDBContract.recentSongsTable.TABLE_NAME, colunas);
                cursor.moveToLast();
                for(int i = 0; i<cursor.getCount(); i++){
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MusicAppDBContract.recentSongsTable._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.recentSongsTable.COLUMN_NAME_NAME));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.recentSongsTable.COLUMN_NAME_PATH));
                    Song song = new Song(id, name, path);
                    songs.add(song);
                    if(selectedName.equals(name)){
                        selectedPosition = i;
                    }
                    cursor.moveToPrevious();
                    getSupportActionBar().setTitle("Músicas Recentes");
                }
                break;
            default:
                colunas = new String[]{
                        BaseColumns._ID,
                        MusicAppDBContract.songsTable.COLUMN_NAME_NAME,
                        MusicAppDBContract.songsTable.COLUMN_NAME_PATH
                };
                cursor = dbHelper.select(dbwrite, MusicAppDBContract.songsTable.TABLE_NAME, colunas, MusicAppDBContract.songsTable.COLUMN_NAME_NAME);
                cursor.moveToFirst();
                for(int i = 0; i<cursor.getCount(); i++){
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable.COLUMN_NAME_NAME));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MusicAppDBContract.songsTable.COLUMN_NAME_PATH));
                    Song song = new Song(id, name, path);
                    songs.add(song);
                    if(selectedName.equals(name)){
                        selectedPosition = i;
                    }
                    cursor.moveToNext();
                    getSupportActionBar().setTitle("Todas as Músicas");
                }
                break;
        }
        displaySongs();
    }

    void displaySongs(){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(getApplicationContext());
        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.recyclerView);
        MusicAdapter musicAdapter = new MusicAdapter(songs);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(musicAdapter);
        musicAdapter.setListener(new MusicAdapter.MusicAdapterListener() {
            @Override
            public void onItemClick(int position) {
                findViewById(R.id.listLayoutPlayer).setVisibility(View.VISIBLE);

                recyclerView.scrollToPosition(position);
                txtsname.setText(songs.get(position).getTitle());
                txtsname.setSelected(true);

                if(selectedName.equals(songs.get(position).getTitle())){
                    startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("mySongs", songs).putExtra("position", position));
                } else{
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    if(PlayerActivity.mediaPlayer != null){
                        PlayerActivity.mediaPlayer.stop();
                        PlayerActivity.mediaPlayer.release();
                    }
                    Uri uri = Uri.parse(songs.get(position).getPath());
                    PlayerActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    PlayerActivity.mediaPlayer.start();
                }
                selectedName = songs.get(position).getTitle();


                dbHelper.insert(dbwrite, MusicAppDBContract.recentSongsTable.TABLE_NAME, songs.get(position).getTitle(), songs.get(position).getPath());
                if(selectedLayout == 2){
                    selectedLayout = 1;
                    selectList(selectedLayout);
                } else{
                    selectedLayout = 0;
                    selectedPosition = position;
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_item_all_musics: {
                selectedLayout = 1;
                selectList(selectedLayout);
                break;
            }
            case R.id.nav_item_recent_musics: {
                selectedLayout = 2;
                selectList(selectedLayout);
                break;
            }
            default: {
                Toast.makeText(this, "Menu Default", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
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