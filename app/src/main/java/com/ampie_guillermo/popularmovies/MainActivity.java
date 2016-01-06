package com.ampie_guillermo.popularmovies;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName ();
    private static final String SELECTED_TAB = "sel-tab";

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        /**
         * We will have ONLY two pages, so it is OK to "preload" them for the sake of
         * UI responsiveness:
         *   - 1st. Movies by Popularity
         *   - 2nd. Movies by Rating
         */
        MovieListFragment.PopularMovieListFragment PopularMoviesPage
                = new MovieListFragment.PopularMovieListFragment ();

        MovieListFragment.RatedMovieListFragment RatedMoviesPage
                = new MovieListFragment.RatedMovieListFragment();

        viewPagerAdapter.addFragmentPage(PopularMoviesPage,
                                         getString(R.string.sort_by_popularity))
                        .addFragmentPage(RatedMoviesPage,
                                         getString(R.string.sort_by_rating));

        // Attach the adapter to the View Pager
        viewPager.setAdapter(viewPagerAdapter);

        // Get the TabLayout and attach the ViewPager to it
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        //tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        //tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));
    }

    /**
     * Save all appropriate fragment state.
     *
     * @param outState the Bundle object to save the activity state 
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(LOG_TAG, "SAVE SELECTED_TAB: " + Integer.toString(tabLayout.getSelectedTabPosition()));
        outState.putInt(SELECTED_TAB, tabLayout.getSelectedTabPosition());
    }

    /**
     * This method is called after {@link #onStart} when the activity is
     * being re-initialized from a previously saved state, given here in
     * <var>savedInstanceState</var>.  Most implementations will simply use {@link #onCreate}
     * to restore their state, but it is sometimes convenient to do it here
     * after all of the initialization has been done or to allow subclasses to
     * decide whether to use your default implementation.  The default
     * implementation of this method performs a restore of any view state that
     * had previously been frozen by {@link #onSaveInstanceState}.
     * <p/>
     * <p>This method is called between {@link #onStart} and
     * {@link #onPostCreate}.
     *
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     * @see #onCreate
     * @see #onPostCreate
     * @see #onResume
     * @see #onSaveInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(LOG_TAG, "GET SELECTED_TAB: " + Integer.toString(savedInstanceState.getInt(SELECTED_TAB)));
        viewPager.setCurrentItem(savedInstanceState.getInt(SELECTED_TAB));
    }
}

