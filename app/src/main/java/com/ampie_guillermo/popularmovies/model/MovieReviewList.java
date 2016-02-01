package com.ampie_guillermo.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * A class to store a movie's list of reviews
 */
public class MovieReviewList {

    @SerializedName("id")
    private final String mMovieID;

    @SerializedName("results")
    private final ArrayList<MovieReview> mReviewList;

    public MovieReviewList() {
        mMovieID = "";
        mReviewList = new ArrayList<>();
    }

    public ArrayList<MovieReview> getReviewList() {
        return mReviewList;
    }

    public String getMovieID() {
        return mMovieID;
    }

    public static class MovieReview {

        @SerializedName("id")
        private final String mReviewID;

        @SerializedName("author")
        private final String mAuthor;

        @SerializedName("content")
        private final String mContent;

        public MovieReview(String reviewID, String author, String content) {
            mReviewID = reviewID;
            mAuthor   = author;
            mContent  = content;
        }

        public String getID() {
            return mReviewID;
        }

        public String getAuthor() {
            return mAuthor;
        }

        public String getContent() {
            return mContent;
        }
    }
}
