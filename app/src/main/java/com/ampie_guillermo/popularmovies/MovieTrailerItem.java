package com.ampie_guillermo.popularmovies;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

/**
 * RecyclerView library and code reference: FastAdapter
 * http://mikepenz.github.io/FastAdapter
 */

public class MovieTrailerItem extends AbstractItem<MovieTrailerItem, MovieTrailerItem.ViewHolder> {

    private final String LOG_TAG = MovieTrailerItem.class.getSimpleName();
    private static final String THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    private static final String THUMBNAIL_IMAGE_TYPE = "/mqdefault.jpg";

    private MovieTrailerList.MovieTrailer mMovieTrailer;

    public MovieTrailerItem(MovieTrailerList.MovieTrailer mMovieTrailer) {
        this.mMovieTrailer = mMovieTrailer;
    }

    // The ID for the movie trailer item
    @Override
    public int getType() {
        return R.id.movie_trailer_item_id;
    }

    // The layout for the movie trailer item
    @Override
    public int getLayoutRes() {
        return R.layout.movie_trailer_item;
    }

    @Override
    public void bindView(ViewHolder viewHolder) {
        super.bindView(viewHolder);

        Uri thumbnailUri
                = Uri.withAppendedPath(Uri.parse(THUMBNAIL_BASE_URL),
                                                 mMovieTrailer.getKey() + THUMBNAIL_IMAGE_TYPE);
        Log.e (LOG_TAG, thumbnailUri.toString());
        // Show movie trailer's thumbnail
        Picasso.with(viewHolder.itemView.getContext()) // itemView is a member of class ViewHolder
                .load(thumbnailUri)
                .placeholder(R.drawable.no_thumbnail)
                .error(R.drawable.no_thumbnail)
                //.resize(320, 180)
                //.centerInside()
                .fit()
                .into(viewHolder.mThumbnailView);

        // Show the movie trailer's name
        //viewHolder.mNameView.setText(mMovieTrailer.getName());
    }

    // The viewHolder used for the movie trailer
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameView;
        private ImageView mThumbnailView;

        public ViewHolder(View view) {
            super(view);
            mThumbnailView = (ImageView) view.findViewById(R.id.movie_trailer_thumbnail);
            //mNameView      = (TextView) view.findViewById(R.id.movie_trailer_name);
        }
    }
}
