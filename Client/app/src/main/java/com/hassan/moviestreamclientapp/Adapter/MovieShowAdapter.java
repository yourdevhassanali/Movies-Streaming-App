package com.hassan.moviestreamclientapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hassan.moviestreamclientapp.Model.GetVideoDetails;
import com.hassan.moviestreamclientapp.Model.MovieItemClickListenerNew;
import com.hassan.moviestreamclientapp.R;

import java.util.List;

public class MovieShowAdapter extends RecyclerView.Adapter<MovieShowAdapter.MyViewHolder> {
    Context mContext ;
    List<GetVideoDetails> uploads ;
    MovieItemClickListenerNew movieItemClickListenerNew;

    public MovieShowAdapter(Context mContext, List<GetVideoDetails> uploads, MovieItemClickListenerNew listener) {
        this.mContext = mContext;
        this.uploads = uploads;
        movieItemClickListenerNew = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item_new,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        GetVideoDetails getVideoDetails = uploads.get(position);

//        holder.TvTitle.setText(getVideoDetails.getVideo_name());
        Glide.with(mContext).load(getVideoDetails.getVideo_thumb()).into(holder.ImgMovie);
        //holder.container.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_transition_animation));
    }
    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

         TextView TvTitle;
      ImageView ImgMovie;
        ConstraintLayout container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //TvTitle = itemView.findViewById(R.id.item_movie_title);
            ImgMovie = itemView.findViewById(R.id.item_movie_img);
            container = itemView.findViewById(R.id.container);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    movieItemClickListenerNew.onMovieClick(uploads.get(getAdapterPosition()),ImgMovie);
                }
            });
        }
    }
}

