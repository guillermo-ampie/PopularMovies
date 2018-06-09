package com.ampie_guillermo.popularmovies.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.ampie_guillermo.popularmovies.BuildConfig;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.databinding.FragmentMovieDetailBinding;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.model.MovieReviewList;
import com.ampie_guillermo.popularmovies.model.MovieTrailerList;
import com.ampie_guillermo.popularmovies.network.MovieReviewService;
import com.ampie_guillermo.popularmovies.network.MovieTrailerService;
import com.ampie_guillermo.popularmovies.ui.adapter.MovieReviewAdapter;
import com.ampie_guillermo.popularmovies.ui.adapter.MovieTrailerAdapter;
import com.ampie_guillermo.popularmovies.utils.MyPMErrorUtils;
import com.ampie_guillermo.popularmovies.utils.VectorAnimationSelectWithPath;
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
    implements
    MovieTrailerAdapter.MovieTrailerItemClickListener,
    VectorAnimationSelectWithPath.OnSelectedEventListener {

  static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

  // The BASE URL is the same for trailers & reviews
  private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org";
  private static final String MOVIE_TRAILER_LIST = "movie_trailer_list";
  private static final String MOVIE_REVIEW_LIST = "movie_review_list";

  private static final long ANIMATION_DURATION = 1000L;
  /**
   * Just avoid creating the RETROFIT object with every instantiation of the
   * MovieDetailFragment object (singleton pattern: eager initialization).
   */
  private static final Retrofit RETROFIT =
      new Retrofit.Builder()
          .baseUrl(MOVIEDB_BASE_URL)
          .addConverterFactory(GsonConverterFactory.create())
          .build();

  MovieTrailerList mTrailers;
  MovieReviewList mReviews;
  FragmentMovieDetailBinding binding;

  private MovieTrailerAdapter mMovieTrailerAdapter;
  private MovieReviewAdapter mMovieReviewAdapter;
  private Call<MovieTrailerList> mCallTrailers;
  private Call<MovieReviewList> mCallReviews;

  public MovieDetailFragment() {
    mTrailers = new MovieTrailerList();
    mReviews = new MovieReviewList();
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {

    binding = FragmentMovieDetailBinding.inflate(inflater, container, false);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      // "ScrollIndicators" attribute is available since "Marshmallow (API 23)"
      binding.scrollMovieDetailMain.setScrollIndicators(View.SCROLL_INDICATOR_RIGHT);
    }

    final Intent intent = Objects.requireNonNull(getActivity()).getIntent();

    // Get the selected movie passed by Intent
    final Movie selectedMovie = Objects
        .requireNonNull(intent.getExtras())
        .getParcelable(getString(R.string.selected_movie));
    if (selectedMovie != null) {
      final Resources resources = getResources();
      binding.textMovieDetailTitle.setText(selectedMovie.getOriginalTitle());

      // Show the movie poster

      // See comment in MovieAdapter::setupItemView to allow vector drawables in
      // API level < 21 (Lollipop)
      final Drawable placeholderDrawable = ResourcesCompat
          .getDrawable(resources, R.drawable.ic_movie_black_237x180dp, null);
      final Drawable placeholderDrawableError = ResourcesCompat
          .getDrawable(resources, R.drawable.ic_broken_image_black_237x180dp, null);
//      Picasso.with(getContext())
      Picasso.get()
          .load(selectedMovie.getPosterUri())
          .placeholder(placeholderDrawable)
          .error(placeholderDrawableError)
          .into(binding.imageMovieDetailPoster);

      // Set the movie release date
      binding.textMovieDetailReleaseDateContent.setText(selectedMovie.getReleaseDate());

      // Rating & Votes values will be formatted based on the current locale's properties
      final NumberFormat numberFormat = NumberFormat.getNumberInstance();
      numberFormat.setMaximumFractionDigits(1);
      final String rating = numberFormat.format((double) selectedMovie.getVoteAverage());

      // Set the movie rating
      binding.textMovieDetailRatingContent.setText(rating);

      // Format the number using the current's locale grouping
      numberFormat.setGroupingUsed(true);
      final String votes = numberFormat.format((long) selectedMovie.getVoteCount());

      // Set the movie votes
      binding.textMovieDetailVoteCountContent.setText(votes);

      // Set the movie overview
      binding.textMovieDetailOverviewContent.setText(selectedMovie.getOverview());

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

      final int startColor = resources.getColor(R.color.white);
      final int endColor = resources.getColor(R.color.red);
      final boolean isSelected = true;
      final VectorAnimationSelectWithPath vectorAnimation =
          new VectorAnimationSelectWithPath(binding.vectorMasterMovieDetailHeart,
              getString(R.string.movie_detail_vector_path_name),
              startColor,
              endColor);
      vectorAnimation.registerOnSelectedEventListener(this);
      vectorAnimation.setStrokeColor(endColor);
      vectorAnimation.setSelected(isSelected);
      vectorAnimation.startAnimation(ANIMATION_DURATION);

      // Get the movie trailers
      fetchTrailers(selectedMovie, savedInstanceState);

      // Get the movie reviews
      fetchReviews(selectedMovie, savedInstanceState);
    }

    return binding.getRoot();
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

  private void fetchTrailers(final Movie selectedMovie, final Bundle savedInstanceState) {
    // Set an -empty- adapter because the trailers have not been fetched
    mMovieTrailerAdapter = new MovieTrailerAdapter(this);
    binding.recyclerMovieDetailTrailers.setAdapter(mMovieTrailerAdapter);

    // We will show the movie trailers in just one row
    binding.recyclerMovieDetailTrailers.setLayoutManager(new LinearLayoutManager(getContext(),
        LinearLayoutManager.HORIZONTAL,
        false));
    binding.recyclerMovieDetailTrailers.setHasFixedSize(true);

    if (savedInstanceState != null) {
      // We already have the trailers list, retrieve it and show it
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
              binding.getRoot().getContext(),
              R.string.error_contacting_server,
              t.getMessage());
        }
      });
    }
  }

  void setupTrailersView() {
    if (mTrailers.getTrailerList().isEmpty()) {
      // We got no trailers, show "No trailers" text
      binding.textMovieDetailNoTrailers.setVisibility(View.VISIBLE);

      // Hide the RecyclerView that shows the movie trailers
      binding.recyclerMovieDetailTrailers.setVisibility(View.GONE);
    } else {
      // Set the data(trailers) we have just fetched
      mMovieTrailerAdapter.setMovieTrailerList(mTrailers);
    }
  }

  private void fetchReviews(final Movie selectedMovie, final Bundle savedInstanceState) {
    // Set an -empty- adapter because the reviews have not been fetched
    mMovieReviewAdapter = new MovieReviewAdapter();
    binding.recyclerMovieDetailReviews.setAdapter(mMovieReviewAdapter);

    // We will show the movie trailers in just one row
    final LinearLayoutManager layoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    binding.recyclerMovieDetailReviews.setLayoutManager(layoutManager);

    // Set a divider line
    final DividerItemDecoration dividerLine =
        // TODO: check getContext() call
        new DividerItemDecoration(binding.recyclerMovieDetailReviews.getContext(),
            layoutManager.getOrientation());
    binding.recyclerMovieDetailReviews.addItemDecoration(dividerLine);

    if (savedInstanceState != null) {
      // We already have the reviews list, retrieve it and show it
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
              // getContext() and getActivity() could be null
//              getContext()
//              getActivity(),
              binding.getRoot().getContext(),
              R.string.error_contacting_server,
              t.getMessage());
        }
      });
    }
  }

  void setupReviewsView() {
    final List<MovieReviewList.MovieReview> reviewList = mReviews.getReviewList();

    if (reviewList.isEmpty()) {
      // We got no reviews, show "No reviews" text
      binding.textMovieDetailNoReviews.setVisibility(View.VISIBLE);

      // Hide the RecyclerView that shows the movie reviews
      binding.recyclerMovieDetailReviews.setVisibility(View.GONE);
    } else {
      // We got reviews, show them and their total number
      final String totalReviews =
          '(' + String.valueOf(reviewList.size()) + ") "
              + getResources().getString(R.string.movie_detail_reviews);
      binding.textMovieDetailReviews.setText(totalReviews);
      // Set the data(reviews) we have just fetched
      mMovieReviewAdapter.setMovieReviewList(mReviews);
    }
  }

  @Override
  public void onMovieTrailerItemClick(final int clickedItemIndex) {
    final List<MovieTrailerList.MovieTrailer> trailerList = mTrailers.getTrailerList();

    MyPMErrorUtils.validateIndexInCollection(clickedItemIndex, trailerList.size());
    final MovieTrailerList.MovieTrailer trailer = trailerList.get(clickedItemIndex);
    final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?";
    final String VIDEO_PARAM = "v";
    final Uri trailerUri =
        Uri.parse(YOUTUBE_BASE_URL)
            .buildUpon()
            .appendQueryParameter(VIDEO_PARAM, trailer.getKey())
            .build();

    // Play the movie trailer on youtube.com
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

  @Override
  public void onSelected(boolean isSelected) {
    final String message =
        isSelected ? getString(R.string.movie_detail_add_movie)
            : getString(R.string.movie_detail_remove_movie);
    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
  }
}

