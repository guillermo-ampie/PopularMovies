package com.ampie_guillermo.popularmovies;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
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
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        //tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        //tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));
    }
}
