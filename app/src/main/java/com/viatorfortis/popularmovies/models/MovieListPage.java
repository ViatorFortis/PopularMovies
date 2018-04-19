package com.viatorfortis.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieListPage {

    @SerializedName("results")
    public List<Movie> mMovieList;
}
