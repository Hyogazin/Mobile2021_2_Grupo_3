package com.example.mobile2021_02_grupo03.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.services.NotificationActionService;

import java.io.File;
import java.util.ArrayList;

public class CreateNotification {

    public static final String CHANNEL_ID = "channell";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static Notification notification;

    public static void createNotification(Context context, ArrayList<File> mySongs, int position){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            String sname = mySongs.get(position).getName().replace(".mp3", "").replace(".wav", "");
            //MediaSessionCompat

            //Bitmap icon = BitmapFactory.decodeResource(context.getResources(), track.getImage());
            int drw_previous;
            Intent intentPrevious = new Intent(context, NotificationActionService.class).setAction(ACTION_PREVIOUS);
            PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, 0 , intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
            drw_previous = R.drawable.ic_previous;

            int drw_play;
            Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0 , intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
            drw_play = R.drawable.ic_play;


            int drw_next;
            Intent intentNext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
            PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 0 , intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
            drw_next = R.drawable.ic_next;



            //create notification
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_play)
                    .setContentTitle("App Mobile")
                    .setContentText(sname)
                    //.setLargeIcon()
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(drw_play, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    //.setStyle(new
                     //   .setShowActionInCompactView(0,1,2)
                      //  .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            notificationManagerCompat.notify(1, notification);
        }
    }
}
