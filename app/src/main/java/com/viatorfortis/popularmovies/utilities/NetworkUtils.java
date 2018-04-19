package com.viatorfortis.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;

import com.viatorfortis.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {
    final static String MOVIES_LIST_BASE_URL =
            "https://api.themoviedb.org/3";

    public final static String POPULARITY_SORTING_ENDPOINT =
            "movie/popular";

    public final static String TOP_RATED_SORTING_ENDPOINT =
            "movie/top_rated";

    final static String MOVIE_POSTER_BASE_URL =
            "https://image.tmdb.org/t/p";

    final static String API_KEY_PARAMETER = "api_key";

    public static String getMovieListPageJSON(Context context, String sortingEndpoint, int nextLoadedPageNumber)
            throws IOException {
        URL url = buildMoviesListURL(context, sortingEndpoint, nextLoadedPageNumber);
        return getResponseFromHttpUrl(url);
    }

    private static URL buildMoviesListURL(Context context, String sortingEndpoint, int nextLoadedPageNumber) {
        Uri uri = Uri.parse(MOVIES_LIST_BASE_URL).buildUpon()
                .appendEncodedPath(sortingEndpoint)
                .appendQueryParameter(API_KEY_PARAMETER, context.getString(R.string.api_key) )
                .appendQueryParameter("page", String.valueOf(nextLoadedPageNumber) )
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString() );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static Uri buildMoviePosterURL(String sizeEndPoint, String posterPath) {
        Uri uri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendEncodedPath(sizeEndPoint + posterPath)
                .build();

        return uri;

        /*Uri uri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendPath(sizeEndPoint)
                .appendPath(posterPath)
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString() );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;*/
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
