package com.viatorfortis.popularmovies.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieVideoList {
    @SerializedName("results")
    public List<MovieVideo> mMovieVideoList;
}
