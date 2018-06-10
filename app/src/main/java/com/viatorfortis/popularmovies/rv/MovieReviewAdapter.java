package com.viatorfortis.popularmovies.rv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.viatorfortis.popularmovies.R;
import com.viatorfortis.popularmovies.models.MovieReview;
import java.util.ArrayList;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {
    private final ArrayList<MovieReview> mMovieReviewList;
    private static int mNextPageNumber = 1;

    public static int getNextLoadedPageNumber() {
        return mNextPageNumber;
    }

    public static void resetNextLoadedPageNumber() {
        mNextPageNumber = 1;
    }

    public interface ItemClickListener {
        void onItemClick(MovieReview review);
    }

    private final ItemClickListener mItemClickListener;

    public MovieReviewAdapter(ArrayList <MovieReview> movieReviewList, ItemClickListener itemClickListener) {
        mMovieReviewList = movieReviewList;
        mItemClickListener = itemClickListener;
    }


    public class MovieReviewViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView mAuthor;
        private final TextView mContent;

        private MovieReviewViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mAuthor = itemView.findViewById(R.id.tv_author);
            mContent = itemView.findViewById(R.id.tv_content);
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(, String.valueOf("asasasasasas"), Toast.LENGTH_LONG).show();
            mItemClickListener.onItemClick(mMovieReviewList.get(getAdapterPosition() ) );
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

    public void addItems(ArrayList<MovieReview> movieReviewList) {
        mNextPageNumber++;

        mMovieReviewList.addAll(movieReviewList);
        notifyDataSetChanged();
    }
}
