package com.viatorfortis.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.viatorfortis.popularmovies.db.MovieContract.*;

class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITEMOVIES_TABLE = "CREATE TABLE " +
                FavouriteMoviesEntry.TABLE_NAME + " (" +
                FavouriteMoviesEntry._ID + " INTEGER PRIMARY KEY, " +
                FavouriteMoviesEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                FavouriteMoviesEntry.COLUMN_NAME_RELEASEDATE + " TIMESTAMP, " +
                FavouriteMoviesEntry.COLUMN_NAME_POSTERPATH + " TEXT, " +
                FavouriteMoviesEntry.COLUMN_NAME_VOTEAVERAGE + " REAL, " +
                FavouriteMoviesEntry.COLUMN_NAME_PLOTSYNOPSIS + " TEXT" +
                "); ";

        db.execSQL(SQL_CREATE_FAVOURITEMOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
