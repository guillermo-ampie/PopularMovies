package com.ampie_guillermo.popularmovies.network;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.ampie_guillermo.popularmovies.BuildConfig;
import com.ampie_guillermo.popularmovies.model.Movie;
import com.ampie_guillermo.popularmovies.ui.MovieAdapter;
import com.ampie_guillermo.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final MovieAdapter mMovieAdapter;
    private final Context mContext;

    public FetchMovieTask(Context context, MovieAdapter movieAdapter) {
        mContext = context;
        mMovieAdapter = movieAdapter;
    }

    /**
     * Take the string representing the complete movies list in JSON format and
     * pull out the relevant data needed to construct the movie objects.
     */
    private Movie[] getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects we need to extract.
        final String MOVIE_RESULTS = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_VOTE_AVERAGE = "vote_average";
        final String MOVIE_VOTE_COUNT = "vote_count";
        String MOVIE_POSTER_BASE_URI = "https://image.tmdb.org/t/p/w";

        Resources res = mContext.getResources();
        int moviePosterWidthInPixels = (int) (res.getDimension(R.dimen.movie_poster_width)
                / res.getDisplayMetrics().density);

        /**
         * We need to specify in the HTTPS request and the XML files
         * the --movie poster width--, so to avoid a manual
         * synchronization (so error prone!) of both files, we are using
         * "res/values/dimens:movie_poster_width" resource for this purpose.
         * In the https request we need to use the nominal value stored in
         * "movie_poster_width", not the scaled value returned by
         * getDimension(), so we must -adjust back- by the screen density factor
         */
        MOVIE_POSTER_BASE_URI += String.valueOf(moviePosterWidthInPixels);

        JSONObject movieJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = movieJson.getJSONArray(MOVIE_RESULTS);

        Movie[] resultMovies = new Movie[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {

            // Get the JSON object with the movie data
            JSONObject currentMovieJson;

            String movieID;
            String movieOriginalTitle;
            String movieReleaseDate;
            String movieOverview;
            String moviePosterRelativePath;
            Uri moviePosterCompleteUri;
            double movieVoteAverage;
            int movieVoteCount;

            currentMovieJson = moviesArray.getJSONObject(i);
            movieID = Integer.toString(currentMovieJson.getInt(MOVIE_ID));
            movieOriginalTitle = currentMovieJson.getString(MOVIE_ORIGINAL_TITLE);
            movieReleaseDate = currentMovieJson.getString(MOVIE_RELEASE_DATE);
            movieOverview = currentMovieJson.getString(MOVIE_OVERVIEW);
            moviePosterRelativePath = currentMovieJson.getString(MOVIE_POSTER_PATH);
            movieVoteAverage = currentMovieJson.getDouble(MOVIE_VOTE_AVERAGE);
            movieVoteCount = currentMovieJson.getInt(MOVIE_VOTE_COUNT);

            /**
             * Store only the movie release year.
             * The format from TheMovieDB for release date: YYYY-MM-DD
             */
            String movieReleaseYear = (movieReleaseDate.split("-"))[0];

            moviePosterCompleteUri = Uri.withAppendedPath(
                    Uri.parse(MOVIE_POSTER_BASE_URI), moviePosterRelativePath);

            //Populate our array of movies
            resultMovies[i] = new Movie(movieID,
                    movieOriginalTitle,
                    movieReleaseYear,
                    movieOverview,
                    moviePosterCompleteUri,
                    movieVoteAverage,
                    movieVoteCount);

            //Log.e (LOG_TAG, movieOriginalTitle);
        }
        return resultMovies;
    }

    @Override
    protected Movie[] doInBackground(String... params) {
        // TODO: Give feedback to user with SnackBar
        // Verify size of params and that the sorting method is not null
        if (params.length == 0 || params[0] == null) {
            Log.e(LOG_TAG, "Missing sorting method in https query");
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // The raw JSON response as a string.
        String movieJsonStr = null;

        try {
            // Construct the URL for TheMovieDB query
            final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
            final String SORT_BY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            final String VOTE_COUNT_PARAM = "vote_count.gte";
            final String VOTE_COUNT = "100"; // Let's get some sense from the valuations

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, params[0])
                    .appendQueryParameter(VOTE_COUNT_PARAM, VOTE_COUNT)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            //Log.e(LOG_TAG, builtUri.toString());

            // Create the request to TheMovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Oops, we got nothing.
                Log.e(LOG_TAG, "Empty response from TheMovieDB server");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Append a newline for debugging purposes
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  Nothing to do!
                Log.e(LOG_TAG, "Empty response from TheMovieDB server");
                return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error contacting TheMovieDB server", e);
            // If we didn't successfully get the movie list, there's nothing to do
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMoviesDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the response.
        Log.e(LOG_TAG, "Error contacting TheMovieDB server / parsing response");
        return null;
    }

    @Override
    protected void onPostExecute(Movie[] result) {
        if (result != null) {
            // New data from the server
            mMovieAdapter.clear();
            for (Movie currentMovie : result) {
                mMovieAdapter.add(currentMovie);
            }
        }
    }
}
