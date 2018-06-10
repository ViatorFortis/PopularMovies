package com.viatorfortis.popularmovies.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.viatorfortis.popularmovies.models.Movie;
import com.viatorfortis.popularmovies.models.MovieListPage;
import com.viatorfortis.popularmovies.models.MovieReview;
import com.viatorfortis.popularmovies.models.MovieReviewListPage;
import com.viatorfortis.popularmovies.models.MovieVideo;
import com.viatorfortis.popularmovies.models.MovieVideoList;
import java.util.List;

public class JsonUtils {
    public static List<Movie> parseMovieListJson(String json)
            throws JsonSyntaxException {
        Gson gson = new Gson();
        MovieListPage movieListPage = gson.fromJson(json, MovieListPage.class);

        return movieListPage.mMovieList;
    }

    public static List<MovieReview> parseMovieReviewListJson(String json)
            throws JsonSyntaxException {
        Gson gson = new Gson();
        MovieReviewListPage movieReviewListPage = gson.fromJson(json, MovieReviewListPage.class);

        return movieReviewListPage.mMovieReviewList;
    }

    public static List<MovieVideo> parseMovieVideoListJson(String json)
        throws JsonSyntaxException {
        Gson gson = new Gson();
        MovieVideoList movieVideoList = gson.fromJson(json, MovieVideoList.class);

        return  movieVideoList.mMovieVideoList;
    }
}
