package com.ampie_guillermo.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class to store a movie's list of trailers.
 */
public class MovieTrailerList implements Parcelable {

  public static final Creator<MovieTrailerList> CREATOR = new Creator<MovieTrailerList>() {
    @Override
    public MovieTrailerList createFromParcel(Parcel source) {
      return new MovieTrailerList(source);
    }

    @Override
    public MovieTrailerList[] newArray(int size) {
      return new MovieTrailerList[size];
    }
  };

  @SerializedName("id")
  private final String mMovieId;
  @SerializedName("results")
  private ArrayList<MovieTrailerList.MovieTrailer> mTrailerList;

  public MovieTrailerList() {
    mMovieId = "";
    mTrailerList = new ArrayList<>();
  }

  protected MovieTrailerList(Parcel in) {
    mMovieId = in.readString();
    mTrailerList = in.createTypedArrayList(MovieTrailer.CREATOR);
  }

  public List<MovieTrailer> getTrailerList() {
    return Collections.unmodifiableList(mTrailerList);
  }

  public void setTrailerList(final ArrayList<MovieTrailer> trailerList) {
    mTrailerList = new ArrayList<>(trailerList);
  }

  public String getMovieId() {
    return mMovieId;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(mMovieId);
    dest.writeTypedList(mTrailerList);
  }

  public static class MovieTrailer implements Parcelable {

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
      @Override
      public MovieTrailer createFromParcel(Parcel source) {
        return new MovieTrailer(source);
      }

      @Override
      public MovieTrailer[] newArray(int size) {
        return new MovieTrailer[size];
      }
    };
    @SerializedName("key")
    private final String mKey;
    @SerializedName("name")
    private final String mName;

    public MovieTrailer(String key, String name) {
      mKey = key;
      mName = name;
    }

    protected MovieTrailer(Parcel in) {
      mKey = in.readString();
      mName = in.readString();
    }

    public String getKey() {
      return mKey;
    }

    public String getName() {
      return mName;
    }

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(mKey);
      dest.writeString(mName);
    }
  }
}
