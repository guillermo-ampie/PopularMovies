package com.ampie_guillermo.popularmovies.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.ampie_guillermo.popularmovies.BuildConfig;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.model.MovieReviewList;
import com.ampie_guillermo.popularmovies.model.MovieTrailerList;
import com.ampie_guillermo.popularmovies.model.MovieTrailerList.MovieTrailer;
import com.ampie_guillermo.popularmovies.network.MovieReviewService;
import com.ampie_guillermo.popularmovies.network.MovieTrailerService;
import com.ampie_guillermo.popularmovies.utils.MyPMErrorUtils;
import com.squareup.picasso.Picasso;
import java.text.NumberFormat;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * The fragment to display the movie's data.
 */
public class MovieDetailFragment
    extends Fragment
    implements MovieTrailerAdapter.MovieTrailerItemClickListener {

  static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

  // The BASE URL is the same for trailers & reviews
  private static final String MOVIEDB_TRAILER_BASE_URL = "https://api.themoviedb.org";
  /**
   * Just avoid creating the RETROFIT object with every instantiation of the
   * MovieDetailFragment object (singleton pattern: eager initialization)
   */
  private static final Retrofit RETROFIT
      = new Retrofit.Builder()
      .baseUrl(MOVIEDB_TRAILER_BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build();

  MovieTrailerList mTrailers;
  RecyclerView mRvMovieTrailers;
  MovieTrailerAdapter mMovieTrailerAdapter;

  MovieReviewList mReviews;
  RecyclerView mRvMovieReviews;
  MovieReviewAdapter mMovieReviewAdapter;

  View mRootView;
  private Call<MovieTrailerList> mCallTrailers;
  private Call<MovieReviewList> mCallReviews;

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
    Movie selectedMovie = intent.getExtras().getParcelable(getString(R.string.selected_movie));
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

      // Rating & Votes values will be formatted based on the current locale's properties
      NumberFormat numberFormat = NumberFormat.getNumberInstance();
      numberFormat.setMaximumFractionDigits(1);
      String rating = numberFormat.format(selectedMovie.getVoteAverage());

      tv = mRootView.findViewById(R.id.rating_text);
      tv.setText(rating);

      // Format the number using the current's locale grouping
      numberFormat.setGroupingUsed(true);
      String votes = numberFormat.format(selectedMovie.getVoteCount());
      tv = mRootView.findViewById(R.id.vote_count_text);
      tv.setText(votes);

      tv = mRootView.findViewById(R.id.movie_overview_text);
      tv.setText(selectedMovie.getOverview());

      // Get the movie trailers
      fetchTrailers(selectedMovie);

      // Get the movie reviews
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
    if (mCallTrailers != null) {
      mCallTrailers.cancel();
    }

    if (mCallReviews != null) {
      mCallReviews.cancel();
    }
  }

  private void fetchTrailers(Movie selectedMovie) {

    // Get a reference to the Trailer's RecyclerView
    mRvMovieTrailers = mRootView.findViewById(R.id.rv_trailers);

    // Set an -empty- adapter because the trailers have not been fetched
    mMovieTrailerAdapter = new MovieTrailerAdapter(this);
    mRvMovieTrailers.setAdapter(mMovieTrailerAdapter);

    // We will show the movie trailers in just one row
    mRvMovieTrailers.setLayoutManager(new LinearLayoutManager(getContext(),
        LinearLayoutManager.HORIZONTAL,
        false));
    mRvMovieTrailers.setHasFixedSize(true);

    // Create an instance of our MovieTrailerService.
    MovieTrailerService movieTrailerService = RETROFIT.create(MovieTrailerService.class);

    // Create a call instance for looking up the movie's list of trailers
    mCallTrailers = movieTrailerService.get(selectedMovie.getId(), BuildConfig.MOVIE_DB_API_KEY);

    // Fetch the trailers
    mCallTrailers.enqueue(new Callback<MovieTrailerList>() {
      @Override
      public void onResponse(Call<MovieTrailerList> call, Response<MovieTrailerList> response) {
        if (response.isSuccessful()) {
          // Here we get the movie trailer list!
          mTrailers = response.body();

          if (mTrailers.getTrailerList().isEmpty()) {
            // Show "No trailers" text
            TextView tvNoTrailers = mRootView.findViewById(R.id.tv_no_trailers);
            tvNoTrailers.setVisibility(View.VISIBLE);
            // Hide the RecyclerView that contains the movie trailers
            mRvMovieTrailers.setVisibility(View.GONE);
          } else {
            // Set the data(trailers) we have just fetched
            mMovieTrailerAdapter.setMovieTrailerList(mTrailers);
          }
        } else {
          MyPMErrorUtils.showErrorMessage(LOG_TAG,
              getContext(),
              R.string.error_bad_response,
              response.message());
        }
      }

      @Override
      public void onFailure(Call<MovieTrailerList> call, Throwable t) {
        MyPMErrorUtils.showErrorMessage(LOG_TAG,
            getContext(),
            R.string.error_contacting_server,
            t.getMessage());
      }
    });
  }

  private void fetchReviews(Movie selectedMovie) {

    // Get a reference to the Trailer's RecyclerView
    mRvMovieReviews = mRootView.findViewById(R.id.rv_reviews);

    // Set an -empty- adapter because the reviews have not been fetched
    mMovieReviewAdapter = new MovieReviewAdapter();
    mRvMovieReviews.setAdapter(mMovieReviewAdapter);

    // We will show the movie trailers in just one row
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
        LinearLayoutManager.HORIZONTAL,
        false);
    mRvMovieReviews.setLayoutManager(layoutManager);

    // Set a divider line
    DividerItemDecoration dividerLine
        = new DividerItemDecoration(mRvMovieReviews.getContext(),
        layoutManager.getOrientation());
    mRvMovieReviews.addItemDecoration(dividerLine);

    // Create an instance of our MovieReviewService.
    MovieReviewService movieReviewService = RETROFIT.create(MovieReviewService.class);

    // Create a call instance for looking up the movie's list of reviews
    mCallReviews = movieReviewService.get(selectedMovie.getId(),
        BuildConfig.MOVIE_DB_API_KEY);

    // Fetch the Reviews
    mCallReviews.enqueue(new Callback<MovieReviewList>() {
      @Override
      public void onResponse(Call<MovieReviewList> call, Response<MovieReviewList> response) {
        if (response.isSuccessful()) {
          // Here we get the movie review list!
          mReviews = response.body();

          if (mReviews.getReviewList().isEmpty()) {
            // Show "No reviews" text
            TextView tvNoReviews = mRootView.findViewById(R.id.tv_no_reviews);
            tvNoReviews.setVisibility(View.VISIBLE);
            // Hide the RecyclerView that contains the movie reviews
            mRvMovieReviews.setVisibility(View.GONE);
          } else {
            // Set the data(reviews) we have just fetched
            mMovieReviewAdapter.setMovieReviewList(mReviews);
          }
        } else {
          MyPMErrorUtils.showErrorMessage(LOG_TAG,
              getContext(),
              R.string.error_bad_response,
              response.message());
        }
      }

      @Override
      public void onFailure(Call<MovieReviewList> call, Throwable t) {
        MyPMErrorUtils.showErrorMessage(LOG_TAG,
            getContext(),
            R.string.error_contacting_server,
            t.getMessage());
      }
    });
  }

  @Override
  public void onMovieTrailerItemClick(int clickedItemIndex) {

    final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?";
    final String VIDEO_PARAM = "v";
    final ArrayList<MovieTrailer> trailerList = mTrailers.getTrailerList();

    MyPMErrorUtils.validateIndexInCollection(clickedItemIndex, trailerList.size());
    final MovieTrailer trailer = trailerList.get(clickedItemIndex);
    final Uri trailerUri = Uri.parse(YOUTUBE_BASE_URL)
        .buildUpon()
        .appendQueryParameter(VIDEO_PARAM, trailer.getKey())
        .build();

    // Play the movie trailer on youtube.com
    // TODO: Expand code to play trailer in youtube app if installed
    startActivity(new Intent(Intent.ACTION_VIEW, trailerUri));
  }
}