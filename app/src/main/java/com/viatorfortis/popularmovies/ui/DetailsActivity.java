package com.viatorfortis.popularmovies.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import com.viatorfortis.popularmovies.models.Movie;
import com.viatorfortis.popularmovies.models.MovieReview;
import com.viatorfortis.popularmovies.rv.MovieReviewAdapter;
import com.viatorfortis.popularmovies.utilities.JsonUtils;
import com.viatorfortis.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity
        extends AppCompatActivity
//        implements LoaderManager.LoaderCallbacks
        implements MovieReviewAdapter.ItemClickListener
        {

    private Movie mMovie;
    private boolean mFavouriteMovie;

    private boolean mReviewLoading = true;

    private int REVIEW_LIST_LOADER_ID = 14;

    private LoaderManager.LoaderCallbacks<List<MovieReview>> mReviewLoaderListener;

    private RecyclerView.LayoutManager mReviewLayoutManager;
    private MovieReviewAdapter mReviewAdapter;

    private RecyclerView mReviewRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar appBar = findViewById(R.id.tb_details_appbar);
        setSupportActionBar(appBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.details_activity_caption) );

        mFavouriteMovie = false;
        Movie movie;

        try {
            movie = getIntent().getParcelableExtra(getString(R.string.movie_parcel_key));
        } catch (NullPointerException e) {
            Toast.makeText(this, "Unable to get movie details", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mMovie = movie;
        populateViews(movie);

        mReviewAdapter.resetNextLoadedPageNumber();

        mReviewLoaderListener = new LoaderManager.LoaderCallbacks<List<MovieReview>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public Loader<List<MovieReview>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<List<MovieReview>>(getApplicationContext() ) {
                    @Override
                    public List<MovieReview> loadInBackground() {
                        try {
                            String movieReviewListPageJSON = NetworkUtils.getMovieReviewListPageJSON(getContext(), mMovie.getId(), mReviewAdapter.getNextLoadedPageNumber() );

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
                    item.setIcon(R.drawable.ic_star_yellow_36dp);
                } else {
                    item.setIcon(R.drawable.ic_star_border_black_36dp);
                }
                break;
            default:
                Toast.makeText(this, getString(R.string.menu_item_undefined_action_toast), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void loadReviewsIntoAdapter() {
        getSupportLoaderManager().getLoader(REVIEW_LIST_LOADER_ID).forceLoad();
    }

    @Override
    public void onItemClick(MovieReview review) {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("ReviewParcel", review);
        startActivity(intent);

        /*
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View reviewLayout = inflater.inflate(R.layout.review,
                null);

        //reviewLayout.setLayoutParams();

        final PopupWindow popupWindow = new PopupWindow(reviewLayout,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
                );

        TextView authorTextView = reviewLayout.findViewById(R.id.review_tv_author);
        authorTextView.setText(review.getAuthor() );

        TextView contentTextView = reviewLayout.findViewById(R.id.review_tv_content);
        contentTextView.setText(review.getContent() );

        Button closeButton = reviewLayout.findViewById(R.id.review_btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
        }
        });

        //popupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.black));
        popupWindow.showAtLocation(mReviewRecyclerView, Gravity.CENTER, 0, 0);
        */
    }

}
