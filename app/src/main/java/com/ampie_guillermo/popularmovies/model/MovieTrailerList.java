package com.ampie_guillermo.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * A class to store a movie's list of trailers
 */
public class MovieTrailerList {

    @SerializedName("id")
    private final String mMovieId;

    @SerializedName("results")
    private final ArrayList <MovieTrailerList.MovieTrailer>  mTrailerList;

    public MovieTrailerList() {
        mMovieId = "";
        mTrailerList = new ArrayList<>();
    }

    public ArrayList<MovieTrailerList.MovieTrailer> getTrailerList() {
        return mTrailerList;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public static class MovieTrailer {

        @SerializedName("key")
        private final String mKey;

        @SerializedName("name")
        private final String mName;

        public MovieTrailer(String key, String name) {
            mKey  = key;
            mName = name;
        }

        public String getKey() {
            return mKey;
        }

        public String getName() {
            return mName;
        }
    }
}
