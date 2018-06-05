package com.viatorfortis.popularmovies.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieContract {

    private MovieContract() {}

    public static final String AUTHORITY = "com.viatorfortis.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVOURITE_MOVIES = "favouriteMovies";

    public static final class FavouriteMoviesEntry
            implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE_MOVIES).build();

        public static final String TABLE_NAME = "favouriteMovies";

        public static final String COLUMN_NAME_TITLE = "title";

        public static final String COLUMN_NAME_RELEASEDATE = "releaseDate";

        public static final String COLUMN_NAME_POSTERPATH = "posterPath";

        public static final String COLUMN_NAME_VOTEAVERAGE = "voteAverage";

        public static final String COLUMN_NAME_PLOTSYNOPSIS = "plotSynopsis";

        public static final String [] FULL_PROJECTION = {
                _ID,
                COLUMN_NAME_TITLE,
                COLUMN_NAME_RELEASEDATE,
                COLUMN_NAME_POSTERPATH,
                COLUMN_NAME_VOTEAVERAGE,
                COLUMN_NAME_PLOTSYNOPSIS
            };
    }

}
