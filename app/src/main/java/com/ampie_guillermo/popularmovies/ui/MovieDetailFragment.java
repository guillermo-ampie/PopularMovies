package com.ampie_guillermo.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ampie_guillermo.popularmovies.BuildConfig;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.network.MovieTrailerService;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.model.MovieTrailerList;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * The fragment to display the movie's data.
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final String MOVIEDB_TRAILER_BASE_URL = "https://api.themoviedb.org";

    private MovieTrailerList mTrailers;
    private RecyclerView mRecyclerView;
    private FastItemAdapter <MovieTrailerItem> mTrailerAdapter;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();

        // Get the selected movie passed by Intent
        Movie selectedMovie = intent.getExtras().getParcelable("selected-movie");
        if (selectedMovie != null) {
/*            // we will reuse -tv- variable for all the TextView objects in this fragment
            TextView tv = (TextView) rootView.findViewById(R.id.movie_title_text);
            //tv.setText(selectedMovie.getMovieOriginalTitle());

            ImageView moviePosterView =
                    (ImageView) rootView.findViewById(R.id.movie_poster_detail_view);
            //Picasso.with(getContext())
            //       .load(selectedMovie.getMoviePosterCompleteUri())
            //       .into(moviePosterView);

            tv = (TextView) rootView.findViewById(R.id.release_date_text);
            //tv.setText(selectedMovie.getMovieReleaseDate());

            String rating = String.valueOf(selectedMovie.getMovieVoteAverage());

            tv = (TextView) rootView.findViewById(R.id.rating_text);
            //tv.setText(rating);

            String votes = String.valueOf(selectedMovie.getMovieVoteCount());
            tv = (TextView) rootView.findViewById(R.id.vote_count_text);
            //tv.setText(votes);

            tv = (TextView) rootView.findViewById(R.id.movie_overview_text);
            //tv.setText(selectedMovie.getMovieOverview());
*/
            // Get a reference to the RecyclerView
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_list);

            // We will show the movie trailers in just one column, so a LinearLayoutManager
            // will do the job
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            mTrailerAdapter = new FastItemAdapter<>();
            // Attach the trailer adapter to the RecyclerView
            mRecyclerView.setAdapter(mTrailerAdapter);

            /**
             * Get the movie trailers
             */
            fetchTrailers(selectedMovie);
            }

        return rootView;
    }

    private void fetchTrailers(Movie selectedMovie) {

        // Create a simple REST adapter which points to theMovieDB.org API
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(MOVIEDB_TRAILER_BASE_URL)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

        // Create an instance of our MovieTrailerService.
        MovieTrailerService movieTrailerService = retrofit.create(MovieTrailerService.class);

        // Create a call instance for looking up the movie's list of trailers
        Call<MovieTrailerList> call = movieTrailerService.get(selectedMovie.getMovieID(),
                                                              BuildConfig.MOVIE_DB_API_KEY);

        // Fetch the trailers
        call.enqueue(new Callback<MovieTrailerList>() {
            @Override
            public void onResponse(Response<MovieTrailerList> response) {
                if (response.isSuccess()) {
                    // Here we get the movie trailer list!
                    mTrailers = response.body();
                    mTrailerAdapter.clear();
                    for (MovieTrailerList.MovieTrailer trailer : mTrailers.getTrailerList()) {
                        mTrailerAdapter.add(new MovieTrailerItem(trailer));
                        Log.e(LOG_TAG, "movie id: " + mTrailers.getMovieID());
                        Log.e(LOG_TAG, "trailer name: " + trailer.getName());
                        Log.e(LOG_TAG, "trailer key: " + trailer.getKey());
                    }
                    Log.e(LOG_TAG, "Number of trailers: " + mTrailerAdapter.getAdapterItemCount());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO: Impvove Callback´s error handling
                Log.e(LOG_TAG, "Error contacting theMovieDB.org Server: " + t.getMessage());
            }
        });
    }
}
