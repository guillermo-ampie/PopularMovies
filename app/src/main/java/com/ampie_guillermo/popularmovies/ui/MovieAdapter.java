package com.ampie_guillermo.popularmovies.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

  private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
  static MovieItemClickListener sOnClickListener;
  private ArrayList<Movie> mMovieList;

  MovieAdapter(MovieItemClickListener onClickListener) {
    sOnClickListener = onClickListener;
  }

  /**
   * Called by RecyclerView to display the data at the specified position. This method should
   * update the contents of the {link ViewHolder#itemView} to reflect the item at the given
   * position.
   * <p>
   * Note that unlike {@link ListView}, RecyclerView will not call this method
   * again if the position of the item changes in the data set unless the item itself is
   * invalidated or the new position cannot be determined. For this reason, you should only
   * use the <code>position</code> parameter while acquiring the related data item inside
   * this method and should not keep a copy of it. If you need the position of an item later
   * on (e.g. in a click listener), use {link ViewHolder#getAdapterPosition()} which will
   * have the updated adapter position.
   *
   * Override {link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
   * handle efficient partial bind.
   *
   * @param holder The ViewHolder which should be updated to represent the contents of the item at
   * the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   */
  @Override
  public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {

    Movie currentMovie = mMovieList.get(position);

    Picasso.with(holder.itemView.getContext())
        .load(currentMovie.getPosterCompleteUri())
        .placeholder(R.drawable.no_thumbnail)
        .error(R.drawable.no_thumbnail)
        .into(holder.mMovieThumbnailView);
  }

  /**
   * Returns the total number of items in the data set held by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override
  public int getItemCount() {
    return (mMovieList != null) ? mMovieList.size() : 0;
  }

  void setMovieList(ArrayList<Movie> movieList) {
    // set the new data & update the UI
    mMovieList = movieList;
    notifyDataSetChanged();
  }

  /**
   * Called when RecyclerView needs a new {link ViewHolder} of the given type to represent
   * an item.
   * <p>
   * This new ViewHolder should be constructed with a new View that can represent the items
   * of the given type. You can either create a new View manually or inflate it from an XML
   * layout file.
   * <p>
   * The new ViewHolder will be used to display items of the adapter using
   * {link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
   * different items in the data set, it is a good idea to cache references to sub views of
   * the View to avoid unnecessary {@link View#findViewById(int)} calls.
   *
   * @param parent The ViewGroup into which the new View will be added after it is bound to an
   * adapter position.
   * @param viewType The view type of the new View.
   * @return A new ViewHolder that holds a View of the given view type.
   * @see #getItemViewType(int) see #onBindViewHolder(ViewHolder, int)
   */
  @Override
  public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.movie_poster_item, parent, false);

    return new MovieViewHolder(view);
  }

  interface MovieItemClickListener {

    void onMovieItemClick(int clickedItemIndex);
  }

  // The View Holder used for each movie trailer
  static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView mMovieThumbnailView;

    MovieViewHolder(View itemView) {
      super(itemView);
      mMovieThumbnailView = itemView.findViewById(R.id.movie_poster_view);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      // getAdapterPosition() gives us the the item that was clicked
      sOnClickListener.onMovieItemClick(getAdapterPosition());
    }
  }
}
