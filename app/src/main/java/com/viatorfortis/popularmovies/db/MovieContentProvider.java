package com.viatorfortis.popularmovies.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.viatorfortis.popularmovies.db.MovieContract.FavouriteMoviesEntry;

public class MovieContentProvider extends ContentProvider {

    public static final int FAVOURITE_MOVIES = 100;

    public static final int FAVOURITE_MOVIE_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // match for the whole favouriteMovies directory
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVOURITE_MOVIES, FAVOURITE_MOVIES);

        // match for single row of the favouriteMovies directory
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVOURITE_MOVIES + "/#", FAVOURITE_MOVIE_WITH_ID);

        return uriMatcher;
    }

    private MovieDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext() );
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case FAVOURITE_MOVIES:
                long id = db.insert(FavouriteMoviesEntry.TABLE_NAME, null, values);
                if (id >= 0) {
                    returnUri = ContentUris.withAppendedId(FavouriteMoviesEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into table (URI: " + uri.toString() + ")");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unidentified URI: " + uri.toString() );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match) {
            case FAVOURITE_MOVIES:
                returnCursor = db.query(FavouriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVOURITE_MOVIE_WITH_ID:
                String rowId = uri.getPathSegments().get(1);
                selection = FavouriteMoviesEntry._ID + "=?";

                returnCursor = db.query(FavouriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String [] {rowId},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unidentified URI: " + uri.toString() );
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int deletedRowsQuantity;

        switch (match) {
            case FAVOURITE_MOVIE_WITH_ID:
                String rowId = uri.getPathSegments().get(1);
                deletedRowsQuantity = db.delete(FavouriteMoviesEntry.TABLE_NAME,
                        FavouriteMoviesEntry._ID + "=?",
                        new String [] {rowId});
                break;
            default:
                throw new UnsupportedOperationException("Unidentified URI: " + uri.toString() );
        }

        if (deletedRowsQuantity > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRowsQuantity;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
