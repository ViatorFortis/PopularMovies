package com.viatorfortis.popularmovies.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieReviewListPage {
    @SerializedName("results")
    public List<MovieReview> mMovieReviewList;
}
