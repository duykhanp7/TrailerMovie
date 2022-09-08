package com.example.movies.adapter.search;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.data.api.APIGetData;
import com.example.movies.databinding.LayoutItemFilmBinding;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Adapter tìm kiếm phim
 */
public class SearchMovieAdapter extends RecyclerView.Adapter<SearchMovieAdapter.ViewHolder> {

    public List<MovieObject.Movie> moviesSearch;
    public int page = 1;
    public String keyword;
    IMovieItemClickListener iMovieItemClickListener;
    String typeMovieOrTVShow = Utils.TYPE_MOVIE;

    public SearchMovieAdapter(IMovieItemClickListener iMovieItemClickListener) {
        this.moviesSearch = new ArrayList<>();
        this.iMovieItemClickListener = iMovieItemClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMoviesSearch(List<MovieObject.Movie> movies) {
        this.moviesSearch = movies;
        notifyDataSetChanged();
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTypeMovieOrTVShow() {
        return typeMovieOrTVShow;
    }

    public void setTypeMovieOrTVShow(String typeMovieOrTVShow) {
        this.typeMovieOrTVShow = typeMovieOrTVShow;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemFilmBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_film, parent, false);
        return new ViewHolder(binding, iMovieItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieObject.Movie movie = moviesSearch.get(position);
        holder.bindData(movie);
        if (position == moviesSearch.size() - 3) {
            APIGetData.apiGetData.getMovieByKeyword(typeMovieOrTVShow, keyword, String.valueOf(++page)).enqueue(new Callback<MovieObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<MovieObject> call, @NonNull Response<MovieObject> response) {
                    try {
                        int size = moviesSearch.size();
                        moviesSearch.addAll(Objects.requireNonNull(response.body()).getMoviesList());
                        notifyItemRangeInserted(size, moviesSearch.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieObject> call, @NonNull Throwable t) {

                }
            });
        }
        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return moviesSearch.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutItemFilmBinding binding;
        MovieObject.Movie movie;
        IMovieItemClickListener iMovieItemClickListener;

        public ViewHolder(@NonNull LayoutItemFilmBinding itemView, IMovieItemClickListener iMovieItemClickListener) {
            super(itemView.getRoot());
            this.binding = itemView;
            this.iMovieItemClickListener = iMovieItemClickListener;
            itemView.getRoot().setOnClickListener(this);
        }

        public void bindData(MovieObject.Movie movie) {
            this.movie = movie;
            binding.setItemFilm(movie);

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.shimmerLayoutMovie.post(() -> binding.shimmerLayoutMovie.setVisibility(View.GONE));
                    binding.layoutMovieNotShimmer.post(() -> binding.layoutMovieNotShimmer.setVisibility(View.VISIBLE));
                }
            }, 500);
        }

        @Override
        public void onClick(View view) {
            iMovieItemClickListener.itemClicked(movie);
        }
    }
}
