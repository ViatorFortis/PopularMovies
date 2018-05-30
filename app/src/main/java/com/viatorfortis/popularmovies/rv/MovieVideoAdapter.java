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
import com.viatorfortis.popularmovies.models.MovieVideo;
import com.viatorfortis.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;

public class MovieVideoAdapter
        extends RecyclerView.Adapter<MovieVideoAdapter.MovieVideoViewHolder> {

    private final ArrayList<MovieVideo> mMovieVideoList;
    private final int mMovieId;

    public interface ItemClickListener {
        void onItemClick(MovieVideo video);
    }

    private final ItemClickListener mItemClickListener;

    public MovieVideoAdapter(ArrayList<MovieVideo> movieVideoList, ItemClickListener itemClickListener, int movieId) {
        mMovieVideoList = movieVideoList;
        mItemClickListener = itemClickListener;
        mMovieId = movieId;
    }

    public class MovieVideoViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView mVideoName;
        private final ImageView mVideoThumbnail;

        private MovieVideoViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mVideoName = itemView.findViewById(R.id.tv_video_name);
            mVideoThumbnail = itemView.findViewById(R.id.iv_video_thumbnail);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(mMovieVideoList.get(getAdapterPosition() ) );
        }
    }

    @Override
    public MovieVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext() );
        View view = inflater.inflate(R.layout.recyclerview_videoitemview, parent, false);

        view.setTag(mMovieVideoList);
        return new MovieVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieVideoViewHolder holder, int position) {
        Context thumbnailContext = holder.mVideoThumbnail.getContext();

        Picasso.with(thumbnailContext)
                .load(NetworkUtils.buildYoutubeVideoThumbnailURL(mMovieVideoList.get(position).getKey() ) )
                .into(holder.mVideoThumbnail);

        holder.mVideoName.setText(mMovieVideoList.get(position).getName() );
    }

    @Override
    public int getItemCount() { return mMovieVideoList.size(); }

    public void addItems(ArrayList<MovieVideo> movieVideoList) {
        mMovieVideoList.addAll(movieVideoList);
        notifyDataSetChanged();
    }
}
