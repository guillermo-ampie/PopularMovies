package com.ampie_guillermo.popularmovies.ui;

import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.ampie_guillermo.popularmovies.BuildConfig;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.ui.MovieListFragment.PopularMovieListFragment;
import com.ampie_guillermo.popularmovies.ui.MovieListFragment.RatedMovieListFragment;
import com.ampie_guillermo.popularmovies.ui.adapter.ViewPagerAdapter;


/**
 * Tabs code reference:
 * http://www.ekiras.com/2015/12/how-to-implement-material-design-tab-layout-in-android.html
 */

public class MainActivity extends AppCompatActivity {

  private static final String LOG_TAG = MainActivity.class.getSimpleName();
  private static final String BUNDLE_SELECTED_TAB = "BUNDLE_SELECTED_TAB";

  private ViewPager mViewPager;
  private TabLayout mTabLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // See comment in MovieAdapter::MovieViewHolder()  to allow vector drawables in
    // API level < 21 (Lollipop)
    if (Build.VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
      AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
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
// TODO: Check out Stetho because is generating a LEAK in MainActivity....

//      Stetho.initializeWithDefaults(this);

      /**
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
      final StrictMode.VmPolicy.Builder policyBuilder = new StrictMode.VmPolicy.Builder();
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
      StrictMode.setVmPolicy(policyBuilder.penaltyLog().penaltyLog().build());
    }

    setContentView(R.layout.activity_main);

    final Toolbar toolbar = findViewById(R.id.toolbar_movie_detail_toolbar);
    setSupportActionBar(toolbar);

    mViewPager = findViewById(R.id.viewpager_main_viewpager);
    final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

    /**
     * We will have THREE TABS, in this case it seems OK to "preload" them for the sake of
     * UI responsiveness:
     *   - 1st. Movies by Popularity
     *   - 2nd. Movies by Rating
     *   - 3rd. Favorites movies
     */
    final PopularMovieListFragment popularMoviesPage
        = new PopularMovieListFragment();

    final RatedMovieListFragment ratedMoviesPage
        = new RatedMovieListFragment();

    final FavouriteMovieListFragment favouriteMoviesPage
        = new FavouriteMovieListFragment();

    viewPagerAdapter
        .addFragmentPage(popularMoviesPage, getString(R.string.main_popularity_tab_title))
        .addFragmentPage(ratedMoviesPage, getString(R.string.main_rating_tab_title))
        .addFragmentPage(favouriteMoviesPage, getString(R.string.main_favourite_tab_title));

    // Attach the adapter to the View Pager
    mViewPager.setAdapter(viewPagerAdapter);

    // Get the TabLayout and attach the ViewPager to it
    mTabLayout = findViewById(R.id.tab_main_tabs);

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
    outState.putInt(BUNDLE_SELECTED_TAB, mTabLayout.getSelectedTabPosition());
  }

  /**
   * This method is called after {@link #onStart} when the activity is
   * being re-initialized from a previously saved state, given here in
   * <var>savedInstanceState</var>.  Most implementations will simply use {@link #onCreate}
   * to restore their state, but it is sometimes convenient to do it here
   * after all of the initialization has been done or to allow subclasses to
   * decide whether to use your default implementation.  The default
   * implementation of this method performs a restore of any divider_view_1 state that
   * had previously been frozen by {@link #onSaveInstanceState}.
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
    mViewPager.setCurrentItem(savedInstanceState.getInt(BUNDLE_SELECTED_TAB));
  }
}

