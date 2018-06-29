package com.ampie_guillermo.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.ui.adapter.MovieItemClickListener;
import com.ampie_guillermo.popularmovies.utils.MyPMErrorUtils;

/**
 * NOTE: MovieDB API Key stored in "~/.gradle/gradle.properties" using the method specified for
 * Sunshine-Version-2 with API key
 */
public class BaseMovieListFragment
    extends Fragment
    implements MovieItemClickListener {

  protected static final String EXTRA_MOVIE_SORTING_METHOD = "EXTRA_MOVIE_SORTING_METHOD";
  private static final String LOG_TAG = BaseMovieListFragment.class.getSimpleName();

  protected RecyclerView mRvMovieGrid;
  private Group mGroupErrorDisplay;
  private Group mGroupNoFavouriteDisplay;
  private ImageView mImageNoFavourites;
  private ProgressBar mLoadingIndicator;

  private String mSortingMethodParam;
  private FetchMoviesListener moviesFetchListener;

  public BaseMovieListFragment() {
  }

  /**
   * Called to ask the fragment to save its current dynamic state, so it
   * can later be reconstructed in a new instance of its process is
   * restarted.  If a new instance of the fragment later needs to be
   * created, the data you place in the Bundle here will be available
   * in the Bundle given to {@link #onCreate(Bundle)},
   * #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
   * {@link #onActivityCreated(Bundle)}.
   * This corresponds to  Activity onSaveInstanceState(Bundle)
   * Activity onSaveInstanceState(Bundle)} and most of the discussion there
   * applies here as well.  Note however: <em>this method may be called
   * at any time before {@link #onDestroy()}</em>.  There are many situations
   * where a fragment may be mostly torn down (such as when placed on the
   * back stack with no UI showing), but its state will not be saved until
   * its owning activity actually needs to save its state.
   *
   * @param outState Bundle in which to place your saved state.
   */
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    /**
     * There is no need to use savedInstanceState because mCachedMovieList is managed by the
     * AsyncTaskLoader on configuration changes
     */
//    if (!mCachedMovieList.isEmpty()) {
//      outState.putParcelableArrayList(MOVIE_LIST, mCachedMovieList);
//    }
  }

  /**
   * Called to do initial creation of a fragment.  This is called after
   * {@link #onAttach (Activity)} and before
   * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
   * Note that this can be called while the fragment's activity is
   * still in the process of being created.  As such, you can not rely
   * on things like the activity's content divider_view_1 hierarchy being initialized
   * at this point.  If you want to do work once the activity itself is
   * created, see {@link #onActivityCreated(Bundle)}.
   *
   * @param savedInstanceState If the fragment is being re-created from a previous saved state, this
   * is the state.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    /**
     * There is no need to use savedInstanceState() because the movie list fetched is persisted
     * / cached by the AsyncTaskLoader's subclass MovieListLoader upon configuration changes
     */
//    if ((savedInstanceState != null) && savedInstanceState.containsKey(MOVIE_LIST)) {
//      // Let's get the saved movie list array from a saved state
//      mCachedMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
//    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    mLoadingIndicator = rootView.findViewById(R.id.progressbar_main_base);
    mGroupErrorDisplay = rootView.findViewById(R.id.group_main_no_connection);
    mGroupNoFavouriteDisplay = rootView.findViewById(R.id.group_main_no_favourite_movies);

    showLoadingIndicator();

    // Get a reference to the RecyclerView (the movie grid), and attach the movie adapter to it.
    mRvMovieGrid = rootView.findViewById(R.id.recycler_main_movie_grid);

    // Al the items (movie posters) in the RecyclerView are the same size
    mRvMovieGrid.setHasFixedSize(true);

    // We will show the movie list in a grid with a parameterized number of columns
    mRvMovieGrid.setLayoutManager(new GridLayoutManager(getContext(),
        getResources().getInteger(R.integer.num_columns)));

//    // Set an empty adapter because the movies have not been fetched yet
//    mMovieAdapter = new MovieAdapter(this);
//    mRvMovieGrid.setAdapter(mMovieAdapter);
//
    /**
     * The RecyclerView's Adapter will take data from a JSON response from TheMovieDB.org
     * server / local DB
     */
    moviesFetchListener.setupRecyclerViewAdapter();
    return rootView;
  }

  /**
   * Called when the fragment's activity has been created and this
   * fragment's hierarchy instantiated.  It can be used to do final
   * initialization once these pieces are in place, such as retrieving
   * views or restoring state.  It is also useful for fragments that use
   * {@link #setRetainInstance(boolean)} to retain their instance,
   * as this callback tells the fragment when it is fully associated with
   * the new activity instance.  This is called after {@link #onCreateView}
   * and before {@link #onViewStateRestored(Bundle)}.
   *
   * @param savedInstanceState If the fragment is being re-created from a previous saved state, this
   * is the state.
   */
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    moviesFetchListener.getMovies();
  }

  @Override
  public void onMovieItemClick(int clickedItemIndex) {
    MyPMErrorUtils.validateIndexInCollection(clickedItemIndex, moviesFetchListener.movieListSize());
//    final Movie currentMovie = mMovieList.get(clickedItemIndex);
    final Movie currentMovie = moviesFetchListener.getSelectedMovie(clickedItemIndex);
    final Intent intent = new Intent(getActivity(),
        MovieDetailActivity.class).putExtra(getString(R.string.EXTRA_SELECTED_MOVIE), currentMovie);

    startActivity(intent);
  }

  protected String getSortingMethodParam() {
    return mSortingMethodParam;
  }

  protected void setSortingMethodParam(String sortingMethodParam) {
    mSortingMethodParam = sortingMethodParam;
  }

  protected void showLoadingIndicator() {
    mLoadingIndicator.setVisibility(View.VISIBLE);
  }

  protected void hideLoadingIndicator() {
    mLoadingIndicator.setVisibility(View.INVISIBLE);
  }

  protected void showErrorDisplay() {
    mRvMovieGrid.setVisibility(View.INVISIBLE);
    mGroupErrorDisplay.setVisibility(View.VISIBLE);
    mGroupErrorDisplay.requestLayout(); // I don't like this...
  }

  protected void showNoFavouritesDisplay() {
    mRvMovieGrid.setVisibility(View.INVISIBLE);
    mGroupNoFavouriteDisplay.setVisibility(View.VISIBLE);
    mGroupNoFavouriteDisplay.requestLayout(); // I don't like this...
  }

  protected final void registerFetchMoviesListener(final FetchMoviesListener listener) {
    moviesFetchListener = listener;
  }

  public interface FetchMoviesListener {

    void getMovies();

    Movie getSelectedMovie(int clickedItemIndex);

    int movieListSize();

    /*
     * Create & attach the RecyclerView Adapter
     */
    void setupRecyclerViewAdapter();

  }
}
