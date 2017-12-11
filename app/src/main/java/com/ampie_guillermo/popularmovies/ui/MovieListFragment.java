package com.ampie_guillermo.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.network.FetchMovieTask;
import com.ampie_guillermo.popularmovies.model.Movie;

import java.util.ArrayList;

/**
 * A fragment containing a GridView with all the movies' posters.
 * The code is inspired by the Sunshine-Version-2 demo project from
 * the ---Developing Android Apps - Fundamentals--- course
 *
 * NOTE: MovieDB API Key stored in "~/.gradle/gradle.properties"
 *       Using the method specified for Sunshine-Version-2 with
 *       API key
 */
public class    MovieListFragment extends Fragment {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName ();
    private static final String MOVIE_LIST = "movie-list";

    private MovieAdapter mMovieAdapter;
    private String mSortingMethodParam;
    private ArrayList <Movie> mMovieArrayList;

    public MovieListFragment() {
        mMovieArrayList = new ArrayList<>();
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p/>
     * <p>This corresponds to  Activity onSaveInstanceState(Bundle)
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

        // Save the movie list we got from TheMovieDB.org server
        outState.putParcelableArrayList(MOVIE_LIST, mMovieArrayList);
        //Log.e(LOG_TAG, "State saved: " + mMovieArrayList.size());

    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach (Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p/>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Check: When the UP BUTTON is pressed the saved state is not retrieved...
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST)) {
            // Let's get the saved movie list array from a saved state
            mMovieArrayList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            //Log.e(LOG_TAG, "State restored");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        /*
         * The MovieAdapter will take data from a a JSON response from TheMovieDB.org
         * server and use it to populate the GridView it's attached to.
         */
        mMovieAdapter = new MovieAdapter (getActivity(), mMovieArrayList);

        // Get a reference to the GridView, and attach the movie adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie currentMovie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("selected-movie", currentMovie);

                startActivity(intent);
            }
        });
        return rootView;
    }

    protected void setSortingMethodParam(String sortingMethodParam) {
        mSortingMethodParam = sortingMethodParam;
    }

    protected void getMovies() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity(), mMovieAdapter);
        movieTask.execute(mSortingMethodParam);
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
         * Let's connect to theMovieDB server/read from database --only-- if the movie list
         * is empty. The movie list is getting filled up either when we connect to TheMovieDB
         * server/read from the database or when we restore it from a previous state (state saved
         * in onSaveInstanceState)
         */
        if (mMovieArrayList.isEmpty()) {
            getMovies();
        }
    }

    public static class PopularMovieListFragment extends MovieListFragment {

        private static final String SORT_BY_POPULARITY = "popularity.desc";

        public PopularMovieListFragment() {
            setSortingMethodParam(SORT_BY_POPULARITY);
        }
    }

    public static class RatedMovieListFragment extends MovieListFragment {

        private static final String SORT_BY_RATING = "vote_average.desc";

        public RatedMovieListFragment() {
            setSortingMethodParam(SORT_BY_RATING);
        }
    }

    public static class FavoriteMovieListFragment extends MovieListFragment {

        public FavoriteMovieListFragment() {
            /*
            We do not need to call setSortingMethodParam (""). In this case,
            we are going to read from the local database and not to connect to TheMovieDB server
            */
        }

        @Override
        protected void getMovies() {
            /*
             * READ FROM DATABASE AND POPULATE "mMovieArrayList":
             * if (Database is empty) {
             *   show "No favorite movies"
             * }
             * else {
             * display all movies in the database (we are storing only the FAVORITES movies
             * in the database)
             * }
             */
        }
    }
}
