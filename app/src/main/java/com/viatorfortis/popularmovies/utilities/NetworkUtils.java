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
    private final static String MOVIES_LIST_BASE_URL =
            "https://api.themoviedb.org/3/movie";

    public final static String POPULARITY_SORTING_ENDPOINT =
            "popular";

    public final static String TOP_RATED_SORTING_ENDPOINT =
            "top_rated";

    private final static String MOVIE_POSTER_BASE_URL =
            "https://image.tmdb.org/t/p";

    private final static String REVIEWS_SEGMENT = "reviews";

    private final static String PAGE_PARAMETER = "page";

    private final static String VIDEOS_SEGMENT = "videos";

    private final static String API_KEY_PARAMETER = "api_key";

    private final static String YOUTUBE_VIDEO_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi";

    private final static String FIRST_JPEG_SEGMENT = "0.jpg";

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
        return Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendEncodedPath(sizeEndPoint + posterPath)
                .build();
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {
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

    private static URL buildMovieReviewListURL(Context context, int movieId, int pageNumber) {
        Uri uri = Uri.parse(MOVIES_LIST_BASE_URL).buildUpon()
                .appendEncodedPath(String.valueOf(movieId) )
                .appendEncodedPath(REVIEWS_SEGMENT)
                .appendQueryParameter(API_KEY_PARAMETER, context.getString(R.string.api_key) )
                .appendQueryParameter(PAGE_PARAMETER, String.valueOf(pageNumber) )
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString() );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getMovieReviewListPageJSON(Context context, int movieId, int pageNumber)
            throws IOException {
        URL url = buildMovieReviewListURL(context, movieId, pageNumber);
        return getResponseFromHttpUrl(url);
    }

    private static URL buildMovieVideoListURL (Context context, int movieId) {
        Uri uri = Uri.parse(MOVIES_LIST_BASE_URL).buildUpon()
                .appendEncodedPath(String.valueOf(movieId) )
                .appendEncodedPath(VIDEOS_SEGMENT)
                .appendQueryParameter(API_KEY_PARAMETER, context.getString(R.string.api_key) )
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString() );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getMovieVideoListJSON(Context context, int movieId)
            throws IOException {
        URL url = buildMovieVideoListURL(context, movieId);
        return getResponseFromHttpUrl(url);
    }

    public static Uri buildYoutubeVideoThumbnailURL(String movieVideoKey) {
        return Uri.parse(YOUTUBE_VIDEO_THUMBNAIL_BASE_URL).buildUpon()
                .appendEncodedPath(String.valueOf(movieVideoKey) )
                .appendEncodedPath(FIRST_JPEG_SEGMENT)
                .build();
    }
}
