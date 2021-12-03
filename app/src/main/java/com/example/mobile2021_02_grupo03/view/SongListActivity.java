package com.example.mobile2021_02_grupo03.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.adapter.SongsAdapter;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.presenter.SongListPresenter;
import com.google.android.material.navigation.NavigationView;

public class SongListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SongListPresenter songListPresenter;
    public static RecyclerView recyclerView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout listLayoutPlayer;
    public static TextView txtsname;
    private Button btnPrev;
    public static Button btnPlay;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.musicListId);
        navigationView = findViewById(R.id.navView);
        recyclerView = findViewById(R.id.recyclerView);
        listLayoutPlayer = findViewById(R.id.listLayoutPlayer);
        txtsname = findViewById(R.id.listLayoutSongName);
        btnPrev = findViewById(R.id.listLayoutBtnPrevious);
        btnPlay = findViewById(R.id.listLayoutPlayBtn);
        btnNext = findViewById(R.id.listLayoutBtnNext);

        songListPresenter = new SongListPresenter(this);

        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (songListPresenter.mediaPlayer == null){
            listLayoutPlayer.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,0,10);
            recyclerView.setLayoutParams(param);
        } else{
            listLayoutPlayer.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,0,8);
            recyclerView.setLayoutParams(param);
            updateLayout(songListPresenter.selectedName);
        }

        songListPresenter.getAllSongsFromSQLite();

        listLayoutPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listLayoutPlayer.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,0,8);
                recyclerView.setLayoutParams(param);
                recyclerView.scrollToPosition(songListPresenter.selectedPosition);
                songListPresenter.onPlayerClick();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseButtonResource();
                int position = songListPresenter.mediaPlayerPrevPosition();
                recyclerView.scrollToPosition(position);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        recyclerView.findViewHolderForAdapterPosition(position).itemView.performClick();
                    }
                }, 100);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(songListPresenter.mediaPlayer.isPlaying()){
                    playButtonResource();
                    songListPresenter.mediaPlayer.pause();
                } else {
                    pauseButtonResource();
                    songListPresenter.mediaPlayer.start();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseButtonResource();
                int position = songListPresenter.mediaPlayerNextPosition();
                recyclerView.scrollToPosition(position);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        recyclerView.findViewHolderForAdapterPosition(position).itemView.performClick();
                    }
                }, 100);

            }
        });
    }

    public void displaySongs(SongsAdapter adapter){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new SongsAdapter.MusicAdapterListener() {
            @Override
            public void onItemClick(int position) {
                listLayoutPlayer.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,0,8);
                recyclerView.setLayoutParams(param);
                songListPresenter.onItemClick(position);
                recyclerView.scrollToPosition(songListPresenter.selectedPosition);
            }
        });
    }

    public void updateLayout(String name){
        txtsname.setText(name);
        txtsname.setSelected(true);
    }

    public void pauseButtonResource(){
        btnPlay.setBackgroundResource(R.drawable.ic_pause);
    }

    public void playButtonResource(){
        btnPlay.setBackgroundResource(R.drawable.ic_play);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_item_all_musics: {
                songListPresenter.getAllSongsFromSQLite();
                break;
            }
            case R.id.nav_item_recent_musics: {
                songListPresenter.getRecentSongsFromSQLite();
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
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}