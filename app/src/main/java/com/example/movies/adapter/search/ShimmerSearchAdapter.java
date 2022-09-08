package com.example.movies.adapter.search;

import static com.example.movies.resources.MovieResources.mapGenresIDMovie;
import static com.example.movies.resources.MovieResources.mapGenresIDTVShow;
import static com.example.movies.ui.activity.details.DetailsMovieActivity.chipTextClicked;
import static com.example.movies.ui.activity.main.MainActivity.positionTab;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.data.api.APIGetData;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.databinding.LayoutItemFilmBinding;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.ui.activity.details.DetailsMovieActivity;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShimmerSearchAdapter extends RecyclerView.Adapter<ShimmerSearchAdapter.ViewHolder> {


    private List<MovieObject.Movie> movieListFilter;

    public ShimmerSearchAdapter(List<MovieObject.Movie> list) {
        this.movieListFilter = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemFilmBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_film, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieObject.Movie item = movieListFilter.get(position);
        holder.bindData(item);

    }

    @Override
    public int getItemCount() {
        return movieListFilter.size();
    }



    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutItemFilmBinding binding;
        IMovieItemClickListener itemClicked;
        MovieObject.Movie movie;

        public ViewHolder(@NonNull LayoutItemFilmBinding bindingTemp) {
            super(bindingTemp.getRoot());
            binding = bindingTemp;
        }

        public void bindData(MovieObject.Movie a) {
            this.movie = a;
            binding.setItemFilm(a);

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.shimmerLayoutMovie.post(() -> binding.shimmerLayoutMovie.setVisibility(View.VISIBLE));
                    binding.layoutMovieNotShimmer.post(() -> binding.layoutMovieNotShimmer.setVisibility(View.GONE));
                }
            }, 500);

        }

        @Override
        public void onClick(View view) {
            itemClicked.itemClicked(movie);
        }
    }
}
