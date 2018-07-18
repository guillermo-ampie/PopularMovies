package com.ampie_guillermo.popularmovies.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.database.MovieColumns;
import com.ampie_guillermo.popularmovies.database.MoviesProvider;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.ui.adapter.FavouriteMovieAdapter;
import java.util.Objects;

public class FavouriteMovieListFragment
    extends BaseMovieListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
    BaseMovieListFragment.FetchMoviesListener {

  public static final int MOVIE_ID_INDEX = 0;
  public static final int ORIGINAL_TITLE_INDEX = 1;
  public static final int RELEASE_YEAR_INDEX = 2;
  public static final int OVERVIEW_INDEX = 3;
  public static final int POSTER_URI_INDEX = 4;
  public static final int BACKDROP_URI_INDEX = 5;
  public static final int VOTE_AVERAGE_INDEX = 6;
  public static final int VOTE_COUNT_INDEX = 7;
  public static final int FAVOURITE_MOVIE_LIST_LOADER_ID = 1100;
  private static final String[] MOVIE_COLUMNS_PROJECTION = {
      MovieColumns.MOVIE_ID,
      MovieColumns.ORIGINAL_TITLE,
      MovieColumns.RELEASE_YEAR,
      MovieColumns.OVERVIEW,
      MovieColumns.POSTER_URI,
      MovieColumns.BACKDROP_URI,
      MovieColumns.VOTE_AVERAGE,
      MovieColumns.VOTE_COUNT
  };
  private static final String LOG_TAG = FavouriteMovieListFragment.class.getSimpleName();

  private FavouriteMovieAdapter favouriteMovieAdapter;

  public FavouriteMovieListFragment() {
    /*
     * We do not need to call setSortingMethodParam(). In this case, we are going to read
     * from the local database and not to connect to TheMovieDB server
     */
    registerFetchMoviesListener(this);
  }

  @Override
  public void getMovies() {
    // Get movies from DB using a CursorLoader
    getLoaderManager().initLoader(FAVOURITE_MOVIE_LIST_LOADER_ID, null, this);
  }

  @Override
  public Movie getSelectedMovie(final int clickedItemIndex) {
    // Get Movie from Cursor
    return favouriteMovieAdapter.getItem(clickedItemIndex);
  }

  @Override
  public int movieListSize() {
    // Get size from Cursor
    return favouriteMovieAdapter.getItemCount();
  }

  @Override
  public void setupRecyclerViewAdapter() {
    // Set an empty adapter because the movies have not been fetched yet
    favouriteMovieAdapter = new FavouriteMovieAdapter(this);
    mRvMovieGrid.setAdapter(favouriteMovieAdapter);

  }

  @Override
  public void onClickRetry(View view) {
    // In FavouriteMovieListFragment we do nothing here
  }

  /**
   * Called when the fragment is visible to the user and actively running.
   * This is generally
   * tied to {@link Activity#onResume() Activity.onResume} of the containing
   * Activity's lifecycle.
   */
  @Override
  public void onResume() {
    super.onResume();
    // Re-query the DB after the Fragment/Activity has been -paused-: Any time we leave the
    // MainActivity / FavouriteMovieListFragment and return from MovieDetailActivity /
    // MovieDetailFragment, where the selected movie could have been marked as favourite /
    // not favourite

    // restart the loader!
    getLoaderManager().restartLoader(FAVOURITE_MOVIE_LIST_LOADER_ID, null, this);
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

    switch (id) {
      case FAVOURITE_MOVIE_LIST_LOADER_ID:
        return new CursorLoader(Objects.requireNonNull(getActivity()), // context
            MoviesProvider.Movies.CONTENT_URI, // content URI
            MOVIE_COLUMNS_PROJECTION, // projection
            null, // selection
            null, // selection args
            null); // sort order
      default:
        throw new IllegalArgumentException(
            String.format("%s: %d", getString(R.string.error_unknown_loader_id), id));
    }
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
   * the {@link CursorAdapter##CursorAdapter(Context, * Cursor, int)} constructor <em>without</em> passing
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
    hideLoadingIndicator();
    // Set the new data
    favouriteMovieAdapter.swapCursor(data);
    if (data.getCount() == 0) {
      // There are not favourite movies
      showNoFavouritesDisplay();
      Log.v(LOG_TAG, "++++++++++onLoadFinished(): NO DATA");
    } else {
      Log.v(LOG_TAG, "++++++++++onLoadFinished(): DISPLAYING DATA");
      showFavouriteMovieListDisplay();
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
    // On reset, the loader's data becomes invalid, so clear the adapter's data
    favouriteMovieAdapter.swapCursor(null);
  }
}
