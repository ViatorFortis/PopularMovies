package com.viatorfortis.popularmovies.ui;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;
import com.viatorfortis.popularmovies.R;
import com.viatorfortis.popularmovies.db.MovieContract;
import com.viatorfortis.popularmovies.models.Movie;
import com.viatorfortis.popularmovies.models.MovieReview;
import com.viatorfortis.popularmovies.models.MovieVideo;
import com.viatorfortis.popularmovies.models.MovieVideoList;
import com.viatorfortis.popularmovies.rv.MovieReviewAdapter;
import com.viatorfortis.popularmovies.rv.MovieVideoAdapter;
import com.viatorfortis.popularmovies.utilities.JsonUtils;
import com.viatorfortis.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity
        extends AppCompatActivity
//        implements LoaderManager.LoaderCallbacks
        implements MovieReviewAdapter.ItemClickListener,
        MovieVideoAdapter.ItemClickListener
        {

    private Movie mMovie;
    private boolean mFavouriteMovie;

    private boolean mReviewLoading = true;

    private int REVIEW_LIST_LOADER_ID = 14;

    private LoaderManager.LoaderCallbacks<List<MovieReview>> mReviewLoaderListener;

    private RecyclerView.LayoutManager mReviewLayoutManager;
    private MovieReviewAdapter mReviewAdapter;

    private RecyclerView mReviewRecyclerView;


    private int VIDEO_LIST_LOADER_ID = 15;

    private LoaderManager.LoaderCallbacks<List<MovieVideo>> mVideoLoaderListener;

    private RecyclerView.LayoutManager mVideoLayoutManager;
    private MovieVideoAdapter mVideoAdapter;

    private RecyclerView mVideoRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar appBar = findViewById(R.id.tb_details_appbar);
        setSupportActionBar(appBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.details_activity_caption) );

        Movie movie;

        try {
            movie = getIntent().getParcelableExtra(getString(R.string.movie_parcel_key));
        } catch (NullPointerException e) {
            Toast.makeText(this, "Unable to get movie details", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mMovie = movie;

        //mFavouriteMovie = false;

        populateViews(movie);

        MovieReviewAdapter.resetNextLoadedPageNumber();

        mReviewLoaderListener = new LoaderManager.LoaderCallbacks<List<MovieReview>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public Loader<List<MovieReview>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<List<MovieReview>>(getApplicationContext() ) {
                    @Override
                    public List<MovieReview> loadInBackground() {
                        try {
                            String movieReviewListPageJSON = NetworkUtils.getMovieReviewListPageJSON(getContext(), mMovie.getId(), MovieReviewAdapter.getNextLoadedPageNumber() );

                            if (!movieReviewListPageJSON.isEmpty()) {
                                return JsonUtils.parseMovieReviewListJson(movieReviewListPageJSON);
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
            public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> data) {
                mReviewLoading = false;

                if (data != null
                        && data instanceof ArrayList) {
                    mReviewAdapter.addItems((ArrayList<MovieReview>) data);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.movie_review_list_not_loaded_toast), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLoaderReset(android.support.v4.content.Loader<List<MovieReview>> loader) {

            }
        };

        ArrayList<MovieReview> reviewList;
        LoaderManager loaderManager = getSupportLoaderManager();

        if (savedInstanceState == null
                || !savedInstanceState.containsKey("reviews")) {
            reviewList = new ArrayList<>();
            loaderManager.restartLoader(REVIEW_LIST_LOADER_ID, new Bundle(), mReviewLoaderListener).forceLoad();
        } else {
            reviewList = savedInstanceState.getParcelableArrayList("reviews");
            mReviewLoading = false;
            loaderManager.restartLoader(REVIEW_LIST_LOADER_ID, new Bundle(), mReviewLoaderListener);
        }


        mReviewRecyclerView = findViewById(R.id.rv_movie_reviews);

        mReviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mReviewRecyclerView.setLayoutManager(mReviewLayoutManager);

        mReviewAdapter = new MovieReviewAdapter(reviewList, this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        mReviewRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private final int THRESHOLD_ITEM_COUNT = 3;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = mReviewLayoutManager.getItemCount();
                int lastVisibleItemPosition = ( (LinearLayoutManager) mReviewLayoutManager).findLastVisibleItemPosition();

                if (!mReviewLoading) {
                    if ((lastVisibleItemPosition + THRESHOLD_ITEM_COUNT) >= totalItemCount) {
                        mReviewLoading = true;
                        loadReviewsIntoAdapter();
                    }
                }
            }
        });


        mVideoLoaderListener = new LoaderManager.LoaderCallbacks<List<MovieVideo>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public Loader<List<MovieVideo>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<List<MovieVideo>>(getApplicationContext() ) {
                    @Override
                    public List<MovieVideo> loadInBackground() {
                        try {
                            String movieVideoListJSON = NetworkUtils.getMovieVideoListJSON(getContext(), mMovie.getId() );

                            if (!movieVideoListJSON.isEmpty() ) {
                                return JsonUtils.parseMovieVideoListJson(movieVideoListJSON);
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
            public void onLoadFinished(Loader<List<MovieVideo>> loader, List<MovieVideo> data) {
                if (data != null
                        && data instanceof ArrayList) {
                    mVideoAdapter.addItems((ArrayList<MovieVideo>) data);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.movie_video_list_not_loaded_toast), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<List<MovieVideo>> loader) {

            }
        };

        ArrayList<MovieVideo> videoList;

        if (savedInstanceState == null
                || !savedInstanceState.containsKey("videos")) {
            videoList = new ArrayList<>();
            loaderManager.restartLoader(VIDEO_LIST_LOADER_ID, new Bundle(), mVideoLoaderListener).forceLoad();
        } else {
            videoList = savedInstanceState.getParcelableArrayList("videos");

            // no sense to restart loader since TMDB API doesn't provide video list pagination
            //loaderManager.restartLoader(VIDEO_LIST_LOADER_ID, new Bundle(), mVideoLoaderListener);
        }

        mVideoRecyclerView = findViewById(R.id.rv_movieVideos);

        mVideoLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mVideoRecyclerView.setLayoutManager(mVideoLayoutManager);

        mVideoAdapter = new MovieVideoAdapter(videoList, this, mMovie.getId() );
        mVideoRecyclerView.setAdapter(mVideoAdapter);


    }

    private void populateViews (Movie movie) {

        ( (TextView) findViewById(R.id.tv_details_title) ).setText(movie.getTitle() );

        ImageView PosterImageView = findViewById(R.id.iv_details_poster);
        Picasso.with(PosterImageView.getContext() )
                .load(NetworkUtils.buildMoviePosterURL(getString(R.string.size_w300_end_point), movie.getPosterPath() ) )
                .into(PosterImageView);

        ( (TextView) findViewById(R.id.tv_details_release_date) ).setText(movie.getReleaseDate() );

        ( (RatingBar) findViewById(R.id.rb_details_vote_average) ).setRating(movie.getVoteAverage() );

        String ratingInBrackets = getString(R.string.rating_in_brackets, movie.getVoteAverage() );
        ( (TextView) findViewById(R.id.tv_details_vote_average) ).setText(ratingInBrackets);

        ( (TextView) findViewById(R.id.tv_details_plot_synopsis) ).setText(movie.getPlotSynopsis() );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);

        Uri contentUriForOneFavourite = ContentUris.withAppendedId( MovieContract.FavouriteMoviesEntry.CONTENT_URI, mMovie.getId() );
        Cursor favouriteMovieCursor = getContentResolver().query(contentUriForOneFavourite,
                null,
                null,
                null,
                null);

        MenuItem menuItem = menu.findItem(R.id.mi_favourite);

        if (favouriteMovieCursor != null
                && favouriteMovieCursor.getCount() > 0) {
            mFavouriteMovie = true;
            menuItem.setIcon(R.drawable.ic_star_yellow_36dp);
        } else {
            mFavouriteMovie = false;
            menuItem.setIcon(R.drawable.ic_star_border_black_36dp);
        }

        favouriteMovieCursor.close();

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.mi_favourite:
                mFavouriteMovie = !mFavouriteMovie;
                if (mFavouriteMovie) {
                    //int insertResult =
                    addToFavourite(mMovie);
                    item.setIcon(R.drawable.ic_star_yellow_36dp);
                } else {
                    removeFromFavourite(mMovie.getId() );
                    item.setIcon(R.drawable.ic_star_border_black_36dp);
                }
                break;
            default:
                Toast.makeText(this, getString(R.string.menu_item_undefined_action_toast), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void addToFavourite(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.FavouriteMoviesEntry._ID, movie.getId() );
        contentValues.put(MovieContract.FavouriteMoviesEntry.COLUMN_NAME_TITLE, movie.getTitle() );
        contentValues.put(MovieContract.FavouriteMoviesEntry.COLUMN_NAME_RELEASEDATE, movie.getReleaseDate() );
        contentValues.put(MovieContract.FavouriteMoviesEntry.COLUMN_NAME_POSTERPATH, movie.getPosterPath() );
        contentValues.put(MovieContract.FavouriteMoviesEntry.COLUMN_NAME_VOTEAVERAGE, movie.getVoteAverage() );
        contentValues.put(MovieContract.FavouriteMoviesEntry.COLUMN_NAME_PLOTSYNOPSIS, movie.getPlotSynopsis() );

        Uri uri = getContentResolver().insert(MovieContract.FavouriteMoviesEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(this, getString(R.string.add_to_favourite_toast), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.failed_to_add_to_favourite_toast), Toast.LENGTH_LONG).show();
        }
    }

    private void removeFromFavourite(int movieId) {

        Uri contentUriForOneFavourite = ContentUris.withAppendedId( MovieContract.FavouriteMoviesEntry.CONTENT_URI, mMovie.getId() );
        int deletionResult = getContentResolver().delete(contentUriForOneFavourite, null, null);

        if (deletionResult == 1) {
            Toast.makeText(this, getString(R.string.remove_from_favourite_toast), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.failed_to_remove_to_favourite_toast), Toast.LENGTH_LONG).show();
        }
    }

    private void loadReviewsIntoAdapter() {
        getSupportLoaderManager().getLoader(REVIEW_LIST_LOADER_ID).forceLoad();
    }

    @Override
    public void onItemClick(MovieReview review) {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra(getString(R.string.review_extra_name), review);
        startActivity(intent);
    }

    @Override
    public void onItemClick(MovieVideo video) {
        Uri movieVideoUri = NetworkUtils.buildYoutubeVideoURL(video.getKey() );

        Intent youtubeUrlOpen = new Intent(Intent.ACTION_VIEW, movieVideoUri);
        Intent chooser = Intent.createChooser(youtubeUrlOpen, getString(R.string.implicit_intent_chooser_title) );

        if (youtubeUrlOpen.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }
}
