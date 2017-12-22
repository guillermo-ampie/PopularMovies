package com.ampie_guillermo.popularmovies.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Movie: contains all the movie's relevant data
 * Parcelable code generated with: http://www.parcelabler.com/
 */
public class Movie implements Parcelable {

    @SuppressWarnings("unused")
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

    private final String mId;
    private final String mOriginalTitle;
    private final String mReleaseDate;
    private final String mOverview;
    private final Uri mPosterCompleteUri;
    private final double mVoteAverage;
    private final int mVoteCount;

    Movie(String id,
          String originalTitle,
          String releaseDate,
          String overview,
          Uri posterCompleteUri,
          double voteAverage,
          int voteCount) {
        mId = id;
        mOriginalTitle = originalTitle;
        mReleaseDate = releaseDate;
        mOverview = overview;
        mPosterCompleteUri = posterCompleteUri;
        mVoteAverage = voteAverage;
        mVoteCount = voteCount;
    }

    private Movie(Parcel in) {
        mId = in.readString();
        mOriginalTitle = in.readString();
        mReleaseDate = in.readString();
        mOverview = in.readString();
        mPosterCompleteUri = (Uri) in.readValue(Uri.class.getClassLoader());
        mVoteAverage = in.readDouble();
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

    public Uri getPosterCompleteUri() {
        return mPosterCompleteUri;
    }

    public double getVoteAverage() {
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
                ", mPosterCompleteUri=" + mPosterCompleteUri +
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
        dest.writeValue(mPosterCompleteUri);
        dest.writeDouble(mVoteAverage);
        dest.writeInt(mVoteCount);
    }

    public static class MovieBuilder {
        private String mId;
        private String mOriginalTitle;
        private String mReleaseDate;
        private String mOverview;
        private Uri mPosterCompleteUri;
        private double mVoteAverage;
        private int mVoteCount;

        public MovieBuilder setId(String id) {
            mId = id;
            return this;
        }

        public MovieBuilder setOriginalTitle(String originalTitle) {
            mOriginalTitle = originalTitle;
            return this;
        }

        public MovieBuilder setReleaseDate(String releaseDate) {
            mReleaseDate = releaseDate;
            return this;
        }

        public MovieBuilder setOverview(String overview) {
            mOverview = overview;
            return this;
        }

        public MovieBuilder setPosterCompleteUri(Uri posterCompleteUri) {
            mPosterCompleteUri = posterCompleteUri;
            return this;
        }

        public MovieBuilder setVoteAverage(double voteAverage) {
            mVoteAverage = voteAverage;
            return this;
        }

        public MovieBuilder setVoteCount(int voteCount) {
            mVoteCount = voteCount;
            return this;
        }

        public Movie build() {
            return new Movie(mId,
                    mOriginalTitle,
                    mReleaseDate,
                    mOverview,
                    mPosterCompleteUri,
                    mVoteAverage,
                    mVoteCount);
        }
    }
}