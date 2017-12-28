package com.ampie_guillermo.popularmovies.ui;

import android.content.Context;
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
import android.widget.Toast;

import com.ampie_guillermo.popularmovies.BuildConfig;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.model.MovieReviewList;
import com.ampie_guillermo.popularmovies.model.MovieTrailerList;
import com.ampie_guillermo.popularmovies.network.MovieReviewService;
import com.ampie_guillermo.popularmovies.network.MovieTrailerService;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * The fragment to display the movie's data.
 * <p>
 * Reference code to add views dynamically into a ViewGroup
 * http://www.myandroidsolutions.com/2013/02/10/android-add-views-into-view-dynamically/
 */
public class MovieDetailFragment extends Fragment {

    static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    // The BASE URL is the same for trailers & reviews
    private static final String MOVIEDB_TRAILER_BASE_URL = "https://api.themoviedb.org";
    /**
     * Just avoid creating the retrofit object with every instantiation of the
     * MovieDetailFragment object (singleton pattern: eager initialization)
     */
    private static final Retrofit retrofit
            = new Retrofit.Builder()
            .baseUrl(MOVIEDB_TRAILER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    MovieTrailerList mTrailers;
    MovieReviewList mReviews;
    View mRootView;
    private Call<MovieTrailerList> mCallTrailers;
    private Call<MovieReviewList> mCallReviews;

//    private RecyclerView mRecyclerView;
//    private FastItemAdapter <MovieTrailerItem> mTrailerAdapter;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // "ScrollIndicators" attribute is available since "Marshmallow (API 23)"
            ScrollView sv = mRootView.findViewById(R.id.main_scroll_view);
            sv.setScrollIndicators(View.SCROLL_INDICATOR_RIGHT);
        }

        Intent intent = getActivity().getIntent();

        // Get the selected movie passed by Intent
        Movie selectedMovie = intent.getExtras().getParcelable("selected-movie");
        if (selectedMovie != null) {
            // we will reuse -tv- variable for all the TextView objects in this fragment
            TextView tv = mRootView.findViewById(R.id.movie_title_text);
            tv.setText(selectedMovie.getOriginalTitle());

            ImageView moviePosterView =
                    mRootView.findViewById(R.id.movie_poster_detail_view);
            // Show the movie poster
            Picasso.with(getContext())
                    .load(selectedMovie.getPosterCompleteUri())
                    .placeholder(R.drawable.no_thumbnail)
                    .error(R.drawable.no_thumbnail)
                    .into(moviePosterView);

            tv = mRootView.findViewById(R.id.release_date_text);
            tv.setText(selectedMovie.getReleaseDate());

            // Rating & Votes values will be formatted based on the current locale
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(1);
            String rating = numberFormat.format(selectedMovie.getVoteAverage());

            tv = mRootView.findViewById(R.id.rating_text);
            tv.setText(rating);

            numberFormat.setGroupingUsed(true);
            String votes = numberFormat.format(selectedMovie.getVoteCount());
            tv = mRootView.findViewById(R.id.vote_count_text);
            tv.setText(votes);

            tv = mRootView.findViewById(R.id.movie_overview_text);
            tv.setText(selectedMovie.getOverview());

/*
            // Get a reference to the RecyclerView
            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.trailer_list);

            // We will show the movie trailers in just one column, so a LinearLayoutManager
            // will do the job
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            mTrailerAdapter = new FastItemAdapter<>();
            // Attach the trailer adapter to the RecyclerView
            mRecyclerView.setAdapter(mTrailerAdapter);
*/
            /*
             * Get the movie trailers
             */
            fetchTrailers(selectedMovie);
            /*
             * Get the movie reviews
             */
            fetchReviews(selectedMovie);
        }
        return mRootView;
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
        mCallTrailers = movieTrailerService.get(selectedMovie.getId(),
                BuildConfig.MOVIE_DB_API_KEY);

        // Fetch the trailers
        mCallTrailers.enqueue(new Callback<MovieTrailerList>() {
            @Override
            public void onResponse(Call<MovieTrailerList> call, Response<MovieTrailerList> response) {
                if (response.isSuccessful()) {
                    // Here we get the movie trailer list!
                    mTrailers = response.body();

                    if (!mTrailers.getTrailerList().isEmpty()) {

                        final String THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
                        final String THUMBNAIL_IMAGE_TYPE = "/hqdefault.jpg";

                        // Parent layout for the trailer thumbnail layout
                        LinearLayout parentLayout =
                                mRootView.findViewById(R.id.trailer_linear_layout);

                        // Layout inflater to inflate the trailer thumbnail layout
                        LayoutInflater layoutInflater = getLayoutInflater();

                        for (final MovieTrailerList.MovieTrailer trailer : mTrailers.getTrailerList()) {

                            // Add the thumbnail trailer layout to its parent layout
                            View parentView =
                                    layoutInflater.inflate(R.layout.trailer_thumbnail_layout,
                                    parentLayout,
                                    false);
                            // Now we can get the trailer view
                            ImageView trailerView =
                                    parentView.findViewById(R.id.trailer_thumbnail_view);

                            // Add the trailer view to its parent layout
                            parentLayout.addView(trailerView);

                            final Uri thumbnailUri =
                                    Uri.withAppendedPath(Uri.parse(THUMBNAIL_BASE_URL),
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
                            Log.e(LOG_TAG, "movie id: " + mTrailers.getMovieId());
                            Log.e(LOG_TAG, "trailer name: " + trailer.getName());
                            Log.e(LOG_TAG, "trailer key: " + trailer.getKey());

                        }
                    }
                } else {
                    showErrorMessage(getContext(), R.string.error_bad_response, response.message());
                }
            }

            @Override
            public void onFailure(Call<MovieTrailerList> call, Throwable t) {
                showErrorMessage(getContext(), R.string.error_contacting_server, t.getMessage());
            }
        });
    }

    private void fetchReviews(Movie selectedMovie) {
        // Create an instance of our MovieReviewService.
        MovieReviewService movieReviewService = retrofit.create(MovieReviewService.class);

        // Create a call instance for looking up the movie's list of trailers
        mCallReviews = movieReviewService.get(selectedMovie.getId(),
                BuildConfig.MOVIE_DB_API_KEY);

        // Fetch the Reviews
        mCallReviews.enqueue(new Callback<MovieReviewList>() {
            @Override
            public void onResponse(Call<MovieReviewList> call, Response<MovieReviewList> response) {
                if (response.isSuccessful()) {
                    // Here we get the movie review list!
                    mReviews = response.body();
                    for (MovieReviewList.MovieReview review : mReviews.getReviewList()) {
                        Log.e(LOG_TAG, "movie id: " + mReviews.getMovieId());
                        Log.e(LOG_TAG, "review id: " + review.getId());
                        Log.e(LOG_TAG, "author: " + review.getAuthor());
                        Log.e(LOG_TAG, "content: " + review.getContent());
                    }
                } else {
                    showErrorMessage(getContext(), R.string.error_bad_response, response.message());
                }
            }

            @Override
            public void onFailure(Call<MovieReviewList> call, Throwable t) {
                showErrorMessage(getContext(), R.string.error_contacting_server, t.getMessage());
            }
        });
    }

    void showErrorMessage(Context context, int errorResId, String errorCondition) {
        String errorMessage = MessageFormat.format("{0}: {1}",
                getString(errorResId),
                errorCondition);

        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        if (BuildConfig.DEBUG) {
            Log.e(LOG_TAG, errorMessage);
        }
    }
}