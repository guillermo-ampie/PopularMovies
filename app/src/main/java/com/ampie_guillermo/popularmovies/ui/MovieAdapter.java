package com.ampie_guillermo.popularmovies.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * MovieAdapter: class to handle the display of the movie posters.
 * The code is inspired by the android-custom-arrayadapter demo project referenced in
 * the ---Popular Movies App Implementation Guide---
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    /**
     * Custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context   The current context. Used to inflate the layout file.
     * @param movieList A List of Movie objects to display in the GridView.
     */
    public MovieAdapter(Activity context, List<Movie> movieList) {
        // Here, we initialize the ArrayAdapter's internal storage for the context
        // and the list.
        // The second argument is used when the ArrayAdapter is populating a single
        // TextView (the default case).
        // Because this is a custom adapter with an  ImageView (and not a TextView),
        // the adapter is not going to use this second argument, so it can be any
        // value (here 0 does the job).
        super(context, 0, movieList);
    }

    /**
     * Provides a view for an AdapterView (a GridView in this case)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the Movie object from the ArrayAdapter at the appropriate position
        Movie currentMovie = getItem(position);
        MovieAdapter.MovieViewHolder movieHolder;
        View view;

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView.
        if (convertView == null) {
            //Inflate the layout
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_poster_item, parent, false);

            // Set up the view holder object
            movieHolder = new MovieAdapter.MovieViewHolder();
            movieHolder.mMoviePoster = view.findViewById(R.id.movie_poster_view);

            // Attach the holder object with the view
            view.setTag(movieHolder);
        } else {
            // We just get our view holder with the cached ImageView
            movieHolder = (MovieAdapter.MovieViewHolder) convertView.getTag();
            view = convertView;
        }

        if (currentMovie != null) {
            Picasso.with(getContext())
                    .load(currentMovie.getPosterCompleteUri())
                    .placeholder(R.drawable.no_thumbnail)
                    .error(R.drawable.no_thumbnail)
                    .into(movieHolder.mMoviePoster);
        }

        return view;
    }

    // Only one ImageView but the View Holder Pattern can improve some performance.
    // This caches the ImageView for the movie poster
    private static class MovieViewHolder {
        ImageView mMoviePoster;
    }
}
