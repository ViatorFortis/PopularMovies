package com.viatorfortis.popularmovies.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.viatorfortis.popularmovies.R;
import com.viatorfortis.popularmovies.models.Movie;
import com.viatorfortis.popularmovies.rv.MovieAdapter;
import com.viatorfortis.popularmovies.utilities.JsonUtils;
import com.viatorfortis.popularmovies.utilities.MoviesSharedPreferences;
import com.viatorfortis.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks,
        MovieAdapter.GridItemClickListener {

    //private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static String mSortingPreference;

    private boolean mIsLoading = true;

    private static final int MOVIE_LIST_LOADER_ID = 13;

    private static final int GRID_THRESHOLD_ROW_COUNT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting toolbar as app bar
        Toolbar appBar = findViewById(R.id.tb_appbar);
        setSupportActionBar(appBar);

        mSortingPreference = MoviesSharedPreferences.getPreferredSortType(this);

        // Loader initialization
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader <List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LIST_LOADER_ID);

        //loaderManager.initLoader(MOVIE_LIST_LOADER_ID, new Bundle(), this).forceLoad();
        if (movieLoader == null){
            loaderManager.initLoader(MOVIE_LIST_LOADER_ID, new Bundle(),this).forceLoad();
        }else{
            loaderManager.restartLoader(MOVIE_LIST_LOADER_ID, new Bundle(),this).forceLoad();
        }

        List<Movie> moviesList = new ArrayList<>();

        // RecyclerView initialization
        RecyclerView recyclerView = findViewById(R.id.rv_movies);

        mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MovieAdapter(moviesList, this);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int thresholdItemCount = GRID_THRESHOLD_ROW_COUNT * ( (GridLayoutManager) mLayoutManager).getSpanCount();

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();

                if (!mIsLoading) {
                    if ( (lastVisibleItemPosition + thresholdItemCount) >= totalItemCount
                         && lastVisibleItemPosition >= 0) {
                        mIsLoading = true;
                        loadMoviesIntoAdapter(false);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        MoviesSharedPreferences.setPreferredSortType(this, mSortingPreference);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_types_menu, menu);

        if(mSortingPreference.equals(getString(R.string.movies_popularity_sorting) ) ) {
            menu.findItem(R.id.action_sort_by_popular).setChecked(true);
        } else{
            if(mSortingPreference.equals(getString(R.string.movies_rating_sorting) ) ) {
                menu.findItem(R.id.action_sort_by_top_rated).setChecked(true);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);

        String newSortPref;

        switch (item.getItemId() ) {
            case R.id.action_sort_by_popular:
                newSortPref = getString(R.string.movies_popularity_sorting);
                break;

            case R.id.action_sort_by_top_rated:
                newSortPref = getString(R.string.movies_rating_sorting);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        if (!mSortingPreference.equals(newSortPref)) {
            mSortingPreference = newSortPref;
            loadMoviesIntoAdapter(true);
            return true;
        } else {
            return false;
        }
    }



    private void loadMoviesIntoAdapter(boolean clearListBeforeAddition) {
        if(clearListBeforeAddition) {
            mAdapter.resetMoviesList();
        }

        getSupportLoaderManager().getLoader(MOVIE_LIST_LOADER_ID).forceLoad();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

//                if (mData != null) {
//                    // Use cached data
//                    deliverResult(mData);
//                } else {
//                    // We have no data, so kick off loading it
//                    forceLoad();
//                }
            }

            @Override
            public List<Movie> loadInBackground() {
                String sortingEndpoint;

                if (mSortingPreference.equals(getString(R.string.movies_popularity_sorting))) {
                    sortingEndpoint = NetworkUtils.POPULARITY_SORTING_ENDPOINT;
                } else {
                    sortingEndpoint = NetworkUtils.TOP_RATED_SORTING_ENDPOINT;
                }

                try {
                    String MovieListPageJSON = NetworkUtils.getMovieListPageJSON(getContext(), sortingEndpoint, mAdapter.getNextLoadedPageNumber() );

                    if (!MovieListPageJSON.isEmpty()) {
                        return JsonUtils.parseMovieListJson(MovieListPageJSON, getContext() );
                    }
                } catch (IOException e) {
                    Log.d(e.getClass().getName(), e.getMessage());
                } catch (JsonSyntaxException e) {
                    Log.d(e.getClass().getName(), e.getMessage());
                }

                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Object object) {
        mIsLoading = false;

        if (object != null && object instanceof ArrayList) {
            mAdapter.addMoviesList((ArrayList<Movie>) object);
        } else {
            Toast.makeText(this, getString(R.string.movies_list_not_loaded_toast), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    @Override
    public void onGridItemClick(int adapterPosition) {
        //Toast.makeText(this, String.valueOf(adapterPosition), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(getString(R.string.movie_parcel_key), mAdapter.getMovie(adapterPosition) );

        startActivity(intent);
    }
}
