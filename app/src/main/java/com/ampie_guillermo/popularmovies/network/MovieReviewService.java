package com.ampie_guillermo.popularmovies.network;

import com.ampie_guillermo.popularmovies.model.MovieReviewList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit turns your HTTP API into a Java interface
 */
public interface MovieReviewService {
    @GET("/3/movie/{movieid}/reviews")
    Call<MovieReviewList> get(@Path("movieid") String movieID,
                              @Query("api_key") String APIKey);

}
