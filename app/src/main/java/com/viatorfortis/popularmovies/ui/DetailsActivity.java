package com.viatorfortis.popularmovies.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.viatorfortis.popularmovies.R;
import com.viatorfortis.popularmovies.models.Movie;
import com.viatorfortis.popularmovies.utilities.NetworkUtils;

public class DetailsActivity
        extends AppCompatActivity {

    private boolean mFavouriteMovie;

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

        populateViews(movie);
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
}
