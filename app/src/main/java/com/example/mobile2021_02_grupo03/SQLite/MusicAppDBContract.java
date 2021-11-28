package com.example.mobile2021_02_grupo03.SQLite;

import android.provider.BaseColumns;

public final class MusicAppDBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private MusicAppDBContract() {

    }
    /* Inner class that defines the table contents */
    public static class songsTable implements BaseColumns {
        public static final String TABLE_NAME = "songs";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PATH = "path";
    }

    public static class recentSongsTable implements BaseColumns {
        public static final String TABLE_NAME = "recentSongs";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PATH = "path";
    }
}
