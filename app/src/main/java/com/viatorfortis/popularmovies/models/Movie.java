package com.viatorfortis.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class Movie
        implements Parcelable {

    @SerializedName("id")
    private int mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("release_date")
    private String mReleaseDate;

    @SerializedName("poster_path")
    private String mPosterPath;

    @SerializedName("vote_average")
    private float mVoteAverage;

    @SerializedName("overview")
    private String mPlotSynopsis;

    public int getId() {
        return mId;
    }
    public void setId(int newValue) {
        mId = newValue;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String newValue) {
        mTitle = newValue;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }
    public void setReleaseDate(String newValue) {
        mReleaseDate = newValue;
    }

    public String getPosterPath() {
        return mPosterPath;
    }
    public void setPosterURL(String newValue) {
        mPosterPath = newValue;
    }

    public float getVoteAverage() {
        return mVoteAverage;
    }
    public void setVoteAverage(float newValue) {
        mVoteAverage = newValue;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }
    public void setPlotSynopsis(String newValue) {
        mPlotSynopsis = newValue;
    }

    public Movie(String title, String releaseDate, String posterURL, float voteAverage, String plotSynopsis) {
        mTitle = title;
        mReleaseDate = releaseDate;
        mPosterPath = posterURL;
        mVoteAverage = voteAverage;
        mPlotSynopsis = plotSynopsis;
    }


    private Movie(Parcel in) {
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
        mVoteAverage = in.readFloat();
        mPlotSynopsis = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mPosterPath);
        dest.writeFloat(mVoteAverage);
        dest.writeString(mPlotSynopsis);
    }
}
