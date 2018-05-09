package com.viatorfortis.popularmovies.rv;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viatorfortis.popularmovies.R;
import com.viatorfortis.popularmovies.models.Movie;
import com.viatorfortis.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {



    private final ArrayList<Movie> mMovieList;
    private static int mNextPageNumber = 1;
    private boolean mClearListBeforeAddition = false;

    public Movie getMovie(int adapterPosition) {
        return mMovieList.get(adapterPosition);
    }

    public ArrayList<Movie> getMovieList() {
        return mMovieList;
    }

    private final GridItemClickListener mGridItemClickListener;

    public interface GridItemClickListener {
        void onGridItemClick(int adapterPosition);
    }

    public MovieAdapter(ArrayList <Movie> moviesList, GridItemClickListener gridItemClickListener) {
        mMovieList = moviesList;
        mGridItemClickListener = gridItemClickListener;
    }

    public static int getNextLoadedPageNumber() {
        return mNextPageNumber;
    }


    public void resetMoviesList() {
        mClearListBeforeAddition = true;
        mNextPageNumber = 1;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        private final TextView mTextViewName;
        private final ImageView mImageViewThumbnail;

        private MovieViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTextViewName = itemView.findViewById(R.id.tv_name);
            mImageViewThumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }

        @Override
        public void onClick(View v) {
            mGridItemClickListener.onGridItemClick(getAdapterPosition() );
        }
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext() );
        View view = inflater.inflate(R.layout.recyclerview_itemview, parent, false);

        view.setTag(mMovieList);

        return new MovieViewHolder(view);
    }

//    @Override
//    public void onViewAttachedToWindow(MovieViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        Context thumbnailContext = holder.mImageViewThumbnail.getContext();

        holder.mTextViewName.setText(mMovieList.get(position).getTitle() );
        Picasso.with(thumbnailContext)
                .load(NetworkUtils.buildMoviePosterURL(thumbnailContext.getString(R.string.size_w200_end_point), mMovieList.get(position).getPosterPath() ) )
                .into(holder.mImageViewThumbnail);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public void addMoviesList(ArrayList<Movie> moviesList) {
        if (mClearListBeforeAddition) {
            mMovieList.clear();
            mClearListBeforeAddition = false;
        }

        mNextPageNumber++;

        mMovieList.addAll(moviesList);
        notifyDataSetChanged();
    }
}
