package com.ampie_guillermo.popularmovies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * A class to store a movie's list of trailers
 */
public class MovieTrailerList {

    @SerializedName("id")
    private final String mMovieID;

    @SerializedName("results")
    private final ArrayList <MovieTrailer>  mTrailerList;

    public MovieTrailerList() {
        mTrailerList = new ArrayList<>();
        mMovieID     = null;
    }

    public ArrayList<MovieTrailer> getTrailerList() {
        return mTrailerList;
    }

    public static class MovieTrailer {

        @SerializedName("key")
        private final String mKey;

        @SerializedName("name")
        private final String mName;

        public MovieTrailer(String mKey, String mName) {
            this.mKey  = mKey;
            this.mName = mName;
        }

        public String getKey() {
            return mKey;
        }

        public String getName() {
            return mName;
        }
    }
}
