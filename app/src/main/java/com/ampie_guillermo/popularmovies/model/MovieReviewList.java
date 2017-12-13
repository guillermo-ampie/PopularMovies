package com.ampie_guillermo.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * A class to store a movie's list of reviews
 */
public class MovieReviewList {

    @SerializedName("id")
    private final String mMovieId;

    @SerializedName("results")
    private final ArrayList<MovieReviewList.MovieReview> mReviewList;

    public MovieReviewList() {
        // TODO: Check this assignment....
        mMovieId = "";
        mReviewList = new ArrayList<>();
    }

    public ArrayList<MovieReviewList.MovieReview> getReviewList() {
        return mReviewList;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public static class MovieReview {

        @SerializedName("id")
        private final String mReviewId;

        @SerializedName("author")
        private final String mAuthor;

        @SerializedName("content")
        private final String mContent;

        public MovieReview(String reviewId, String author, String content) {
            mReviewId = reviewId;
            mAuthor   = author;
            mContent  = content;
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
    }
}
