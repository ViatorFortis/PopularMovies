package com.viatorfortis.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class Movie
        implements Parcelable {

    // for Popular movies part 2 project
    @SerializedName("id")
    private final int mId;

    @SerializedName("title")
    private final String mTitle;

    @SerializedName("release_date")
    private final String mReleaseDate;

    @SerializedName("poster_path")
    private final String mPosterPath;

    @SerializedName("vote_average")
    private final float mVoteAverage;

    @SerializedName("overview")
    private final String mPlotSynopsis;

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public float getVoteAverage() {
        return mVoteAverage;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    public Movie(int id, String title, String releaseDate, String posterURL, float voteAverage, String plotSynopsis) {
        mId = id;
        mTitle = title;
        mReleaseDate = releaseDate;
        mPosterPath = posterURL;
        mVoteAverage = voteAverage;
        mPlotSynopsis = plotSynopsis;
    }


    private Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
        mVoteAverage = in.readFloat();
        mPlotSynopsis = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
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
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mPosterPath);
        dest.writeFloat(mVoteAverage);
        dest.writeString(mPlotSynopsis);
    }
}
