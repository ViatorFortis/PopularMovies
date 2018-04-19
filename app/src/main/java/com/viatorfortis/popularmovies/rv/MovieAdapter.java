package com.viatorfortis.popularmovies.rv;
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



    private List<Movie> mMovieList;
    private static int mNextPageNumber = 1;
    private boolean mClearListBeforeAddition = false;

    public Movie getMovie(int adapterPosition) {
        return mMovieList.get(adapterPosition);
    }

    private final GridItemClickListener mGridItemClickListener;

    public interface GridItemClickListener {
        void onGridItemClick(int adapterPosition);
    }

    public MovieAdapter(List <Movie> moviesList, GridItemClickListener gridItemClickListener) {
        mMovieList = moviesList;
        mGridItemClickListener = gridItemClickListener;
    }

    public void incrementNextLoadedPageNumber() {
        mNextPageNumber++;
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
        private TextView mTextViewName;
        private ImageView mImageViewThumbnail;

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

        MovieViewHolder movieViewHolder = new MovieViewHolder(view);
        return movieViewHolder;
    }

//    @Override
//    public void onViewAttachedToWindow(MovieViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        holder.mTextViewName.setText(mMovieList.get(position).getTitle() );
        Picasso.with(holder.mImageViewThumbnail.getContext() )
                .load(NetworkUtils.buildMoviePosterURL("w200", mMovieList.get(position).getPosterPath() ) )
                .into(holder.mImageViewThumbnail);

        /*final String name = values.get(position);
        holder.mTextViewName.setText(name);
        holder.mTextViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(position);
            }
        });*/
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
