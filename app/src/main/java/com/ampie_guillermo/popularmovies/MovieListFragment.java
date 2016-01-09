package com.ampie_guillermo.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
public class MovieListFragment extends Fragment {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName ();

    private MovieAdapter mMovieAdapter;
    private String mSortingMethodParam;

    public MovieListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        /**
         * The MovieAdapter will take data from a a JSON response from TheMovieDB.org
         * server and use it to populate the GridView it's attached to.
         */
        mMovieAdapter = new MovieAdapter (getActivity(), new ArrayList<Movie>());

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

    void setSortingMethodParam(String sortingMethodParam) {
        mSortingMethodParam = sortingMethodParam;
    }

    private void getMovies() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity(), mMovieAdapter);
        movieTask.execute(mSortingMethodParam);
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovies();
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
}
