package com.ampie_guillermo.popularmovies.ui;

import android.app.Activity;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.ui.adapter.FavouriteMovieAdapter;

public class FavouriteMovieListFragment
    extends BaseMovieListFragment
    implements BaseMovieListFragment.FetchMoviesListener {

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
    /*
     * READ FROM DATABASE AND POPULATE "mCachedMovieList":
     * if (Database is empty) {
     *   show "No favorite movies"
     * }
     * else {
     * display all movies in the database (we are storing only the FAVORITES movies
     * in the database)
     * }
     */
  }

  @Override
  public Movie getSelectedMovie(int clickedItemIndex) {
    /*
     * Get Movie from Cursor
     */
    assert 1 != 1;
    return null;
  }

  @Override
  public int movieListSize() {
    /*
     * Get size from Cursor
     */
    assert 1 != 1;
    return 0;
  }

  @Override
  public void setupRecyclerViewAdapter() {
    // Set an empty adapter because the movies have not been fetched yet
      favouriteMovieAdapter = new FavouriteMovieAdapter(this);
      mRvMovieGrid.setAdapter(favouriteMovieAdapter);

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
    // MainActivity / BaseMovieListFragment and return(when we go and get back from
    // MovieDetailActivity / MovieDetailFragment where the selected movie could have been marked
    // as favourite / not favourite

    // restart the loader!

  }
}
