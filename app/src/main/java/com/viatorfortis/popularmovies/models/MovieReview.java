package com.viatorfortis.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class MovieReview implements Parcelable {

    @SerializedName("id")
    private final String mId;

    @SerializedName("author")
    private final String mAuthor;

    @SerializedName("content")
    private final String mContent;

    public String getId() { return mId; }

    public String getAuthor() { return mAuthor; }

    public String getContent() { return mContent; }

    public MovieReview (String id, String author, String content) {
        mId = id;
        mAuthor = author;
        mContent = content;
    }

    private MovieReview (Parcel in) {
        mId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
    }

    public static final Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };
}
