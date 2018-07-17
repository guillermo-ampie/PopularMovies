package com.ampie_guillermo.popularmovies.ui.adapter;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.ui.FavouriteMovieListFragment;
import java.util.List;

public class FavouriteMovieAdapter extends RecyclerView.Adapter<MovieViewHolder> {

  private static final String LOG_TAG = FavouriteMovieAdapter.class.getSimpleName();
  private final MovieItemOnClickListener onClickListener;

  private Cursor cursor;

  public FavouriteMovieAdapter(final MovieItemOnClickListener listener) {
    onClickListener = listener;
  }

  /**
   * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
   * an item.
   * <p>
   * This new ViewHolder should be constructed with a new View that can represent the items
   * of the given type. You can either create a new View manually or inflate it from an XML
   * layout file.
   * <p>
   * The new ViewHolder will be used to display items of the adapter using
   * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
   * different items in the data set, it is a good idea to cache references to sub views of
   * the View to avoid unnecessary {@link View#findViewById(int)} calls.
   *
   * @param parent The ViewGroup into which the new View will be added after it is bound to
   * an adapter position.
   * @param viewType The view type of the new View.
   * @return A new ViewHolder that holds a View of the given view type.
   * @see #getItemViewType(int)
   * @see #onBindViewHolder(ViewHolder, int)
   */
  @NonNull
  @Override
  public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(R.layout.item_movie_poster, parent, false);

    return new MovieViewHolder(view, onClickListener);
  }

  /**
   * Called by RecyclerView to display the data at the specified position. This method should
   * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
   * position.
   * <p>
   * Note that unlike {@link ListView}, RecyclerView will not call this method
   * again if the position of the item changes in the data set unless the item itself is
   * invalidated or the new position cannot be determined. For this reason, you should only
   * use the {@code position} parameter while acquiring the related data item inside
   * this method and should not keep a copy of it. If you need the position of an item later
   * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
   * have the updated adapter position.
   *
   * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
   * handle efficient partial bind.
   *
   * @param holder The ViewHolder which should be updated to represent the contents of the
   * item at the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   */
  @Override
  public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
    final Movie currentMovie = getItem(position);
    holder.setupItemView(currentMovie);
  }

  /**
   * Returns the total number of items in the data set held by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override
  public int getItemCount() {
    return (cursor == null) ? 0 : cursor.getCount();
  }

  /**
   * swapCursor sets a new cursor to be used in the RecyclerView's adapter.
   *
   * @param newCursor: the new cursor to use in the RecyclerView's adapter
   */
  public void swapCursor(final Cursor newCursor) {
    cursor = newCursor;
    notifyDataSetChanged();
  }

  public Movie getItem(final int position) {
    // Move cursor to item's index
    cursor.moveToPosition(position);

    // Get columns from cursor to build up the selected Movie
    final String id = cursor.getString(FavouriteMovieListFragment.MOVIE_ID_INDEX);
    final String originalTitle = cursor.getString(FavouriteMovieListFragment.ORIGINAL_TITLE_INDEX);
    final String releaseYear = cursor.getString(FavouriteMovieListFragment.RELEASE_YEAR_INDEX);
    final String overview = cursor.getString(FavouriteMovieListFragment.OVERVIEW_INDEX);
    final Uri posterCompleteUri =
        Uri.parse(cursor.getString(FavouriteMovieListFragment.POSTER_URI_INDEX));
    final Uri backdropCompleteUri =
        Uri.parse(cursor.getString(FavouriteMovieListFragment.BACKDROP_URI_INDEX));
    final float voteAverage = cursor.getFloat(FavouriteMovieListFragment.VOTE_AVERAGE_INDEX);
    final int voteCount = cursor.getInt(FavouriteMovieListFragment.VOTE_COUNT_INDEX);

    return new Movie.MovieBuilder()
        .setId(id)
        .setOriginalTitle(originalTitle)
        .setReleaseDate(releaseYear)
        .setOverview(overview)
        .setPosterUri(posterCompleteUri)
        .setBackdropUri(backdropCompleteUri)
        .setVoteAverage(voteAverage)
        .setVoteCount(voteCount)
        .build();
  }
}
