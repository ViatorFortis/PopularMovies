package com.viatorfortis.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class MovieVideo implements Parcelable {
    @SerializedName("id")
    private final String mId;

    @SerializedName("key")
    private final String mKey;

    @SerializedName("name")
    private final String mName;

    @SerializedName("size")
    private final int mSize;

    public String getId() { return mId; }

    public String getKey() { return mKey; }

    public String getName() { return mName; }

    public int getSize() { return mSize; }

    public MovieVideo(String id, String key, String name, int size) {
        mId = id;
        mKey = key;
        mName = name;
        mSize = size;
    }

    private MovieVideo(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSize = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeInt(mSize);
    }

    public static final Parcelable.Creator<MovieVideo> CREATOR = new Parcelable.Creator<MovieVideo>() {
        @Override
        public MovieVideo createFromParcel(Parcel in) { return new MovieVideo(in); }

        @Override
        public MovieVideo[] newArray(int size) { return new MovieVideo[size]; }
    };
}
