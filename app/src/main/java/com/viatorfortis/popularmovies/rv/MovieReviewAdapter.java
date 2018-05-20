package com.viatorfortis.popularmovies.rv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viatorfortis.popularmovies.R;
import com.viatorfortis.popularmovies.models.MovieReview;

import java.util.ArrayList;
import java.util.List;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {
    private final ArrayList<MovieReview> mMovieReviewList;
    public static int mNextPageNumber = 1;

    public MovieReviewAdapter(ArrayList <MovieReview> movieReviewList) {
        mMovieReviewList = movieReviewList;
    }


    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView mAuthor;
        private final TextView mContent;

        private MovieReviewViewHolder(View itemView) {
            super(itemView);

            mAuthor = itemView.findViewById(R.id.tv_author);
            mContent = itemView.findViewById(R.id.tv_content);
        }
    }

    @Override
    public MovieReviewAdapter.MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_reviewitemview, parent, false);

        view.setTag(mMovieReviewList);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, final int position) {
        holder.mAuthor.setText(mMovieReviewList.get(position).getAuthor() );
        holder.mContent.setText(mMovieReviewList.get(position).getContent() );
    }

    @Override
    public int getItemCount() {
        return mMovieReviewList.size();
    }

    public void AddItems(ArrayList<MovieReview> movieReviewList) {
        mNextPageNumber++;

        mMovieReviewList.addAll(movieReviewList);
        notifyDataSetChanged();
    }
}
