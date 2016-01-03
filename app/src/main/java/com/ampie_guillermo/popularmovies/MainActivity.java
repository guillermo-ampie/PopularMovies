package com.ampie_guillermo.popularmovies;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_RATING = "vote_average.desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        /**
         * We will have only TWO pages:
         *   - 1st. Movies by Popularity
         *   - 2nd. Movies by Rating
         */
        MainActivityFragment fragmentPage1 = new MainActivityFragment();
        fragmentPage1.setSortingMethodParam(SORT_BY_POPULARITY);

        MainActivityFragment fragmentPage2 = new MainActivityFragment();
        fragmentPage2.setSortingMethodParam(SORT_BY_RATING);

        viewPagerAdapter.addFragmentPage(fragmentPage1, getString (R.string.sort_by_popularity))
                        .addFragmentPage(fragmentPage2, getString(R.string.sort_by_rating));

        // Attach the adapter to the View Pager
        viewPager.setAdapter(viewPagerAdapter);

        // Get the TabLayout and attach the ViewPager to it
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        //tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        //tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));
    }
}
