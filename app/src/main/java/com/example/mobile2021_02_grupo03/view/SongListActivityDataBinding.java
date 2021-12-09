package com.example.mobile2021_02_grupo03.view;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.databinding.ActivitySongListDatabindingBinding;
import com.example.mobile2021_02_grupo03.presenter.SongListPresenterDataBinding;
import com.google.android.material.navigation.NavigationView;

public class SongListActivityDataBinding extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public ActivitySongListDatabindingBinding layout;
    public SongListPresenterDataBinding presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = ActivitySongListDatabindingBinding.inflate(getLayoutInflater());
        setContentView(layout.getRoot());

        presenter = new SongListPresenterDataBinding(this);

        layout.listLayoutBtnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.prevMiniPlayer();
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
                presenter.nextMiniPlayer();
            }
        });

        layout.listLayoutPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onItemClick(presenter.selectedPosition);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getAllSongsFromSQLite();
        presenter.openMiniPlayer();
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
            default: {
                onBackPressed();
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