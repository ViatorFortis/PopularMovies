package com.viatorfortis.popularmovies.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
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
import com.viatorfortis.popularmovies.db.MovieContract;
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

    private MovieAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static String mSortingPreference;

    private boolean mIsLoading = true;

    private static final int MOVIE_LIST_LOADER_ID = 13;

    private static final int GRID_THRESHOLD_ROW_COUNT = 5;

    private static final int GRID_SPAN_COUNT = 2;

    private static final int FAVOURITE_MOVIE_LIST_LOADER_ID = 14;

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

        ArrayList<Movie> movieList;

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies") ) {
            movieList = new ArrayList<>();

            if (mSortingPreference.equals(getString(R.string.movies_popularity_sorting) )
                    || mSortingPreference.equals(getString(R.string.movies_rating_sorting) ) ) {
                loaderManager.restartLoader(MOVIE_LIST_LOADER_ID, new Bundle(),this).forceLoad();
            } else {
                loaderManager.restartLoader(FAVOURITE_MOVIE_LIST_LOADER_ID, new Bundle(), this).forceLoad();
            }
        } else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
            mIsLoading = false;

            if (mSortingPreference.equals(getString(R.string.movies_popularity_sorting) )
                    || mSortingPreference.equals(getString(R.string.movies_rating_sorting) ) ) {
                loaderManager.restartLoader(MOVIE_LIST_LOADER_ID, new Bundle(),this);
                loaderManager.destroyLoader(FAVOURITE_MOVIE_LIST_LOADER_ID);
            } else {
                loaderManager.restartLoader(FAVOURITE_MOVIE_LIST_LOADER_ID, new Bundle(), this);
                loaderManager.destroyLoader(MOVIE_LIST_LOADER_ID);
            }
        }

        RecyclerView recyclerView = findViewById(R.id.rv_movies);
        initMovieListRecyclerView(recyclerView, movieList);
    }

    private void initMovieListRecyclerView(RecyclerView recyclerView, ArrayList<Movie> movieList) {
        mLayoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MovieAdapter(movieList, this);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private final int thresholdItemCount = GRID_THRESHOLD_ROW_COUNT * ( (GridLayoutManager) mLayoutManager).getSpanCount();

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mSortingPreference.equals(getString(R.string.favourite_movies_list) ) ) {
                    super.onScrolled(recyclerView, dx, dy);

                    int totalItemCount = mLayoutManager.getItemCount();
                    int lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();

                    if (!mIsLoading) {
                        if ((lastVisibleItemPosition + thresholdItemCount) >= totalItemCount
                                && lastVisibleItemPosition >= 0) {
                            mIsLoading = true;
                            loadMoviesIntoAdapter(false);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putParcelableArrayList("movies", mAdapter.getMovieList());
        } catch (Exception e) {
            Log.d(e.getClass().getName(), e.getMessage());
        }
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
            setTitle(getString(R.string.app_name) + getString(R.string.movies_popularity_sorting_app_title_suffix) );
        }
        else if (mSortingPreference.equals(getString(R.string.movies_rating_sorting) ) ) {
            menu.findItem(R.id.action_sort_by_top_rated).setChecked(true);
            setTitle(getString(R.string.app_name) + getString(R.string.movies_rating_sorting_app_title_suffix) );
        }
        else if (mSortingPreference.equals(getString(R.string.favourite_movies_list) ) ) {
            menu.findItem(R.id.action_show_favourite).setChecked(true);
            setTitle(getString(R.string.app_name) + getString(R.string.favourite_list_app_title_suffix) );
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
                setTitle(getString(R.string.app_name) + getString(R.string.movies_popularity_sorting_app_title_suffix) );
                break;

            case R.id.action_sort_by_top_rated:
                newSortPref = getString(R.string.movies_rating_sorting);
                setTitle(getString(R.string.app_name) + getString(R.string.movies_rating_sorting_app_title_suffix) );
                break;

            case R.id.action_show_favourite:
                newSortPref = getString(R.string.favourite_movies_list);
                setTitle(getString(R.string.app_name) + getString(R.string.favourite_list_app_title_suffix) );
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

        if (mSortingPreference.equals(getString(R.string.movies_popularity_sorting) )
                || mSortingPreference.equals(getString(R.string.movies_rating_sorting) ) ) {
            getSupportLoaderManager().restartLoader(MOVIE_LIST_LOADER_ID, new Bundle(), this).forceLoad();
            getSupportLoaderManager().destroyLoader(FAVOURITE_MOVIE_LIST_LOADER_ID);

        } else {
            getSupportLoaderManager().restartLoader(FAVOURITE_MOVIE_LIST_LOADER_ID, new Bundle(), this).forceLoad();
            getSupportLoaderManager().destroyLoader(MOVIE_LIST_LOADER_ID);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        if (id == MOVIE_LIST_LOADER_ID) {
            return new AsyncTaskLoader<List<Movie>>(this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
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
                        String movieListPageJSON = NetworkUtils.getMovieListPageJSON(getContext(), sortingEndpoint, MovieAdapter.getNextLoadedPageNumber());

                        if (!movieListPageJSON.isEmpty()) {
                            return JsonUtils.parseMovieListJson(movieListPageJSON);
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

        else if (id == FAVOURITE_MOVIE_LIST_LOADER_ID) {
            Uri favouriteMovieUri = MovieContract.FavouriteMoviesEntry.CONTENT_URI;

            return new CursorLoader(this,
                    favouriteMovieUri,
                    MovieContract.FavouriteMoviesEntry.FULL_PROJECTION,
                    null,
                    null,
                    MovieContract.FavouriteMoviesEntry._ID
                    );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object object) {
        mIsLoading = false;

        ArrayList<Movie> movieList;

        switch (loader.getId() ) {
            case MOVIE_LIST_LOADER_ID:
                if (object != null
                         && object instanceof ArrayList) {
                    movieList = (ArrayList<Movie>) object;

                    if (movieList.size() > 0) {
                        mAdapter.addMoviesList(movieList);
                    } else {
                        Toast.makeText(this, getString(R.string.no_tmdb_movie_found), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.tmdb_movies_loading_error_toast), Toast.LENGTH_LONG).show();
                }
                break;

            case FAVOURITE_MOVIE_LIST_LOADER_ID:
                if (object != null
                        && object instanceof Cursor) {
                    Cursor cursor = (Cursor) object;

                    movieList = new ArrayList<>();

                    if (cursor.getCount() > 0) {
                        extractFavouriteMovieList(movieList, cursor);
                    } else {
                        Toast.makeText(this, getString(R.string.no_favourite_movie_found), Toast.LENGTH_LONG).show();
                    }
                    mAdapter.resetMoviesList();
                    mAdapter.addMoviesList(movieList);
                } else {
                    Toast.makeText(this, getString(R.string.favourite_movies_loading_error_toast), Toast.LENGTH_LONG).show();
                }
                break;

            default:
                Toast.makeText(this, getString(R.string.undefined_loader), Toast.LENGTH_LONG).show();
        }
    }

    private void extractFavouriteMovieList(ArrayList<Movie> movieList, Cursor cursor) {
        final int idColIndex = cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry._ID);
        final int titleColIndex = cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_NAME_TITLE);
        final int releaseDateColIndex = cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_NAME_RELEASEDATE);
        final int posterURLColIndex = cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_NAME_POSTERPATH);
        final int voteAverageColIndex = cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_NAME_VOTEAVERAGE);
        final int plotSynopsisColIndex = cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_NAME_PLOTSYNOPSIS);

        while (cursor.moveToNext() ) {
            int id = cursor.getInt(idColIndex);
            String title = cursor.getString(titleColIndex);
            String releaseDate = cursor.getString(releaseDateColIndex);
            String posterURL = cursor.getString(posterURLColIndex);
            float voteAverage = cursor.getFloat(voteAverageColIndex);
            String plotSynopsis = cursor.getString(plotSynopsisColIndex);

            Movie movie = new Movie(id, title, releaseDate, posterURL, voteAverage, plotSynopsis);
            movieList.add(movie);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {}

    @Override
    public void onGridItemClick(int adapterPosition) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(getString(R.string.movie_parcel_key), mAdapter.getMovie(adapterPosition) );

        startActivity(intent);
    }
}
