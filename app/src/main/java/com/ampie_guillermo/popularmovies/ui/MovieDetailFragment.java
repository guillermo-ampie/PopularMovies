package com.ampie_guillermo.popularmovies.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ampie_guillermo.popularmovies.BuildConfig;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.model.MovieReviewList;
import com.ampie_guillermo.popularmovies.model.MovieTrailerList;
import com.ampie_guillermo.popularmovies.network.MovieReviewService;
import com.ampie_guillermo.popularmovies.network.MovieTrailerService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * The fragment to display the movie's data.
 *
 * Reference code to add views dynamically into a ViewGroup
 * http://www.myandroidsolutions.com/2013/02/10/android-add-views-into-view-dynamically/
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    // The BASE URL is the same for trailers & reviews
    private static final String MOVIEDB_TRAILER_BASE_URL = "https://api.themoviedb.org";

    private View rootView;
    private MovieTrailerList mTrailers;
    private MovieReviewList mReviews;

    /**
     * Just avoid creating the retrofit object with every instantiation of the
     * MovieDetailFragment object (singleton pattern: eager initialization)
     */
    private static final Retrofit retrofit
            = new Retrofit.Builder()
                          .baseUrl(MOVIEDB_TRAILER_BASE_URL)
                          .addConverterFactory(GsonConverterFactory.create())
                          .build();

    private Call<MovieTrailerList> mCallTrailers;
    private Call<MovieReviewList> mCallReviews;

//    private RecyclerView mRecyclerView;
//    private FastItemAdapter <MovieTrailerItem> mTrailerAdapter;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        /**
         * SDK_INT is available since "Donut"(API 4). Since we are using minSDK=16 we
         * are OK to ask for SDK_INT
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // "ScrollIndicators" attribute is available since "Marshmallow (API 23)"
            ScrollView sv = (ScrollView) rootView.findViewById(R.id.main_scroll_view);
            sv.setScrollIndicators(View.SCROLL_INDICATOR_RIGHT);
        }

        Intent intent = getActivity().getIntent();

        // Get the selected movie passed by Intent
        Movie selectedMovie = intent.getExtras().getParcelable("selected-movie");
        if (selectedMovie != null) {
            // we will reuse -tv- variable for all the TextView objects in this fragment
            TextView tv = (TextView) rootView.findViewById(R.id.movie_title_text);
            tv.setText(selectedMovie.getMovieOriginalTitle());

            ImageView moviePosterView =
                    (ImageView) rootView.findViewById(R.id.movie_poster_detail_view);
            // Show the movie poster
            Picasso.with(getContext())
                   .load(selectedMovie.getMoviePosterCompleteUri())
                   .placeholder(R.drawable.no_thumbnail)
                   .error(R.drawable.no_thumbnail)
                   .into(moviePosterView);

            tv = (TextView) rootView.findViewById(R.id.release_date_text);
            tv.setText(selectedMovie.getMovieReleaseDate());

            String rating = String.valueOf(selectedMovie.getMovieVoteAverage());

            tv = (TextView) rootView.findViewById(R.id.rating_text);
            tv.setText(rating);

            String votes = String.valueOf(selectedMovie.getMovieVoteCount());
            tv = (TextView) rootView.findViewById(R.id.vote_count_text);
            tv.setText(votes);

            tv = (TextView) rootView.findViewById(R.id.movie_overview_text);
            tv.setText(selectedMovie.getMovieOverview());

/*
            // Get a reference to the RecyclerView
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_list);

            // We will show the movie trailers in just one column, so a LinearLayoutManager
            // will do the job
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            mTrailerAdapter = new FastItemAdapter<>();
            // Attach the trailer adapter to the RecyclerView
            mRecyclerView.setAdapter(mTrailerAdapter);
*/
            /**
             * Get the movie trailers
             */
            fetchTrailers(selectedMovie);
            /**
             * Get the movie reviews
             */
            fetchReviews(selectedMovie);
        }
        return rootView;
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to { Activity#onStop() Activity.onStop} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop() {
        super.onStop();

        // Cancel the request if the HTTP scheduler has not executed it already...
        mCallTrailers.cancel();
        mCallReviews.cancel();
    }

    private void fetchTrailers(Movie selectedMovie) {
        // Create an instance of our MovieTrailerService.
        MovieTrailerService movieTrailerService = retrofit.create(MovieTrailerService.class);

        // Create a call instance for looking up the movie's list of trailers
        mCallTrailers = movieTrailerService.get(selectedMovie.getMovieID(),
                                                BuildConfig.MOVIE_DB_API_KEY);

        // Fetch the trailers
        mCallTrailers.enqueue(new Callback<MovieTrailerList>() {
            @Override
            public void onResponse(Response<MovieTrailerList> response) {
                if (response.isSuccess()) {
                    // Here we get the movie trailer list!
                    mTrailers = response.body();

                    if (!mTrailers.getTrailerList().isEmpty()) {

                        final String THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
                        final String THUMBNAIL_IMAGE_TYPE = "/mqdefault.jpg";

                        // Parent layout for the trailer thumbnail layout
                        LinearLayout parentLayout =
                                (LinearLayout) rootView.findViewById(R.id.trailer_linear_layout);

                        // Layout inflater to inflate the trailer thumbnail layout
                        LayoutInflater layoutInflater = getLayoutInflater(null);

                        for (final MovieTrailerList.MovieTrailer trailer : mTrailers.getTrailerList()) {

                            // Add the thumbnail trailer layout to its parent layout
                            View parentView
                                    = layoutInflater.inflate(R.layout.trailer_thumbnail_layout,
                                    parentLayout,
                                    false);
                            // Now we can get the trailer view
                            ImageView trailerView
                                    = (ImageView) parentView.findViewById(R.id.trailer_thumbnail_view);

                            // Add the trailer view to its parent layout
                            parentLayout.addView(trailerView);

                            final Uri thumbnailUri
                                    = Uri.withAppendedPath(Uri.parse(THUMBNAIL_BASE_URL),
                                    trailer.getKey() + THUMBNAIL_IMAGE_TYPE);

                            // Show trailer thumbnail image
                            Picasso.with(getContext())
                                    .load(thumbnailUri)
                                    .placeholder(R.drawable.no_thumbnail)
                                    .error(R.drawable.no_thumbnail)
                                    .fit()
                                    .into(trailerView);

                            trailerView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?";
                                    final String VIDEO_PARAM = "v";
                                    final Uri trailerUri = Uri.parse(YOUTUBE_BASE_URL)
                                            .buildUpon()
                                            .appendQueryParameter(VIDEO_PARAM,
                                                    trailer.getKey())
                                            .build();
                                    // Play the movie trailer on youtube.com
                                    // TODO: Expand code to play trailer in youtube app if installed 
                                    startActivity(new Intent(Intent.ACTION_VIEW, trailerUri));
                                }
                            });
                            Log.e(LOG_TAG, "movie id: " + mTrailers.getMovieID());
                            Log.e(LOG_TAG, "trailer name: " + trailer.getName());
                            Log.e(LOG_TAG, "trailer key: " + trailer.getKey());

                        }
                    }

                } else {
                    Log.e(LOG_TAG,
                            "Bad response from TheMovieDB.org server: " + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO: Improve Callback´s error handling
                Log.e(LOG_TAG, "Error contacting theMovieDB.org Server: " + t.getMessage());
            }
        });
    }

    private void fetchReviews(Movie selectedMovie) {
        // Create an instance of our MovieReviewService.
        MovieReviewService movieReviewService = retrofit.create(MovieReviewService.class);

        // Create a call instance for looking up the movie's list of trailers
        mCallReviews = movieReviewService.get(selectedMovie.getMovieID(),
                                              BuildConfig.MOVIE_DB_API_KEY);

        // Fetch the Reviews
        mCallReviews.enqueue(new Callback<MovieReviewList>() {
            @Override
            public void onResponse(Response<MovieReviewList> response) {
                if (response.isSuccess()) {
                    // Here we get the movie review list!
                    mReviews = response.body();
                    for (MovieReviewList.MovieReview review : mReviews.getReviewList()) {
                        Log.e(LOG_TAG, "movie id: " + mReviews.getMovieID());
                        Log.e(LOG_TAG, "review id: " + review.getID());
                        Log.e(LOG_TAG, "author: " + review.getAuthor());
                        Log.e(LOG_TAG, "content: " + review.getContent());
                    }
                } else {
                    Log.e(LOG_TAG,
                          "Bad response from TheMovieDB.org server: " + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO: Improve Callback´s error handling
                Log.e(LOG_TAG, "Error contacting theMovieDB.org Server: " + t.getMessage());
            }
        });
    }
}
