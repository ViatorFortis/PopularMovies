package com.viatorfortis.popularmovies.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.viatorfortis.popularmovies.R;
import com.viatorfortis.popularmovies.models.MovieReview;

public class ReviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);

        MovieReview review;
        try {
            review = getIntent().getParcelableExtra(getString(R.string.review_extra_name) );
        } catch (NullPointerException e) {
            Toast.makeText(this, "Unable to get review", Toast.LENGTH_LONG).show();
            Log.d(e.getClass().getName(), e.getMessage());
            finish();
            return;
        }

        populateViews(review);

    }

    private void populateViews(MovieReview review) {
        ( (TextView) findViewById(R.id.review_tv_author) ).setText(review.getAuthor() );
        ( (TextView) findViewById(R.id.review_tv_content) ).setText(review.getContent() );
    }
}
