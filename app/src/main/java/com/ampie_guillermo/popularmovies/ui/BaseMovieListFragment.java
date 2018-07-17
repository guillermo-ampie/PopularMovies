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
import android.widget.Button;
import android.widget.ProgressBar;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.ui.adapter.MovieItemOnClickListener;
import com.ampie_guillermo.popularmovies.utils.MyPMErrorUtils;

/**
 * NOTE: MovieDB API Key stored in "~/.gradle/gradle.properties" using the method specified for
 * Sunshine-Version-2 with API key
 */
public class BaseMovieListFragment
    extends Fragment
    implements MovieItemOnClickListener {

  protected static final String EXTRA_MOVIE_SORTING_METHOD = "EXTRA_MOVIE_SORTING_METHOD";
  private static final String LOG_TAG = BaseMovieListFragment.class.getSimpleName();

  protected RecyclerView mRvMovieGrid;
  protected FetchMoviesListener moviesFetchListener;

  private Group mGroupErrorDisplay;
  private Group mGroupNoFavouriteDisplay;
  private ProgressBar mLoadingIndicator;
  private String mSortingMethodParam;

  public BaseMovieListFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    mLoadingIndicator = rootView.findViewById(R.id.progressbar_main_base);
    final Button buttonRetry = rootView.findViewById(R.id.button_main_try_again);
    buttonRetry.setOnClickListener(new View.OnClickListener() {
      /**
       * Called when a view has been clicked.
       *
       * @param v The view that was clicked.
       */
      @Override
      public void onClick(View v) {
        moviesFetchListener.onClickRetry(v);
      }
    });
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

  protected void showMovieListDisplay() {
    mRvMovieGrid.setVisibility(View.VISIBLE);
    mGroupErrorDisplay.setVisibility(View.INVISIBLE);
    mGroupErrorDisplay.requestLayout(); // I don't like this...

  }

  protected void showNoFavouritesDisplay() {
    mRvMovieGrid.setVisibility(View.INVISIBLE);
    mGroupNoFavouriteDisplay.setVisibility(View.VISIBLE);
    mGroupNoFavouriteDisplay.requestLayout(); // I don't like this...
  }

  protected void showFavouriteMovieListDisplay() {
    mRvMovieGrid.setVisibility(View.VISIBLE);
    mGroupNoFavouriteDisplay.setVisibility(View.INVISIBLE);
    mGroupNoFavouriteDisplay.requestLayout(); // I don't like this...
  }

  protected void registerFetchMoviesListener(final FetchMoviesListener listener) {
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

    void onClickRetry(View view);
  }
}
