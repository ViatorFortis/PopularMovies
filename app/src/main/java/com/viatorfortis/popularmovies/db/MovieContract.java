package com.viatorfortis.popularmovies.db;

import android.provider.BaseColumns;

public final class MovieContract {

    private MovieContract() {}

    public static final class FavouriteMovieEntry
            implements BaseColumns {
        public static final String TABLE_NAME = "favouriteMovies";

        public static final String COLUMN_NAME_TITLE = "title";

        public static final String COLUMN_NAME_RELEASEDATE = "releaseDate";

        public static final String COLUMN_NAME_POSTERPATH = "posterPath";

        public static final String COLUMN_NAME_VOTEAVERAGE = "voteAverage";

        public static final String COLUMN_NAME_PLOTSYNOPSIS = "plotSynopsis";
    }

}
