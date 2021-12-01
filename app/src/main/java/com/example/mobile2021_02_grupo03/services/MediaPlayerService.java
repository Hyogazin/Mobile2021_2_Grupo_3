package com.example.mobile2021_02_grupo03.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import androidx.annotation.Nullable;
import com.example.mobile2021_02_grupo03.model.Song;
import java.util.ArrayList;

public class MediaPlayerService extends Service {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private MediaPlayer mediaPlayer;
    public static String name;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<Song> songs = intent.getExtras().getParcelableArrayList("mySongs");
        int position = intent.getExtras().getInt("position");
        name = songs.get(position).getTitle();
        /*if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }*/
        Uri uri = Uri.parse(songs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) mediaPlayer.release();
    }
}
