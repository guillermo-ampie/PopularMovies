package com.ampie_guillermo.popularmovies.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Movie: contains all the movie's relevant data
 * Parcelable code generated with: http://www.parcelabler.com/
 */
public class Movie implements Parcelable {

  public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
    @Override
    public Movie createFromParcel(Parcel source) {
      return new Movie(source);
    }

    @Override
    public Movie[] newArray(int size) {
      return new Movie[size];
    }
  };

  private final String mId;
  private final String mOriginalTitle;
  private final String mReleaseDate;
  private final String mOverview;
  private final Uri mPosterUri;
  private final Uri mBackdropUri;
  private final float mVoteAverage;
  private final int mVoteCount;

  Movie(String id,
      String originalTitle,
      String releaseDate,
      String overview,
      Uri posterUri,
      Uri backdropUri,
      float voteAverage,
      int voteCount) {
    mId = id;
    mOriginalTitle = originalTitle;
    mReleaseDate = releaseDate;
    mOverview = overview;
    mPosterUri = posterUri;
    mBackdropUri = backdropUri;
    mVoteAverage = voteAverage;
    mVoteCount = voteCount;
  }

  Movie(Parcel in) {
    mId = in.readString();
    mOriginalTitle = in.readString();
    mReleaseDate = in.readString();
    mOverview = in.readString();
    mPosterUri = (Uri) in.readValue(Uri.class.getClassLoader());
    mBackdropUri = (Uri) in.readValue(Uri.class.getClassLoader());
    mVoteAverage = in.readFloat();
    mVoteCount = in.readInt();
  }

  public String getId() {
    return mId;
  }

  public String getOriginalTitle() {
    return mOriginalTitle;
  }

  public String getReleaseDate() {
    return mReleaseDate;
  }

  public String getOverview() {
    return mOverview;
  }

  public Uri getPosterUri() { return mPosterUri; }

  public Uri getBackdropUri() {
    return mBackdropUri;
  }

  public float getVoteAverage() {
    return mVoteAverage;
  }

  public int getVoteCount() {
    return mVoteCount;
  }

  @Override
  public String toString() {
    return "Movie{" +
        "mId='" + mId + '\'' +
        ", mOriginalTitle='" + mOriginalTitle + '\'' +
        ", mReleaseDate='" + mReleaseDate + '\'' +
        ", mOverview='" + mOverview + '\'' +
        ", mPosterUri=" + mPosterUri +
        ", mBackdropUri=" + mBackdropUri +
        ", mVoteAverage=" + mVoteAverage +
        ", mVoteCount=" + mVoteCount +
        '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(mId);
    dest.writeString(mOriginalTitle);
    dest.writeString(mReleaseDate);
    dest.writeString(mOverview);
    dest.writeValue(mPosterUri);
    dest.writeValue(mBackdropUri);
    dest.writeFloat(mVoteAverage);
    dest.writeInt(mVoteCount);
  }

  public static class MovieBuilder {

    private String mId;
    private String mOriginalTitle;
    private String mReleaseDate;
    private String mOverview;
    private Uri mPosterUri;
    private Uri mBackdropUri;
    private float mVoteAverage;
    private int mVoteCount;

    public Movie.MovieBuilder setId(String id) {
      mId = id;
      return this;
    }

    public Movie.MovieBuilder setOriginalTitle(String originalTitle) {
      mOriginalTitle = originalTitle;
      return this;
    }

    public Movie.MovieBuilder setReleaseDate(String releaseDate) {
      mReleaseDate = releaseDate;
      return this;
    }

    public Movie.MovieBuilder setOverview(String overview) {
      mOverview = overview;
      return this;
    }

    public Movie.MovieBuilder setPosterUri(Uri posterUri) {
      mPosterUri = posterUri;
      return this;
    }

    public Movie.MovieBuilder setBackdropUri(Uri backdropUri) {
      mBackdropUri = backdropUri;
      return this;
    }

    public Movie.MovieBuilder setVoteAverage(float voteAverage) {
      mVoteAverage = voteAverage;
      return this;
    }

    public Movie.MovieBuilder setVoteCount(int voteCount) {
      mVoteCount = voteCount;
      return this;
    }

    public Movie build() {
      return new Movie(mId,
          mOriginalTitle,
          mReleaseDate,
          mOverview,
          mPosterUri,
          mBackdropUri,
          mVoteAverage,
          mVoteCount);
    }
  }
}