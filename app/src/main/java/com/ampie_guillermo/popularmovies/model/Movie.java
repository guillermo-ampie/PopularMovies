package com.ampie_guillermo.popularmovies.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Movie: contains all the movie's relevant data
 * Parcelable code generated with: http://www.parcelabler.com/
 */
public class Movie implements Parcelable {

    private final String mMovieId;
    private final String mMovieOriginalTitle;
    private final String mMovieReleaseDate;
    private final String mMovieOverview;
    private final Uri    mMoviePosterCompleteUri;
    private final double mMovieVoteAverage;
    private final int    mMovieVoteCount;


    public Movie(String movieId,
                 String movieOriginalTitle,
                 String movieReleaseDate,
                 String movieOverview,
                 Uri moviePosterCompleteUri,
                 double movieVoteAverage,
                 int movieVoteCount) {
        mMovieId = movieId;
        mMovieOriginalTitle = movieOriginalTitle;
        mMovieReleaseDate = movieReleaseDate;
        mMovieOverview = movieOverview;
        mMoviePosterCompleteUri = moviePosterCompleteUri;
        mMovieVoteAverage = movieVoteAverage;
        mMovieVoteCount = movieVoteCount;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public String getMovieOriginalTitle() {
        return mMovieOriginalTitle;
    }

    public String getMovieReleaseDate() {
        return mMovieReleaseDate;
    }

    public String getMovieOverview() {
        return mMovieOverview;
    }

    public Uri getMoviePosterCompleteUri() {
        return mMoviePosterCompleteUri;
    }

    public double getMovieVoteAverage() {
        return mMovieVoteAverage;
    }

    public int getMovieVoteCount() {
        return mMovieVoteCount;
    }

    private Movie(Parcel in) {
        mMovieId = in.readString();
        mMovieOriginalTitle = in.readString();
        mMovieReleaseDate = in.readString();
        mMovieOverview = in.readString();
        mMoviePosterCompleteUri = (Uri) in.readValue(Uri.class.getClassLoader());
        mMovieVoteAverage = in.readDouble();
        mMovieVoteCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieId);
        dest.writeString(mMovieOriginalTitle);
        dest.writeString(mMovieReleaseDate);
        dest.writeString(mMovieOverview);
        dest.writeValue(mMoviePosterCompleteUri);
        dest.writeDouble(mMovieVoteAverage);
        dest.writeInt(mMovieVoteCount);
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
    public String toString() {
        return "Movie{" +
                "mMovieId='" + mMovieId + '\'' +
                ", mMovieOriginalTitle='" + mMovieOriginalTitle + '\'' +
                ", mMovieReleaseDate='" + mMovieReleaseDate + '\'' +
                ", mMovieOverview='" + mMovieOverview + '\'' +
                ", mMoviePosterCompleteUri=" + mMoviePosterCompleteUri +
                ", mMovieVoteAverage=" + mMovieVoteAverage +
                ", mMovieVoteCount=" + mMovieVoteCount +
                '}';
    }
}

