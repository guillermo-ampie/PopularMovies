package com.ampie_guillermo.popularmovies.database;

import android.net.Uri;
import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Database & Content Provider support with Schematic
 * https://github.com/SimonVT/schematic.git - Automatically generate a
 * ContentProvider backed by an SQLite database.
 */

@ContentProvider(authority = MoviesProvider.AUTHORITY, database = MoviesDatabase.class)
public final class MoviesProvider {

  public static final String AUTHORITY = "com.ampie_guillermo.popularmovies.MoviesProvider";
  private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

  private MoviesProvider() {
  }

  private static Uri buildUri(final String... paths) {
    final Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
    for (final String path : paths) {
      builder.appendPath(path);
    }
    return builder.build();
  }

  private interface Path {

    String MOVIES = "movies";
    String FROM_MOVIE = "frommovie";
    String REVIEWS = "reviews";
    String TRAILERS = "trailers";
  }

  @TableEndpoint(table = MoviesDatabase.MOVIES_TABLE)
  public static final class Movies {

    @ContentUri(
        path = Path.MOVIES,
        type = "vnd.android.cursor.dir/movie")
    public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

    private Movies() {
    }

    @InexactContentUri(
        name = "MOVIE_ID",
        path = Path.MOVIES + "/#",
        type = "vnd.android.cursor.item/movie",
        whereColumn = MovieColumns.MOVIE_ID,
        pathSegment = 1)
    public static Uri withId(final String id) {
      return buildUri(Path.MOVIES, id);
    }
  }

  @TableEndpoint(table = MoviesDatabase.MOVIE_REVIEWS_TABLE)
  public static final class MovieReviews {

    private MovieReviews() {
    }

    @InexactContentUri(
        name = "REVIEWS_FROM_MOVIE",
        path = Path.REVIEWS + "/" + Path.FROM_MOVIE + "/#",
        type = "vnd.android.cursor.dir/review",
        whereColumn = MovieReviewColumns.MOVIE_ID,
        pathSegment = 2)
    public static Uri fromMovie(final String id) {
      return buildUri(Path.REVIEWS, Path.FROM_MOVIE, id);
    }
  }

  @TableEndpoint(table = MoviesDatabase.MOVIE_TRAILERS_TABLE)
  public static final class MovieTrailers {

    private MovieTrailers() {
    }

    @InexactContentUri(
        name = "TRAILERS_FROM_MOVIE",
        path = Path.TRAILERS + "/" + Path.FROM_MOVIE + "/#",
        type = "vnd.android.cursor.dir/trailer",
        whereColumn = MovieReviewColumns.MOVIE_ID,
        pathSegment = 2)
    public static Uri fromMovie(final String id) {
      return buildUri(Path.TRAILERS, Path.FROM_MOVIE, id);
    }
  }
}
