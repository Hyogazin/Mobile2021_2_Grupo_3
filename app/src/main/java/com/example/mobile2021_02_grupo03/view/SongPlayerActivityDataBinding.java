package com.example.mobile2021_02_grupo03.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.databinding.ActivitySongPlayerDatabindingBinding;
import com.example.mobile2021_02_grupo03.presenter.SongPlayerPresenterDataBinding;

public class SongPlayerActivityDataBinding extends AppCompatActivity {

    private SongPlayerPresenterDataBinding presenter;
    public ActivitySongPlayerDatabindingBinding layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = DataBindingUtil.setContentView(this, R.layout.activity_song_player_databinding);

        presenter = new SongPlayerPresenterDataBinding(this);

        layout.playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.jumpToTime();
            }
        });

        layout.playerRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.rewindPlayer();
            }
        });

        layout.playerPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.prevPlayer();
            }
        });

        layout.playerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.playPlayer();
            }
        });

        layout.playerNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.nextPlayer();
            }
        });

        layout.playerForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.forwardPlayer();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            presenter.stopHandler();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stopHandler();
    }
}