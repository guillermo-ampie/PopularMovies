package com.ampie_guillermo.popularmovies.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.ui.adapter.MovieAdapter.MovieViewHolder;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Objects;


public class MovieAdapter extends ListAdapter<Movie, MovieViewHolder> {

  private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
  private static final DiffUtil.ItemCallback<Movie> DIFF_CALLBACK =
      new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(Movie oldItem, Movie newItem) {
          return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
          return oldItem.getId().equals(newItem.getId());
        }
      };
  //  private ArrayList<Movie> mMovieList;
  private final MovieItemClickListener mOnClickListener;

  public MovieAdapter(MovieItemClickListener onClickListener) {
    super(DIFF_CALLBACK);
    mOnClickListener = onClickListener;
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
   * @param viewType The divider_view_1 type of the new View.
   * @return A new ViewHolder that holds a View of the given divider_view_1 type.
   * @see #getItemViewType(int) see #onBindViewHolder(ViewHolder, int)
   */
  @Override
  public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_movie_poster, parent, false);

    return new MovieViewHolder(view, mOnClickListener);
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
  public void onBindViewHolder(MovieViewHolder holder, int position) {

//    Movie currentMovie = get(position);
    Movie currentMovie = getItem(position);
    holder.setupItemView(currentMovie);

  }

//  /**
//   * Returns the total number of items in the data set held by the adapter.
//   *
//   * @return The total number of items in this adapter.
//   */
//  @Override
//  public int getItemCount() {
//    return (mMovieList != null) ? mMovieList.size() : 0;
//  }

  public void setMovieList(ArrayList<Movie> movieList) {
    //Set the new data & update the UI
//    mMovieList = new ArrayList<>(movieList);
//    notifyDataSetChanged();
    submitList(movieList);
  }

  @FunctionalInterface
  public interface MovieItemClickListener {

    void onMovieItemClick(int clickedItemIndex);
  }

  // The View Holder used for each movie poster
  static class MovieViewHolder extends RecyclerView.ViewHolder {

    private final Drawable mPlaceholderDrawable;
    private final Drawable mPlaceholderDrawableError;
    private final MovieItemClickListener mOnClickListener;
    private final ImageView mMovieThumbnailView;

    MovieViewHolder(View view, MovieItemClickListener onClickListener) {
      super(view);
      mMovieThumbnailView = view.findViewById(R.id.image_movie_poster_movie_poster);
      mOnClickListener = onClickListener;
      /*
        The hack with the variable "placeholderDrawable" is needed to support -vector drawables- on
        API level < 21 (Lollipop)
        Reference: https://github.com/square/picasso/issues/1109, see
        entry: "ncornette commented on Jun 27, 2016"
      */
      mPlaceholderDrawable = ResourcesCompat
          .getDrawable(itemView.getResources(), R.drawable.ic_movie_black_237x180dp, null);
      mPlaceholderDrawableError = ResourcesCompat.getDrawable(itemView.getResources(),
          R.drawable.ic_broken_image_black_237x180dp,
          null);

      mMovieThumbnailView.setOnClickListener(
          v -> mOnClickListener.onMovieItemClick(getAdapterPosition()));
    }

    void setupItemView(Movie currentMovie) {
      Picasso.get()
//      Picasso.with(itemView.getContext())
          .load(currentMovie.getPosterUri())
          .placeholder(mPlaceholderDrawable)
          .error(mPlaceholderDrawableError)
          .into(mMovieThumbnailView);
    }
  }
}
