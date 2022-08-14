package com.example.movies.adapter.videos;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.activity.main.MainActivity;
import com.example.movies.databinding.ItemYoutubeViewBinding;
import com.example.movies.listener.videos.ITrailerItemClickListener;
import com.example.movies.model.movie.MovieObject;
import com.example.movies.model.videos.TrailerObject;

import java.util.ArrayList;
import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    List<TrailerObject.Trailer> trailers;
    ITrailerItemClickListener iTrailerItemClickListener;
    MovieObject.Movie movie;

    public VideosAdapter(ITrailerItemClickListener iTrailerItemClickListener, MovieObject.Movie a) {
        trailers = new ArrayList<>();
        this.iTrailerItemClickListener = iTrailerItemClickListener;
        this.movie = a;
    }

    public void setTrailers(List<TrailerObject.Trailer> trailers) {
        this.trailers = trailers;
    }

    public void setITrailerItemClickListener(ITrailerItemClickListener iTrailerItemClickListener) {
        this.iTrailerItemClickListener = iTrailerItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemYoutubeViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_youtube_view, parent, false);
        return new ViewHolder(binding, iTrailerItemClickListener,movie);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrailerObject.Trailer trailer = trailers.get(position);
        holder.binding.setItem(trailer);
        holder.setTrailer(trailer);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
            holder.binding.videoMainLayout.setBackground(ContextCompat.getDrawable(holder.binding.getRoot().getContext(),R.color.white));
        }
        else if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            holder.binding.videoMainLayout.setBackground(ContextCompat.getDrawable(holder.binding.getRoot().getContext(),R.color.black));
        }

    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemYoutubeViewBinding binding;
        TrailerObject.Trailer trailer;
        ITrailerItemClickListener iTrailerItemClickListener;
        MovieObject.Movie movie;

        public ViewHolder(@NonNull ItemYoutubeViewBinding binding, ITrailerItemClickListener iTrailerItemClickListener, MovieObject.Movie a) {
            super(binding.getRoot());
            this.binding = binding;
            this.iTrailerItemClickListener = iTrailerItemClickListener;
            this.movie = a;
            binding.getRoot().setOnClickListener(this);
        }

        public void setTrailer(TrailerObject.Trailer trailer) {
            this.trailer = trailer;
        }

        @Override
        public void onClick(View view) {
            MainActivity.addMovieToWatchRecently(movie);
            iTrailerItemClickListener.onTrailerItemClick(trailer);
        }
    }
}
