package com.ampie_guillermo.popularmovies.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.MovieReviewList;
import com.ampie_guillermo.popularmovies.model.MovieReviewList.MovieReview;


public class MovieReviewAdapter
    extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

  private static final String LOG_TAG = MovieReviewAdapter.class.getSimpleName();
  private MovieReviewList mMoviewReviewList;

  public MovieReviewAdapter() {
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
  public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.item_movie_review, parent, false);

    return new MovieReviewViewHolder(view);

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
   * <p>
   * Override {link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
   * handle efficient partial bind.
   *
   * @param holder The ViewHolder which should be updated to represent the contents of the item at
   * the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   */
  @Override
  public void onBindViewHolder(MovieReviewAdapter.MovieReviewViewHolder holder, int position) {

    MovieReview movieReview = mMoviewReviewList.getReviewList().get(position);
    holder.setItemView(movieReview);
  }

  /**
   * Returns the total number of items in the data set held by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override
  public int getItemCount() {

    return (mMoviewReviewList != null) ? mMoviewReviewList.getReviewList().size() : 0;
  }

  void setMovieReviewList(MovieReviewList reviews) {
    // set the new data & update the UI
    mMoviewReviewList = reviews;
    notifyDataSetChanged();
  }

  static class MovieReviewViewHolder extends RecyclerView.ViewHolder {

    private final TextView mAuthorView;
    private final TextView mReviewContentView;

    MovieReviewViewHolder(View view) {
      super(view);
      mAuthorView = view.findViewById(R.id.text_movie_review_review_author);
      mReviewContentView = view.findViewById(R.id.text_movie_review_review_content);
    }

    void setItemView(MovieReview movieReview) {
      mAuthorView.setText(movieReview.getAuthor());
      mReviewContentView.setText(movieReview.getContent());
    }
  }
}
