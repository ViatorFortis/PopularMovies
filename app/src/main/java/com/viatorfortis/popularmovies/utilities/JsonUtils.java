package com.viatorfortis.popularmovies.utilities;


import android.content.Context;

import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.viatorfortis.popularmovies.R;
import com.viatorfortis.popularmovies.models.Movie;
import com.viatorfortis.popularmovies.models.MovieListPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;


public class JsonUtils {
    public static List<Movie> parseMovieListJson(String json, Context context)
            throws JsonSyntaxException {

        /* the following code throws JSONException

        JSONObject movieListPage = new JSONObject(json);
        JSONArray movieList = movieListPage.getJSONArray(context.getString(R.string.json_page_results_field) );
        String jsonMovieList = movieList.toString();

        Gson gson = new Gson();
        Type moviesListType = new TypeToken<List<Movie>>(){}.getType();

        return gson.fromJson(jsonMovieList, moviesListType);
        */

        Gson gson = new Gson();
        MovieListPage movieListPage = gson.fromJson(json, MovieListPage.class);

        return movieListPage.mMovieList;
    }
}
