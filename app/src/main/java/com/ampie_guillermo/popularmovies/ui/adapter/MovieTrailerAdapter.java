package com.ampie_guillermo.popularmovies.ui.adapter;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.MovieTrailerList;
import com.ampie_guillermo.popularmovies.utils.DrawablePlaceholderSingleton;
import com.squareup.picasso.Picasso;


public class MovieTrailerAdapter extends
    RecyclerView.Adapter<MovieTrailerAdapter.TrailerViewHolder> {

  private static final String LOG_TAG = MovieTrailerAdapter.class.getSimpleName();
  private static final String THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
  private static final String THUMBNAIL_IMAGE_TYPE = "/hqdefault.jpg";
  private final MovieTrailerItemClickListener onClickListener;
  private MovieTrailerList mMovieTrailerList;

  public MovieTrailerAdapter(final MovieTrailerItemClickListener listener) {
    onClickListener = listener;
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
   * @see #getItemViewType(int) see onBindViewHolder(ViewHolder, int)
   */
  @Override
  public MovieTrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(R.layout.item_movie_trailer_thumbnail, parent, false);

    return new TrailerViewHolder(view, onClickListener);
  }

  /**
   * Called by RecyclerView to display the data at the specified position. This method should
   * update the contents of the {link ViewHolder#itemView} to reflect the item at the given
   * position.
   * <p>
   * Note that unlike {@link ListView}, RecyclerView will not call this method
   * again if the position of the item changes in the data set unless the item itself is
   * invalidated or the new position cannot be determined. For this reason, you should only
   * use the {@code position} parameter while acquiring the related data item inside
   * this method and should not keep a copy of it. If you need the position of an item later
   * on (e.g. in a click listener), use {link ViewHolder#getAdapterPosition()} which will
   * have the updated adapter position.
   * <p>
   * Override {link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
   * handle efficient partial bind.
   *
   * @param holder The ViewHolder which should be updated to represent the contents of the item at
   * the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   */
  @Override
  public void onBindViewHolder(MovieTrailerAdapter.TrailerViewHolder holder, int position) {
    final Uri thumbnailUri = Uri.withAppendedPath(Uri.parse(THUMBNAIL_BASE_URL),
        mMovieTrailerList.getTrailerList().get(position).getKey()
            + THUMBNAIL_IMAGE_TYPE);

    // Show trailer thumbnail image
    holder.setupItemView(thumbnailUri);
  }


  /**
   * Returns the total number of items in the data set held by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override
  public int getItemCount() {
    return (mMovieTrailerList == null) ? 0 : mMovieTrailerList.getTrailerList().size();
  }

  public void setMovieTrailerList(final MovieTrailerList trailerList) {
    // set the new data & update the UI
    mMovieTrailerList = trailerList;
    notifyDataSetChanged();
  }

  @FunctionalInterface
  public interface MovieTrailerItemClickListener {

    void onMovieTrailerItemClick(int clickedItemIndex);
  }

  // The viewHolder used for each movie trailer
  /* package */ static class TrailerViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private final Drawable drawablePlaceholder;
    private final Drawable drawableErrorPlaceHolder;
    private final MovieTrailerItemClickListener onClickListener;

    // This class uses "implements View.OnClickListener" instead of using a member variable as in
    // MovieViewHolder
    private final ImageView mTrailerThumbnailView;

    /* package */ TrailerViewHolder(final View view, final MovieTrailerItemClickListener listener) {
      super(view);

      final DrawablePlaceholderSingleton placeholders =
          DrawablePlaceholderSingleton.getInstance(itemView.getResources());

      drawablePlaceholder = placeholders.getDrawablePlaceHolder();
      drawableErrorPlaceHolder = placeholders.getDrawablePlaceHolder();
      mTrailerThumbnailView =
          view.findViewById(R.id.image_movie_trailer_thumbnail_trailer_thumbnail);
      onClickListener = listener;
      view.setOnClickListener(this);
//      mTrailerThumbnailView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      // getAdapterPosition() gives us the the item that was clicked
      onClickListener.onMovieTrailerItemClick(getAdapterPosition());
    }

    /* package */ void setupItemView(final Uri trailerThumbnailUri) {
      // See comment in DrawablePlaceholderSingleton() to allow vector drawables in
      // API level < 21 (Lollipop)
      Picasso.get()
          .load(trailerThumbnailUri)
          .placeholder(drawablePlaceholder)
          .error(drawableErrorPlaceHolder)
          .fit()
          .into(mTrailerThumbnailView);
    }
  }
}
