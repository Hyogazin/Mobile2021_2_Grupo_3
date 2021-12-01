package com.example.mobile2021_02_grupo03.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.services.NotificationActionService;

import java.io.File;
import java.util.ArrayList;

public class CreateNotification {

    public static final String CHANNEL_ID = "channel";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_NEXT = "action_next";

    public static void createNotification(Context context, ArrayList<Song> mySongs, int position, int playbutton){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            String sname = mySongs.get(position).getTitle();
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            //Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.musicicon);

            Intent intentPrevious = new Intent(context, NotificationActionService.class);
            intentPrevious.setAction(ACTION_PREVIOUS);
            PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, 0 , intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intentPlay = new Intent(context, NotificationActionService.class);
            intentPlay.setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0 , intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intentNext = new Intent(context, NotificationActionService.class);
            intentNext.setAction(ACTION_NEXT);
            PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 0 , intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("mySongs", mySongs).putExtra("position", position);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);

            int notificationColor = Color.rgb(53,32, 41);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music)
                    //.setLargeIcon(icon)
                    .setColor(notificationColor)
                    // Add media control buttons that invoke intents in your media service
                    .addAction(R.drawable.ic_previous, "Previous", pendingIntentPrevious)
                    .addAction(playbutton, "Pause", pendingIntentPlay)
                    .addAction(R.drawable.ic_next, "Next", pendingIntentNext)
                    // Apply the media style template
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                    .setShowActionsInCompactView(0,1,2)
                                    .setMediaSession(mediaSessionCompat.getSessionToken())
                    )
                    .setContentTitle(sname)
                    .setContentText("Playlist")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setShowWhen(false)
                    .setAutoCancel(false);

            Notification notification = builder.build();
            if(!(playbutton == R.drawable.ic_play)){
                notification.flags = NotificationCompat.FLAG_NO_CLEAR;
            }

            notificationManagerCompat.notify(1, notification);
        }
    }
}
