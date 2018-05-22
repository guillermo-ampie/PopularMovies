package com.ampie_guillermo.popularmovies.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.ampie_guillermo.popularmovies.network.MovieReviewService;
import com.ampie_guillermo.popularmovies.network.MovieTrailerService;
import com.ampie_guillermo.popularmovies.ui.adapter.MovieReviewAdapter;
import com.ampie_guillermo.popularmovies.ui.adapter.MovieTrailerAdapter;
import com.ampie_guillermo.popularmovies.utils.MyPMErrorUtils;
import com.squareup.picasso.Picasso;
import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;
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
  private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org";
  private static final String MOVIE_TRAILER_LIST = "movie_trailer_list";
  private static final String MOVIE_REVIEW_LIST = "movie_review_list";
  /**
   * Just avoid creating the RETROFIT object with every instantiation of the
   * MovieDetailFragment object (singleton pattern: eager initialization)
   */
  private static final Retrofit RETROFIT =
      new Retrofit.Builder()
          .baseUrl(MOVIEDB_BASE_URL)
          .addConverterFactory(GsonConverterFactory.create())
          .build();

  MovieTrailerList mTrailers;
  MovieReviewList mReviews;
  View mRootView;
  private RecyclerView mRvMovieTrailers;
  private MovieTrailerAdapter mMovieTrailerAdapter;
  private RecyclerView mRvMovieReviews;
  private MovieReviewAdapter mMovieReviewAdapter;
  private Call<MovieTrailerList> mCallTrailers;
  private Call<MovieReviewList> mCallReviews;

  public MovieDetailFragment() {
    mTrailers = new MovieTrailerList();
    mReviews = new MovieReviewList();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mRootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      // "ScrollIndicators" attribute is available since "Marshmallow (API 23)"
      ScrollView sv = mRootView.findViewById(R.id.scroll_movie_detail_main);
      sv.setScrollIndicators(View.SCROLL_INDICATOR_RIGHT);
    }

    final Intent intent = Objects.requireNonNull(getActivity()).getIntent();

    // Get the selected movie passed by Intent
    final Movie selectedMovie = Objects
        .requireNonNull(intent.getExtras())
        .getParcelable(getString(R.string.selected_movie));
    if (selectedMovie != null) {
      // we will reuse -tv- variable for all the TextView objects in this fragment
      TextView tv = mRootView.findViewById(R.id.text_movie_detail_title);
      tv.setText(selectedMovie.getOriginalTitle());

      final ImageView moviePosterView = mRootView.findViewById(R.id.image_movie_detail_poster);

      // Show the movie poster

      // See comment in MovieAdapter::setupItemView to allow vector drawables in
      // API level < 21 (Lollipop)
      final Drawable placeholderDrawable = ResourcesCompat
          .getDrawable(this.getResources(), R.drawable.ic_movie_black_237x180dp, null);
      final Drawable placeholderDrawableError = ResourcesCompat
          .getDrawable(this.getResources(), R.drawable.ic_broken_image_black_237x180dp, null);
//      Picasso.with(getContext())
      Picasso.get()
          .load(selectedMovie.getPosterUri())
          .placeholder(placeholderDrawable)
          .error(placeholderDrawableError)
          .into(moviePosterView);

      tv = mRootView.findViewById(R.id.text_movie_detail_release_date_content);
      tv.setText(selectedMovie.getReleaseDate());

      // Rating & Votes values will be formatted based on the current locale's properties
      NumberFormat numberFormat = NumberFormat.getNumberInstance();
      numberFormat.setMaximumFractionDigits(1);
      final String rating = numberFormat.format(selectedMovie.getVoteAverage());

      tv = mRootView.findViewById(R.id.text_movie_detail_rating_content);
      tv.setText(rating);

      // Format the number using the current's locale grouping
      numberFormat.setGroupingUsed(true);
      final String votes = numberFormat.format(selectedMovie.getVoteCount());
      tv = mRootView.findViewById(R.id.text_movie_detail_vote_count_content);
      tv.setText(votes);

      tv = mRootView.findViewById(R.id.text_movie_detail_overview_content);
      tv.setText(selectedMovie.getOverview());

      /** Although the following (commented) code is correct, there is a workaround to all this,
       * that allow us to use drawables directly from XML in TextViews (in pre Lollipop API) using
       * a "selector":  wrap up the vector drawable in a selector file and you can specify
       * "android:drawableStart=@drawable/drawable_wrapper" attribute (and related) in the
       * TextView xml specification.
       *  Note: See comment in MovieAdapter::MovieViewHolder
       *  Reference: https://stackoverflow.com/questions/35761636/
       *  is-it-possible-to-use-vectordrawable-in-buttons-and
       *  -textviews-using-androiddraw/40250753#40250753
       *  See entry from: https://stackoverflow.com/users/4513962/amit-tumkur
       *  answered on Jan 8 at 8:28
       */
      // The following hack is to allow the use of vector drawables directly in TextViews in API
      // level < 21 (Lollipop), see comment in MovieAdapter::setupItemView. The code here is to
      // support vector drawables in TextViews (Compound Text Drawables) because CTD are not
      // working anymore directly from XML in pre Lollipop, i.e: "android:drawableStart" and
      // related attributes are not working anymore

      // Begin of hack
      // Set ic_message_black_36dp drawable
//      final Drawable messageDrawable =
//          AppCompatResources.getDrawable(getActivity(), R.drawable.ic_message_black_36dp);
//      final TextView textMovieOverview = mRootView.findViewById(R.id.text_movie_detail_overview);
//      final int drawableToTextPadding =
//          (int) getActivity().getResources().getDimension(R.dimen.drawable_to_text_padding);
//      textMovieOverview.setCompoundDrawablesWithIntrinsicBounds(messageDrawable,
//          null,
//          null,
//          null);
//      textMovieOverview.setCompoundDrawablePadding(drawableToTextPadding);
      // End of hack

      // Get the movie trailers
      fetchTrailers(selectedMovie, savedInstanceState);

      // Get the movie reviews
      fetchReviews(selectedMovie, savedInstanceState);
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

  private void fetchTrailers(Movie selectedMovie, Bundle savedInstanceState) {

    // Get a reference to the Trailer's RecyclerView
    mRvMovieTrailers = mRootView.findViewById(R.id.recycler_movie_detail_trailers);

    // Set an -empty- adapter because the trailers have not been fetched
    mMovieTrailerAdapter = new MovieTrailerAdapter(this);
    mRvMovieTrailers.setAdapter(mMovieTrailerAdapter);

    // We will show the movie trailers in just one row
    mRvMovieTrailers.setLayoutManager(new LinearLayoutManager(getContext(),
        LinearLayoutManager.HORIZONTAL,
        false));
    mRvMovieTrailers.setHasFixedSize(true);

    if (savedInstanceState != null) {
      // We already have the trailers list
      mTrailers = savedInstanceState.getParcelable(MOVIE_TRAILER_LIST);
      setupTrailersView();
    } else {
      // We have to fetch the trailers
      final MovieTrailerService movieTrailerService = RETROFIT.create(MovieTrailerService.class);

      // Create a call instance for looking up the movie's list of trailers
      mCallTrailers = movieTrailerService.get(selectedMovie.getId(), BuildConfig.MOVIE_DB_API_KEY);

      // Fetch the trailers
      mCallTrailers.enqueue(new Callback<MovieTrailerList>() {
        @Override
        public void onResponse(Call<MovieTrailerList> call, Response<MovieTrailerList> response) {
          if (response.isSuccessful()) {
            // Here we get the movie trailer list!
            mTrailers = response.body();
            setupTrailersView();
          } else {
            MyPMErrorUtils.showErrorMessage(LOG_TAG,
                Objects.requireNonNull(getActivity()),
                R.string.error_bad_response,
                response.message());
          }
        }

        @Override
        public void onFailure(Call<MovieTrailerList> call, Throwable t) {
          //TODO: in onFailure() a call to getContext() or getActivity() can return null.
          // When does this happen ?
          Log.v(LOG_TAG, "++++++++++ onFailure");
          MyPMErrorUtils.showErrorMessage(LOG_TAG,
//              getContext(),
//              getActivity(),
              mRootView.getContext(),
              R.string.error_contacting_server,
              t.getMessage());
        }
      });
    }
  }

  void setupTrailersView() {
    if (mTrailers.getTrailerList().isEmpty()) {
      // Show "No trailers" text
      TextView tvNoTrailers = mRootView.findViewById(R.id.text_movie_detail_no_trailers);
      tvNoTrailers.setVisibility(View.VISIBLE);
      // Hide the RecyclerView that contains the movie trailers
      mRvMovieTrailers.setVisibility(View.GONE);
    } else {
      // Set the data(trailers) we have just fetched
      mMovieTrailerAdapter.setMovieTrailerList(mTrailers);
    }
  }


  private void fetchReviews(Movie selectedMovie, Bundle savedInstanceState) {

    // Get a reference to the Trailer's RecyclerView
    mRvMovieReviews = mRootView.findViewById(R.id.recycler_movie_detail_reviews);

    // Set an -empty- adapter because the reviews have not been fetched
    mMovieReviewAdapter = new MovieReviewAdapter();
    mRvMovieReviews.setAdapter(mMovieReviewAdapter);

    // We will show the movie trailers in just one row
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
        LinearLayoutManager.HORIZONTAL,
        false);
    mRvMovieReviews.setLayoutManager(layoutManager);

    // Set a divider line
    final DividerItemDecoration dividerLine
        = new DividerItemDecoration(mRvMovieReviews.getContext(),
        layoutManager.getOrientation());
    mRvMovieReviews.addItemDecoration(dividerLine);

    if (savedInstanceState != null) {
      // We already have the reviews list
      mReviews = savedInstanceState.getParcelable(MOVIE_REVIEW_LIST);
      setupReviewsView();
    } else {
      // We have to fetch the trailers
      final MovieReviewService movieReviewService = RETROFIT.create(MovieReviewService.class);

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
            setupReviewsView();
          } else {
            MyPMErrorUtils.showErrorMessage(LOG_TAG,
                Objects.requireNonNull(getActivity()),
                R.string.error_bad_response,
                response.message());
          }
        }

        @Override
        public void onFailure(Call<MovieReviewList> call, Throwable t) {
          Log.v(LOG_TAG, "++++++++++ onFailure");
          MyPMErrorUtils.showErrorMessage(LOG_TAG,
//              getContext()
//              getActivity(),
              mRootView.getContext(),
              R.string.error_contacting_server,
              t.getMessage());
        }
      });
    }
  }

  void setupReviewsView() {
    List<MovieReviewList.MovieReview> reviewList = mReviews.getReviewList();

    if (reviewList.isEmpty()) {
      // Show "No reviews" text
      final TextView tvNoReviews = mRootView.findViewById(R.id.text_movie_detail_no_reviews);
      tvNoReviews.setVisibility(View.VISIBLE);
      // Hide the RecyclerView that contains the movie reviews
      mRvMovieReviews.setVisibility(View.GONE);
    } else {
      final TextView textReviewsTitle = mRootView.findViewById(R.id.text_movie_detail_reviews);
      final String totalReviews =
          "("
              + String.valueOf(reviewList.size())
              + ") "
              + getResources().getString(R.string.movie_detail_reviews);
      textReviewsTitle.setText(totalReviews);
      // Set the data(reviews) we have just fetched
      mMovieReviewAdapter.setMovieReviewList(mReviews);
    }
  }

  @Override
  public void onMovieTrailerItemClick(int clickedItemIndex) {

    final List<MovieTrailerList.MovieTrailer> trailerList = mTrailers.getTrailerList();

    MyPMErrorUtils.validateIndexInCollection(clickedItemIndex, trailerList.size());
    final MovieTrailerList.MovieTrailer trailer = trailerList.get(clickedItemIndex);
    final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?";
    final String VIDEO_PARAM = "v";
    final Uri trailerUri = Uri.parse(YOUTUBE_BASE_URL)
        .buildUpon()
        .appendQueryParameter(VIDEO_PARAM, trailer.getKey())
        .build();

    // Play the movie trailer on youtube.com
    // TODO: Expand code to play trailer in youtube app if installed
    startActivity(new Intent(Intent.ACTION_VIEW, trailerUri));
  }

  /**
   * Called to ask the fragment to save its current dynamic state, so it
   * can later be reconstructed in a new instance of its process is
   * restarted.  If a new instance of the fragment later needs to be
   * created, the data you place in the Bundle here will be available
   * in the Bundle given to {@link #onCreate(Bundle)},
   * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
   * {@link #onActivityCreated(Bundle)}.
   *
   * <p>This corresponds to {link Activity#onSaveInstanceState(Bundle outState)
   * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
   * applies here as well.  Note however: <em>this method may be called
   * at any time before {@link #onDestroy()}</em>.  There are many situations
   * where a fragment may be mostly torn down (such as when placed on the
   * back stack with no UI showing), but its state will not be saved until
   * its owning activity actually needs to save its state.
   *
   * @param outState Bundle in which to place your saved state.
   */
  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putParcelable(MOVIE_TRAILER_LIST, mTrailers);
    outState.putParcelable(MOVIE_REVIEW_LIST, mReviews);
  }
}

