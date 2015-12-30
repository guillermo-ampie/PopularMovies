package com.ampie_guillermo.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class MainActivityFragment extends Fragment {

    private final String SORT_BY_POPULARITY = "popularity.desc";
    private final String SORT_BY_RATING = "vote_average.desc";

    private MovieAdapter mMovieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu with our sorting options
        inflater.inflate(R.menu.main_activity_fragment_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {
            case R.id.action_sort_by_popularity:
                getMovies(SORT_BY_POPULARITY);
                return true;
            case R.id.action_sort_by_rating:
                getMovies(SORT_BY_RATING);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void getMovies(String sortingMethod) {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity(), mMovieAdapter);
        movieTask.execute(sortingMethod);
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovies(SORT_BY_POPULARITY);
    }

}
