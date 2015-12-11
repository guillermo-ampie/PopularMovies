package com.ampie_guillermo.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Movie: contains all the movie's relevant data
 * Parcelable code generated with: http://www.parcelabler.com/
 */
public class Movie implements Parcelable {

    private final String movieID;
    private final String movieOriginalTitle;
    private final String movieReleaseDate;
    private final String movieOverview;
    private final Uri    moviePosterCompleteUri;
    private final double movieVoteAverage;
    private final int    movieVoteCount;


    public Movie(String movieID,
                 String movieOriginalTitle,
                 String movieReleaseDate,
                 String movieOverview,
                 Uri moviePosterCompleteUri,
                 double movieVoteAverage,
                 int movieVoteCount) {
        this.movieID = movieID;
        this.movieOriginalTitle = movieOriginalTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.movieOverview = movieOverview;
        this.moviePosterCompleteUri = moviePosterCompleteUri;
        this.movieVoteAverage = movieVoteAverage;
        this.movieVoteCount = movieVoteCount;
    }

    public String getMovieID() {
        return movieID;
    }

    public String getMovieOriginalTitle() {
        return movieOriginalTitle;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public Uri getMoviePosterCompleteUri() {
        return moviePosterCompleteUri;
    }

    public double getMovieVoteAverage() {
        return movieVoteAverage;
    }

    public int getMovieVoteCount() {
        return movieVoteCount;
    }

    protected Movie(Parcel in) {
        movieID = in.readString();
        movieOriginalTitle = in.readString();
        movieReleaseDate = in.readString();
        movieOverview = in.readString();
        moviePosterCompleteUri = (Uri) in.readValue(Uri.class.getClassLoader());
        movieVoteAverage = in.readDouble();
        movieVoteCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieID);
        dest.writeString(movieOriginalTitle);
        dest.writeString(movieReleaseDate);
        dest.writeString(movieOverview);
        dest.writeValue(moviePosterCompleteUri);
        dest.writeDouble(movieVoteAverage);
        dest.writeInt(movieVoteCount);
    }

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
}

