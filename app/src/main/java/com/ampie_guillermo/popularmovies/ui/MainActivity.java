package com.ampie_guillermo.popularmovies.ui;


import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.ampie_guillermo.popularmovies.BuildConfig;
import com.ampie_guillermo.popularmovies.R;
import com.facebook.stetho.Stetho;


/**
 * Tabs code reference:
 * http://www.ekiras.com/2015/12/how-to-implement-material-design-tab-layout-in-android.html
 */

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String SELECTED_TAB = "sel-tab";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (BuildConfig.DEBUG) {
            Log.v(LOG_TAG, "BUILD: ** DEBUG **");

            /**
             * Enable: Stetho
             */

            /**
             * Stetho: a sophisticated debug bridge for Android applications. When enabled,
             * developers have access to the Chrome Developer Tools feature natively part
             * of the Chrome desktop browser. Developers can also choose to enable the
             * optional dumpapp tool which offers a powerful command-line interface to
             * application internals.
             * http://facebook.github.io/stetho/
             */
/*
            Stetho.initialize(Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
*/
            Stetho.initializeWithDefaults(this);


            /*
             * Enable StrictMode
             */

            // Detect for blocking the UI thread
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog()
                            .build());

            /**
             * We intent to use detectAll() in the VmPolicy, but we are getting a
             * policy violation with HttpURLConnection in Android Oreo if we use
             * detectUntaggedSockets(). This is the reason for the ugly code in the VmPolicy
             * As far of Dec, 11 2017 you will get the same result with OkHttp library:
             * no solution or workaround has been provided yet!
             */

            // Detect for memory leaks
            StrictMode.VmPolicy.Builder policyBuilder = new StrictMode.VmPolicy.Builder();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                policyBuilder.detectAll();
            } else {
                // Android Oreo or higher
                policyBuilder.detectActivityLeaks()
                        .detectCleartextNetwork()
                        .detectContentUriWithoutPermission()
                        .detectFileUriExposure()
                        .detectLeakedClosableObjects()
                        .detectLeakedRegistrationObjects()
                        .detectLeakedSqlLiteObjects();
            }
            StrictMode.setVmPolicy(policyBuilder.penaltyLog().penaltyDeath().build());
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        /**
         * We will have THREE TABS, in this case it seems OK to "preload" them for the sake of
         * UI responsiveness:
         *   - 1st. Movies by Popularity
         *   - 2nd. Movies by Rating
         *   - 3rd. Favorites movies
         */
        MovieListFragment.PopularMovieListFragment popularMoviesPage
                = new MovieListFragment.PopularMovieListFragment();

        MovieListFragment.RatedMovieListFragment ratedMoviesPage
                = new MovieListFragment.RatedMovieListFragment();

        MovieListFragment.FavoriteMovieListFragment favoriteMoviesPage
                = new MovieListFragment.FavoriteMovieListFragment();

        viewPagerAdapter.addFragmentPage(popularMoviesPage, getString(R.string.sort_by_popularity))
                .addFragmentPage(ratedMoviesPage, getString(R.string.sort_by_rating))
                .addFragmentPage(favoriteMoviesPage, getString(R.string.favorite_movies));

        // Attach the adapter to the View Pager
        mViewPager.setAdapter(viewPagerAdapter);

        // Get the TabLayout and attach the ViewPager to it
        mTabLayout = findViewById(R.id.tabs);

        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);
        }

        //mTabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        //mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));
    }

    /**
     * Save all appropriate Activity state.
     *
     * @param outState the Bundle object to save the activity state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save TabLayout state: selected tab
        outState.putInt(SELECTED_TAB, mTabLayout.getSelectedTabPosition());
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

        // Get & set TabLayout state: selected tab
        mViewPager.setCurrentItem(savedInstanceState.getInt(SELECTED_TAB));
    }
}

