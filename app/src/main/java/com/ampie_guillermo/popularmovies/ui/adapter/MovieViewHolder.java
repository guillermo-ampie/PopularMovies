package com.ampie_guillermo.popularmovies.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.ampie_guillermo.popularmovies.R;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.utils.DrawablePlaceholderSingleton;
import com.squareup.picasso.Picasso;

// The View Holder used for each movie poster
public class MovieViewHolder extends RecyclerView.ViewHolder {
  private final Drawable drawablePlaceholder;
  private final Drawable drawableErrorPlaceHolder;
  private final MovieItemClickListener onClickListener;
  private final ImageView movieThumbnailView;

  MovieViewHolder(final View view, final MovieItemClickListener onItemClickListener) {
    super(view);

    final DrawablePlaceholderSingleton placeholders =
        DrawablePlaceholderSingleton.getInstance(itemView.getResources());

    drawablePlaceholder = placeholders.getDrawablePlaceHolder();
    drawableErrorPlaceHolder = placeholders.getDrawablePlaceHolder();
    movieThumbnailView = view.findViewById(R.id.image_movie_poster_movie_poster);

    onClickListener = onItemClickListener;
    movieThumbnailView.setOnClickListener(
        v -> onClickListener.onMovieItemClick(getAdapterPosition()));
  }

  void setupItemView(final Movie currentMovie) {
    // See comment in DrawablePlaceholderSingleton() to allow vector drawables in
    // API level < 21 (Lollipop)
    Picasso.get()
        .load(currentMovie.getPosterUri())
        .placeholder(drawablePlaceholder)
        .error(drawableErrorPlaceHolder)
        .into(movieThumbnailView);
  }
}
