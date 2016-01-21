package com.ampie_guillermo.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit turns your HTTP API into a Java interface
 */
public interface MovieTrailerService {

    @GET("/3/movie/{movieid}/videos")
    Call <MovieTrailerList> get (@Path("movieid") String movieID,
                                 @Query("api_key") String APIKey);
}
