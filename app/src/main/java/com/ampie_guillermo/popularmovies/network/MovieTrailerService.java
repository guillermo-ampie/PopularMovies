package com.ampie_guillermo.popularmovies.network;

import com.ampie_guillermo.popularmovies.model.MovieTrailerList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit turns your HTTP API into a Java interface.
 */
public interface MovieTrailerService {

  @GET("/3/movie/{movieid}/videos")
  Call<MovieTrailerList> get(@Path("movieid") String movieId,
                             @Query("api_key") String apiKey);
}
