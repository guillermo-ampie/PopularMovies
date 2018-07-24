package com.ampie_guillermo.popularmovies.ui;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Toast;
import com.ampie_guillermo.popularmovies.BuildConfig;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.database.MovieColumns;
import com.ampie_guillermo.popularmovies.database.MovieReviewColumns;
import com.ampie_guillermo.popularmovies.database.MovieTrailerColumns;
import com.ampie_guillermo.popularmovies.database.MoviesProvider;
import com.ampie_guillermo.popularmovies.databinding.FragmentMovieDetailBinding;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.model.MovieReviewList;
import com.ampie_guillermo.popularmovies.model.MovieTrailerList;
import com.ampie_guillermo.popularmovies.network.MovieReviewService;
import com.ampie_guillermo.popularmovies.network.MovieTrailerService;
import com.ampie_guillermo.popularmovies.ui.adapter.MovieReviewAdapter;
import com.ampie_guillermo.popularmovies.ui.adapter.MovieTrailerAdapter;
import com.ampie_guillermo.popularmovies.utils.DrawablePlaceholderSingleton;
import com.ampie_guillermo.popularmovies.utils.MyPMErrorUtils;
import com.ampie_guillermo.popularmovies.utils.VectorAnimationSelectWithPath;
import com.squareup.picasso.Picasso;
import java.text.NumberFormat;
import java.util.ArrayList;
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
    implements LoaderManager.LoaderCallbacks<Cursor>,
    MovieTrailerAdapter.MovieTrailerItemClickListener,
    VectorAnimationSelectWithPath.OnSelectedEventListener {

  protected static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
  // Ids for CursorLoaders
  private static final int SELECTED_MOVIE_LOADER_ID = 1200;
  private static final int TRAILERS_LOADER_ID = 1300;
  private static final int REVIEWS_LOADER_ID = 1400;
  // The BASE URL is the same for trailers & reviews
  private static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org";
  // Keys for Bundle args
  private static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";
  // Keys for bundles
  private static final String BUNDLE_IS_MOVIE_FAVOURITE = "BUNDLE_IS_MOVIE_FAVOURITE";
  private static final String BUNDLE_MOVIE_TRAILER_LIST = "BUNDLE_MOVIE_TRAILER_LIST";
  private static final String BUNDLE_MOVIE_REVIEW_LIST = "BUNDLE_MOVIE_REVIEW_LIST";
  private static final long ANIMATION_DURATION = 1000L;
  /**
   * Just avoid creating the RETROFIT object with every instantiation of the
   * MovieDetailFragment object (singleton pattern: eager initialization).
   */
  private static final Retrofit RETROFIT =
      new Retrofit.Builder()
          .baseUrl(MOVIE_DB_BASE_URL)
          .addConverterFactory(GsonConverterFactory.create())
          .build();
  protected Movie selectedMovie;
  protected boolean isFavourite;
  protected MovieTrailerList mTrailers;
  protected MovieReviewList mReviews;
  protected FragmentMovieDetailBinding binding;
  private MovieTrailerAdapter mMovieTrailerAdapter;
  private MovieReviewAdapter mMovieReviewAdapter;
  private Call<MovieTrailerList> mCallTrailers;
  private Call<MovieReviewList> mCallReviews;


  public MovieDetailFragment() {
    mTrailers = new MovieTrailerList();
    mReviews = new MovieReviewList();
  }

  protected static ContentProviderResult[] deleteMovieBatch(final ContentResolver resolver,
      final String movieId)
      throws RemoteException, OperationApplicationException {

    // Using a batch of ContentProvider operations for better performance
    final ArrayList<ContentProviderOperation> deleteOperations = new ArrayList<>();

    // Setup operation to delete the trailers
    deleteOperations.add(ContentProviderOperation
        .newDelete(MoviesProvider.MovieTrailers.fromMovie(movieId))
        .withYieldAllowed(true)
        .build());

    // Setup operation to delete the reviews
    deleteOperations.add(ContentProviderOperation
        .newDelete(MoviesProvider.MovieReviews.fromMovie(movieId))
        .withYieldAllowed(true)
        .build());

    // Setup operation to delete the movie: because of referential integrity we must delete the
    // movie at the last
    deleteOperations.add(ContentProviderOperation
        .newDelete(MoviesProvider.Movies.withId(movieId))
        .withYieldAllowed(true)
        .build());

    // Execute the operations
    return resolver.applyBatch(MoviesProvider.AUTHORITY, deleteOperations);
  }

  private static ArrayList<MovieTrailerList.MovieTrailer> buildTrailerList(
      @NonNull final Cursor cursor) {
    // Build the list of trailers from DB
    final int TRAILER_KEY_INDEX = 0;
    final int TRAILER_NAME_INDEX = 1;
    final ArrayList<MovieTrailerList.MovieTrailer> trailerList = new ArrayList<>(cursor.getCount());

    while (cursor.moveToNext()) {
      final String trailerKey = cursor.getString(TRAILER_KEY_INDEX);
      final String trailerName = cursor.getString(TRAILER_NAME_INDEX);

      trailerList.add(new MovieTrailerList.MovieTrailer(trailerKey, trailerName));
    }
    return trailerList;
  }

  private static ArrayList<MovieReviewList.MovieReview> buildReviewList(
      @NonNull final Cursor cursor) {
    // Build the list of reviews from DB
    final int REVIEW_ID_INDEX = 0;
    final int REVIEW_AUTHOR_INDEX = 1;
    final int REVIEW_CONTENT_INDEX = 2;
    final ArrayList<MovieReviewList.MovieReview> reviewList = new ArrayList<>(cursor.getCount());

    while (cursor.moveToNext()) {
      final String reviewId = cursor.getString(REVIEW_ID_INDEX);
      final String reviewAuthor = cursor.getString(REVIEW_AUTHOR_INDEX);
      final String reviewContent = cursor.getString(REVIEW_CONTENT_INDEX);

      reviewList.add(new MovieReviewList.MovieReview(reviewId, reviewAuthor, reviewContent));
    }
    return reviewList;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {

    binding = FragmentMovieDetailBinding.inflate(inflater, container, false);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      // "ScrollIndicators" attribute is available since "Marshmallow (API 23)"
      binding.scrollMovieDetailMain.setScrollIndicators(View.SCROLL_INDICATOR_RIGHT);
    }

    final Intent intent = Objects.requireNonNull(getActivity()).getIntent();

    // Get the selected movie passed by Intent
    selectedMovie = Objects
        .requireNonNull(intent.getExtras())
        .getParcelable(getString(R.string.EXTRA_SELECTED_MOVIE));
    if (selectedMovie != null) {
      final Resources resources = getResources();
      binding.textMovieDetailTitle.setText(selectedMovie.getOriginalTitle());

      // See comment in MovieAdapter::setupItemView to allow vector drawables in
      // API level < 21 (Lollipop)

      // Show the movie poster
      final DrawablePlaceholderSingleton placeholders =
          DrawablePlaceholderSingleton.getInstance(resources);
      Picasso.get()
          .load(selectedMovie.getPosterUri())
          .placeholder(placeholders.getDrawablePlaceHolder())
          .error(placeholders.getDrawableErrorPlaceholder())
          .into(binding.imageMovieDetailPoster);

      // Set the movie release date
      binding.textMovieDetailReleaseDateContent.setText(selectedMovie.getReleaseDate());

      // Rating & Votes values will be formatted based on the current locale's properties
      final NumberFormat numberFormat = NumberFormat.getNumberInstance();
      numberFormat.setMaximumFractionDigits(1);
      final String rating = numberFormat.format(selectedMovie.getVoteAverage());

      // Set the movie rating
      binding.textMovieDetailRatingContent.setText(rating);

      // Format the number using the current's locale grouping
      numberFormat.setGroupingUsed(true);
      final String votes = numberFormat.format(selectedMovie.getVoteCount());

      // Set the movie votes
      binding.textMovieDetailVoteCountContent.setText(votes);

      // Set the movie overview
      binding.textMovieDetailOverviewContent.setText(selectedMovie.getOverview());

      /*
        Although the following (commented) code is correct, there is a workaround to all this,
        that allow us to use drawables directly from XML in TextViews (in pre Lollipop API) using
        a "selector":  wrap up the vector drawable in a selector file and you can specify
        "android:drawableStart=@drawable/drawable_wrapper" attribute (and related) in the
        TextView xml specification.
        Note: See comment in MovieAdapter::MovieViewHolder
        Reference: https://stackoverflow.com/questions/35761636/
        is-it-possible-to-use-vectordrawable-in-buttons-and
        -textviews-using-androiddraw/40250753#40250753
        See entry from: https://stackoverflow.com/users/4513962/amit-tumkur
        answered on Jan 8 at 8:28
       */
      // The following hack is to allow the use of vector drawables directly in TextViews in API
      // level < 21 (Lollipop), see comment in DrawablePlaceholderSingleton. The code here is to
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

      // Set Trailers view: begin
      // Set an -empty- adapter because the trailers have not been fetched
      mMovieTrailerAdapter = new MovieTrailerAdapter(this);
      binding.recyclerMovieDetailTrailers.setAdapter(mMovieTrailerAdapter);

      // We will show the movie trailers in just one row
      binding.recyclerMovieDetailTrailers.setLayoutManager(new LinearLayoutManager(getContext(),
          LinearLayoutManager.HORIZONTAL,
          false));
      binding.recyclerMovieDetailTrailers.setHasFixedSize(true);
      // Set Trailers view: end

      // Set Reviews view: begin
      // Set an -empty- adapter because the reviews have not been fetched
      mMovieReviewAdapter = new MovieReviewAdapter();
      binding.recyclerMovieDetailReviews.setAdapter(mMovieReviewAdapter);

      // We will show the movie trailers in just one row
      final LinearLayoutManager layoutManager =
          new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
      binding.recyclerMovieDetailReviews.setLayoutManager(layoutManager);

      // Set a divider line
      final DividerItemDecoration dividerLine =
          // TODO: Review getContext() call
          new DividerItemDecoration(binding.recyclerMovieDetailReviews.getContext(),
              layoutManager.getOrientation());
      binding.recyclerMovieDetailReviews.addItemDecoration(dividerLine);
      // Set Reviews view: end

      if (savedInstanceState == null) {
        // From the Intent we have almost all the movie data but is missing: Favourite state,
        // trailers and reviews
        completeMovieData();
      } else {
        // There is a prior instance of the Fragment where we saved the selected movie's state,
        // its trailers and reviews, retrieve and show them

        mTrailers = savedInstanceState.getParcelable(BUNDLE_MOVIE_TRAILER_LIST);
        mReviews = savedInstanceState.getParcelable(BUNDLE_MOVIE_REVIEW_LIST);
        isFavourite = savedInstanceState.getBoolean(BUNDLE_IS_MOVIE_FAVOURITE);

        // Initialize the favourite button's state
        setupFavouriteButtonAnimation(getResources());

        showTrailersView();
        showReviewsView();
      }
    }
    return binding.getRoot();
  }

  private void completeMovieData() {

    // Get the trailers and reviews
    getLoaderManager().initLoader(SELECTED_MOVIE_LOADER_ID, null, this);
  }

  /**
   * Called when the Fragment is no longer started.  This is generally
   * tied to {@link Activity#onStop() Activity.onStop} of the containing
   * Activity's lifecycle.
   */
  @Override
  public void onStop() {
    super.onStop();
//    // Cancel the request if the HTTP scheduler has not executed it already...
//    if (mCallTrailers != null) {
//      mCallTrailers.cancel();
//    }
//
//    if (mCallReviews != null) {
//      mCallReviews.cancel();
//    }

    final Context context = Objects.requireNonNull(getActivity()).getApplicationContext();
    final ContentResolver resolver = context.getContentResolver();
    final String movieId = selectedMovie.getId();
    new Thread(new Runnable() {
      @Override
      public void run() {
        try (Cursor cursor = resolver
            .query(MoviesProvider.Movies.withId(movieId),
                new String[]{MovieColumns.MOVIE_ID},
                null,
                null,
                null)) {
          final boolean isMovieInDb = (cursor != null) && (cursor.getCount() > 0);
          // TODO: 7/4/18 The following DB operations are kind of inefficient, -all- the movies
          //       (popular / best rated / favourites) should be inserted into the DB and not only
          //       the favourite ones: Migrate to the complete scheme!!
          if (isFavourite) {
            if (!isMovieInDb) {
              // The movie finished selected as favourite and is not present in the DB --> insert it
              // into DB
              insertMovieBatch(resolver, movieId);
            }
            // Movie is selected as favourite and is already present in the DB --> do nothing
          } else {
            if (isMovieInDb) {
              // Movie finished as -not selected as Favourite- and is in the DB --> delete it
              deleteMovieBatch(resolver, movieId);
            }
            // Movie finished as -not selected as Favourite- and -is not in the DB- --> do nothing
          }
        } catch (SQLException | IllegalArgumentException | RemoteException
            | OperationApplicationException e) {
          MyPMErrorUtils.logErrorMessage(LOG_TAG, context, R.string.error_accessing_sqlite_db,
              e.getMessage());
        }
      }
    }).start();
  }

  /**
   * Called when the fragment is no longer in use.  This is called
   * after {@link #onStop()} and before {@link #onDetach()}.
   */
  @Override
  public void onDestroy() {
    // Cancel the request if the HTTP scheduler has not executed it already...
    if (mCallTrailers != null) {
      mCallTrailers.cancel();
    }
    if (mCallReviews != null) {
      mCallReviews.cancel();
    }

    super.onDestroy();
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

    outState.putParcelable(BUNDLE_MOVIE_TRAILER_LIST, mTrailers);
    outState.putParcelable(BUNDLE_MOVIE_REVIEW_LIST, mReviews);
    outState.putBoolean(BUNDLE_IS_MOVIE_FAVOURITE, isFavourite);
  }

  @Override
  public void onSelected(final boolean isSelected) {
    // Save new state
    isFavourite = isSelected;

    // The Favorite button's state recording is deferred until Fragment's onStop(), so the
    // "click-addicts" of the Favourite button will not generate a performance impact
    final String message =
        isSelected ? getString(R.string.movie_detail_add_movie)
            : getString(R.string.movie_detail_remove_movie);
    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
  }

  /**
   * Called when a previously created loader has finished its load.  Note
   * that normally an application is <em>not</em> allowed to commit fragment
   * transactions while in this call, since it can happen after an
   * activity's state is saved.  See {@link FragmentManager#beginTransaction()
   * FragmentManager.openTransaction()} for further discussion on this.
   *
   * <p>This function is guaranteed to be called prior to the release of
   * the last data that was supplied for this Loader.  At this point
   * you should remove all use of the old data (since it will be released
   * soon), but should not do your own release of the data since its Loader
   * owns it and will take care of that.  The Loader will take care of
   * management of its data so you don't have to.  In particular:
   *
   * <ul>
   * <li> <p>The Loader will monitor for changes to the data, and report
   * them to you through new calls here.  You should not monitor the
   * data yourself.  For example, if the data is a {@link Cursor}
   * and you place it in a {@link CursorAdapter}, use
   * the {@link CursorAdapter(Context, Cursor, int)} constructor <em>without</em> passing
   * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
   * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
   * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
   * from doing its own observing of the Cursor, which is not needed since
   * when a change happens you will get a new Cursor throw another call
   * here.
   * <li> The Loader will release the data once it knows the application
   * is no longer using it.  For example, if the data is
   * a {@link Cursor} from a {@link CursorLoader},
   * you should not call close() on it yourself.  If the Cursor is being placed in a
   * {@link CursorAdapter}, you should use the
   * {@link CursorAdapter#swapCursor(Cursor)}
   * method so that the old Cursor is not closed.
   * </ul>
   *
   * <p>This will always be called from the process's main thread.
   *
   * @param loader The Loader that has finished.
   * @param data The data generated by the Loader.
   */
  @Override
  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    final int loaderId = loader.getId();
    switch (loaderId) {

      case SELECTED_MOVIE_LOADER_ID:
        // If the Movie is in the DB --> it is a Favourite one
        final boolean isMovieInDb = (data != null) && (data.getCount() > 0);
        isFavourite = isMovieInDb;

        // Initialize the favourite button's state
        setupFavouriteButtonAnimation(getResources());

        // Get movie trailers & reviews
        if (isMovieInDb) {
          final Bundle bundle = new Bundle();

          bundle.putString(EXTRA_MOVIE_ID, selectedMovie.getId());
          fetchTrailersFromDb(bundle);
          fetchReviewsFromDb(bundle);
        } else {
          fetchTrailersFromNetwork(false);
          fetchReviewsFromNetwork(false);
        }
        break;

      case TRAILERS_LOADER_ID:
        // Set up the trailer list from DB
        mTrailers.setTrailerList(buildTrailerList(data));
        // Border case: It is possible that when the user selected the movie as Favourite,
        // there were no trailers and reviews (because of a network error) to save into the
        // database, so try now to get them from the network and save them into the DB
        if (mTrailers.getTrailerList().isEmpty()) {
          Log.v(LOG_TAG, "++++++++++ Now trying to get the trailers");
          fetchTrailersFromNetwork(true);

          // showTrailersView() is called inside fetchTrailersFromNetwork() because it must be
          // called when the network operations finishes
          break;
        }
        // Show the movie trailers
        showTrailersView();
        break;

      case REVIEWS_LOADER_ID:
        // Set the reviews from the DB
        mReviews.setReviewList(buildReviewList(data));
        // Border case: It is possible that when the user selected the movie as Favourite,
        // there were no trailers and reviews (because of a network error) to save into the
        // database, so try now to get them from the network and save them into the DB
        if (mReviews.getReviewList().isEmpty()) {
          Log.v(LOG_TAG, "++++++++++ Now trying to get the reviews");
          fetchReviewsFromNetwork(true);
          // showReviewsView() is called inside fetchReviewsFromNetwork() because it must be
          // called when the network operations finishes
          break;
        }
        // Show the movie reviews
        showReviewsView();
        break;

      default:
        throw new IllegalArgumentException(
            String.format("%s: %d", getString(R.string.error_unknown_loader_id), loaderId));
    }
  }

  /**
   * Instantiate and return a new Loader for the given ID.
   *
   * <p>This will always be called from the process's main thread.
   *
   * @param id The ID whose loader is to be created.
   * @param args Any arguments supplied by the caller.
   * @return Return a new Loader instance that is ready to start loading.
   */
  @NonNull
  @Override
  public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
    final String movieId;

    switch (id) {
      case SELECTED_MOVIE_LOADER_ID:
        return new CursorLoader(Objects.requireNonNull(getActivity()), // context
            MoviesProvider.Movies.withId(selectedMovie.getId()), // content URI
            new String[]{MovieColumns.MOVIE_ID}, // projection
            null, // selection
            null, // selection args
            null); // sort order

      case TRAILERS_LOADER_ID:
        movieId = Objects.requireNonNull(args).getString(EXTRA_MOVIE_ID);
        return new CursorLoader(Objects.requireNonNull(getActivity()), // context
            MoviesProvider.MovieTrailers.fromMovie(movieId), // content URI
            new String[]{MovieTrailerColumns.KEY, // projection
                MovieTrailerColumns.NAME},
            null, // selection
            null, // selection args
            null); // sort order

      case REVIEWS_LOADER_ID:
        movieId = Objects.requireNonNull(args).getString(EXTRA_MOVIE_ID);
        return new CursorLoader(Objects.requireNonNull(getActivity()), // context
            MoviesProvider.MovieReviews.fromMovie(movieId), // content URI
            new String[]{MovieReviewColumns.REVIEW_ID, // projection
                MovieReviewColumns.AUTHOR,
                MovieReviewColumns.CONTENT},
            null, // selection
            null, // selection args
            null); // sort order

      default:
        throw new IllegalArgumentException(
            String.format("%s: %d", getString(R.string.error_unknown_loader_id), id));
    }
  }

  /**
   * Called when a previously created loader is being reset, and thus
   * making its data unavailable.  The application should at this point
   * remove any references it has to the Loader's data.
   *
   * <p>This will always be called from the process's main thread.
   *
   * @param loader The Loader that is being reset.
   */
  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    // Nothing to do in this case
  }


  private void fetchTrailersFromDb(final Bundle bundle) {
    getLoaderManager().initLoader(TRAILERS_LOADER_ID, bundle, this);
  }

  private void fetchReviewsFromDb(final Bundle bundle) {
    getLoaderManager().initLoader(REVIEWS_LOADER_ID, bundle, this);
  }

  private void setupFavouriteButtonAnimation(final Resources resources) {
    // Setup animation for favourite button
    final int startColor = resources.getColor(R.color.white);
    final int endColor = resources.getColor(R.color.red);
    final VectorAnimationSelectWithPath vectorAnimation =
        new VectorAnimationSelectWithPath(binding.vectorMasterMovieDetailFavourite,
            getString(R.string.vector_path_name),
            startColor,
            endColor);
    vectorAnimation.registerOnSelectedEventListener(this);
    vectorAnimation.setStrokeColor(endColor);
    vectorAnimation.setSelected(isFavourite);
    vectorAnimation.startAnimation(ANIMATION_DURATION);
  }

  private void fetchTrailersFromNetwork(final boolean shouldWriteIntoDb) {
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
          showTrailersView();
          if (shouldWriteIntoDb && !mTrailers.getTrailerList().isEmpty()) {
            saveTrailersIntoDb();
          }
        } else {
          MyPMErrorUtils.showErrorMessage(LOG_TAG,
              Objects.requireNonNull(getActivity()),
              R.string.error_bad_response,
              response.message());
        }
      }

      @Override
      public void onFailure(Call<MovieTrailerList> call, Throwable t) {
        //TODO: Review: in onFailure() a call to getContext() or getActivity() can return null.
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

  protected void showTrailersView() {
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

  private void fetchReviewsFromNetwork(final boolean shouldWriteIntoDb) {
    final MovieReviewService movieReviewService = RETROFIT.create(MovieReviewService.class);

    // Create a call instance for looking up the movie's list of reviews
    mCallReviews = movieReviewService.get(selectedMovie.getId(), BuildConfig.MOVIE_DB_API_KEY);

    // Fetch the Reviews
    mCallReviews.enqueue(new Callback<MovieReviewList>() {
      @Override
      public void onResponse(Call<MovieReviewList> call, Response<MovieReviewList> response) {
        if (response.isSuccessful()) {
          // Here we get the movie review list!
          mReviews = response.body();
          showReviewsView();
          if (shouldWriteIntoDb && !mReviews.getReviewList().isEmpty()) {
            saveReviewsIntoDb();
          }
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

  protected void showReviewsView() {
    final List<MovieReviewList.MovieReview> reviewList = mReviews.getReviewList();

    if (reviewList.isEmpty()) {
      // We got no reviews, show "No reviews" text
      binding.textMovieDetailNoReviews.setVisibility(View.VISIBLE);

      // Hide the RecyclerView that shows the movie reviews
      binding.recyclerMovieDetailReviews.setVisibility(View.GONE);
    } else {
      // We got reviews, show them and their total number
      final String totalReviews =
          String.format("(%s) %s", String.valueOf(reviewList.size()),
              getResources().getString(R.string.movie_detail_reviews));
      binding.textMovieDetailReviews.setText(totalReviews);
      // Set the data(reviews) we have just fetched
      mMovieReviewAdapter.setMovieReviewList(mReviews);
    }
  }

  protected void saveTrailersIntoDb() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        final Context context =
            Objects.requireNonNull(getActivity()).getApplicationContext();
        try {
          // Using a batch of ContentProvider operations for better performance
          final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

          // Setup operations to insert the trailers
          buildTrailerInsertOperations(selectedMovie.getId(), operations);

          // Execute the operations
          getActivity().getContentResolver()
              .applyBatch(MoviesProvider.AUTHORITY, operations);
          Log.v(LOG_TAG, "++++++++++Trailers SAVED");

        } catch (RemoteException | OperationApplicationException e) {
          MyPMErrorUtils
              .logErrorMessage(LOG_TAG, context, R.string.error_accessing_sqlite_db,
                  e.getMessage());
        }
      }
    }).start();
  }

  protected void saveReviewsIntoDb() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        final Context context =
            Objects.requireNonNull(getActivity()).getApplicationContext();
        try {
          // Using a batch of ContentProvider operations for better performance
          final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

          // Setup operations to insert the trailers
          buildReviewInsertOperations(selectedMovie.getId(), operations);

          // Execute the operations
          getActivity().getContentResolver()
              .applyBatch(MoviesProvider.AUTHORITY, operations);
          Log.v(LOG_TAG, "++++++++++Reviews SAVED");

        } catch (RemoteException | OperationApplicationException e) {
          MyPMErrorUtils
              .logErrorMessage(LOG_TAG, context, R.string.error_accessing_sqlite_db,
                  e.getMessage());
        }
      }
    }).start();
  }


  protected ContentProviderResult[] insertMovieBatch(final ContentResolver resolver,
      final String movieId)
      throws RemoteException, OperationApplicationException {

    // Using a batch of ContentProvider operations for better performance
    final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

    // Setup operation to insert the movie: because of referential integrity the movie
    // is inserted first than its trailers and movies
    operations.add(ContentProviderOperation.newInsert(MoviesProvider.Movies.CONTENT_URI)
        .withValues(buildMovieValues()).build());

    // Setup operations to insert the trailers
    buildTrailerInsertOperations(movieId, operations);

    // Setup operations to insert the reviews
    buildReviewInsertOperations(movieId, operations);

    // Execute the operations
    return resolver.applyBatch(MoviesProvider.AUTHORITY, operations);
  }

  private ContentValues buildMovieValues() {
    final ContentValues cv = new ContentValues();

    cv.put(MovieColumns.MOVIE_ID, selectedMovie.getId());
    cv.put(MovieColumns.ORIGINAL_TITLE, selectedMovie.getOriginalTitle());
    cv.put(MovieColumns.RELEASE_YEAR, selectedMovie.getReleaseDate());
    cv.put(MovieColumns.OVERVIEW, selectedMovie.getOverview());
    cv.put(MovieColumns.POSTER_URI, selectedMovie.getPosterUri().toString());
    cv.put(MovieColumns.BACKDROP_URI, selectedMovie.getBackdropUri().toString());
    cv.put(MovieColumns.VOTE_AVERAGE, selectedMovie.getVoteAverage());
    cv.put(MovieColumns.VOTE_COUNT, selectedMovie.getVoteCount());

    return cv;
  }

  // TODO: 7/4/18 Refactor into more JAVA-8 style: buildTrailersBulkValues & buildReviewsBulkValues
  protected void buildTrailerInsertOperations(final String movieId,
      final ArrayList<ContentProviderOperation> insertOperations) {

    final List<MovieTrailerList.MovieTrailer> trailerList = mTrailers.getTrailerList();
    final ContentValues trailerValues = new ContentValues();

    for (final MovieTrailerList.MovieTrailer trailer : trailerList) {

      // Setup the trailer's ContentValues
      trailerValues.put(MovieTrailerColumns.MOVIE_ID, movieId);
      trailerValues.put(MovieTrailerColumns.KEY, trailer.getKey());
      trailerValues.put(MovieTrailerColumns.NAME, trailer.getName());

      insertOperations.add(
          ContentProviderOperation.newInsert(MoviesProvider.MovieTrailers.fromMovie(movieId))
              .withValues(trailerValues)
              .build());
    }
  }

  protected void buildReviewInsertOperations(final String movieId,
      final ArrayList<ContentProviderOperation> insertOperations) {

    final List<MovieReviewList.MovieReview> reviewList = mReviews.getReviewList();
    final ContentValues reviewValues = new ContentValues();

    for (final MovieReviewList.MovieReview review : reviewList) {

      // Setup the review's ContentValues
      reviewValues.put(MovieReviewColumns.MOVIE_ID, movieId);
      reviewValues.put(MovieReviewColumns.REVIEW_ID, review.getId());
      reviewValues.put(MovieReviewColumns.AUTHOR, review.getAuthor());
      reviewValues.put(MovieReviewColumns.CONTENT, review.getContent());

      insertOperations.add(
          ContentProviderOperation.newInsert(MoviesProvider.MovieReviews.fromMovie(movieId))
              .withValues(reviewValues)
              .build());
    }
  }
}


