package com.ampie_guillermo.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * The fragment to display the movie's data.
 */
public class MovieDetailFragment extends Fragment {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();

        // Get the selected movie passed by Intent
        Movie mSelectedMovie = intent.getExtras().getParcelable("selected-movie");
       if (mSelectedMovie != null) {
           // we will reuse -tv- variable for all the TextView objects in this fragment
           TextView tv = (TextView) rootView.findViewById(R.id.movie_title_text);
           tv.setText(mSelectedMovie.getMovieOriginalTitle());

           ImageView moviePosterView =
                   (ImageView) rootView.findViewById(R.id.movie_poster_detail_view);
           Picasso.with(getContext())
                   .load(mSelectedMovie.getMoviePosterCompleteUri())
                   .into(moviePosterView);

           tv = (TextView) rootView.findViewById(R.id.release_date_text);
           tv.setText(mSelectedMovie.getMovieReleaseDate());

           String rating = String.valueOf(mSelectedMovie.getMovieVoteAverage());
           rating += " / 10";
           tv = (TextView) rootView.findViewById(R.id.rating_text);
           tv.setText(rating);

           String votes = String.valueOf(mSelectedMovie.getMovieVoteCount());
           tv = (TextView) rootView.findViewById(R.id.vote_count_text);
           tv.setText(votes);

           tv = (TextView) rootView.findViewById(R.id.movie_overview_text);
           tv.setText(mSelectedMovie.getMovieOverview());
        }

        return rootView;
    }
}
