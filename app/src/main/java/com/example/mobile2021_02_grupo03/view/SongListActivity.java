package com.example.mobile2021_02_grupo03.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.databinding.ActivitySongListBinding;
import com.example.mobile2021_02_grupo03.model.SongData;
import com.example.mobile2021_02_grupo03.presenter.SongListPresenter;
import com.google.android.material.navigation.NavigationView;

public class SongListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public ActivitySongListBinding layout;
    public SongListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = ActivitySongListBinding.inflate(getLayoutInflater());
        setContentView(layout.getRoot());

        presenter = new SongListPresenter(this);

        layout.listLayoutBtnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onPrevClick();
            }
        });

        layout.listLayoutPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.playMiniPlayer();
            }
        });

        layout.listLayoutBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onNextClick();
            }
        });

        layout.listLayoutPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onPlayerClick();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResumeLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                presenter.songsAdapter.getFilter().filter(s);
                return false;
            }
        });
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                presenter.closeMiniPlayer();
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.openMiniPlayer();
                    }
                }, 200);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_item_all_musics: {
                presenter.getAllSongsFromSQLite();
                break;
            }
            case R.id.nav_item_recent_musics: {
                presenter.getRecentSongsFromSQLite();
                break;
            }
            case R.id.nav_item_streaming_musics: {
                presenter.getStreamingSongs();
                break;
            }
            default: {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            }
        }
        layout.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}