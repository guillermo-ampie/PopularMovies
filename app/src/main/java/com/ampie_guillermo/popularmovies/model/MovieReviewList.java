package com.ampie_guillermo.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * A class to store a movie's list of reviews
 */
public class MovieReviewList implements Parcelable {

  public static final Creator<MovieReviewList> CREATOR = new Creator<MovieReviewList>() {
    @Override
    public MovieReviewList createFromParcel(Parcel source) {
      return new MovieReviewList(source);
    }

    @Override
    public MovieReviewList[] newArray(int size) {
      return new MovieReviewList[size];
    }
  };

  @SerializedName("id")
  private final String mMovieId;
  @SerializedName("results")
  private final ArrayList<MovieReviewList.MovieReview> mReviewList;

  public MovieReviewList() {
    // TODO: Check this assignment....
    mMovieId = "";
    mReviewList = new ArrayList<>();
  }

  protected MovieReviewList(Parcel in) {
    mMovieId = in.readString();
    mReviewList = in.createTypedArrayList(MovieReview.CREATOR);
  }

  public ArrayList<MovieReviewList.MovieReview> getReviewList() {
    return mReviewList;
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
    dest.writeTypedList(mReviewList);
  }

  public static class MovieReview implements Parcelable {

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
      @Override
      public MovieReview createFromParcel(Parcel source) {
        return new MovieReview(source);
      }

      @Override
      public MovieReview[] newArray(int size) {
        return new MovieReview[size];
      }
    };

    @SerializedName("id")
    private final String mReviewId;
    @SerializedName("author")
    private final String mAuthor;
    @SerializedName("content")
    private final String mContent;

    public MovieReview(String reviewId, String author, String content) {
      mReviewId = reviewId;
      mAuthor = author;
      mContent = content;
    }

    protected MovieReview(Parcel in) {
      mReviewId = in.readString();
      mAuthor = in.readString();
      mContent = in.readString();
    }

    public String getId() {
      return mReviewId;
    }

    public String getAuthor() {
      return mAuthor;
    }

    public String getContent() {
      return mContent;
    }

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(mReviewId);
      dest.writeString(mAuthor);
      dest.writeString(mContent);
    }
  }
}
