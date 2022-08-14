package com.example.movies.adapter.movie;

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
import com.example.movies.api.APIGetData;
import com.example.movies.databinding.LayoutItemFilmBinding;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.model.anothers.IDMovieObject;
import com.example.movies.model.movie.MovieObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.ViewHolder> {

    private final List<MovieObject.Movie> favoriteMovies;
    private final Map<String,IDMovieObject> mapFavoriteMovies;
    private final IMovieItemClickListener itemClickListener;

    public FavoriteMoviesAdapter(List<MovieObject.Movie> list,Map<String,IDMovieObject> mapFavoriteMoviesTemp, IMovieItemClickListener itemClickListenerTemp){
        this.favoriteMovies = list;
        mapFavoriteMovies = mapFavoriteMoviesTemp;
        itemClickListener = itemClickListenerTemp;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemFilmBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_film,parent,false);
        return new ViewHolder(binding,itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            MovieObject.Movie movie = favoriteMovies.get(position);
            holder.binding.setItemFilm(movie);
            holder.setMovie(movie);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.binding.shimmerLayoutMovie.post(()->holder.binding.shimmerLayoutMovie.setVisibility(View.GONE));
                holder.binding.layoutMovieNotShimmer.post(()->holder.binding.layoutMovieNotShimmer.setVisibility(View.VISIBLE));
            }
        },500);
    }

    @Override
    public int getItemCount() {
        return favoriteMovies.size();
    }

    public void addMovieFavoriteList(MovieObject.Movie movie, String type){
        int size = this.favoriteMovies.size();
        this.favoriteMovies.add(size,movie);
        this.mapFavoriteMovies.put(movie.getId(),new IDMovieObject(movie.getId(),type));
        notifyItemInserted(size);
    }

    public void removeOutOfFavoriteList(MovieObject.Movie movie){
        int positionItemRemove = getPositionMovie(movie);
        this.favoriteMovies.remove(positionItemRemove);
        this.mapFavoriteMovies.remove(movie.getId());
        this.notifyItemRemoved(positionItemRemove);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void addMovieListFavorites(List<MovieObject.Movie> list){
        favoriteMovies.clear();
        favoriteMovies.addAll(list);
        notifyDataSetChanged();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void addMapIDMovie(Map<String,IDMovieObject> map){
        mapFavoriteMovies.clear();
        mapFavoriteMovies.putAll(map);
    }

    private int getPositionMovie(MovieObject.Movie movie){
        for (int i = 0; i < favoriteMovies.size(); i++) {
            if(favoriteMovies.get(i).getId().equals(movie.getId())){
                return i;
            }
        }
        return -1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getMovieFavoritesFromAPI(){
        favoriteMovies.clear();
        notifyDataSetChanged();
        for (String key : mapFavoriteMovies.keySet()){
            IDMovieObject idMovieObject = mapFavoriteMovies.get(key);
            APIGetData.apiGetData.getDetailsMovieInformation(Objects.requireNonNull(idMovieObject).getType(),idMovieObject.getId()).enqueue(new Callback<MovieObject.Movie>() {
                @Override
                public void onResponse(@NonNull Call<MovieObject.Movie> call, @NonNull Response<MovieObject.Movie> response) {
                    int size = getItemCount();
                    favoriteMovies.add(size,response.body());
                    notifyItemInserted(size);
                }

                @Override
                public void onFailure(@NonNull Call<MovieObject.Movie> call, @NonNull Throwable t) {

                }
            });

        }
    }

    public boolean containsMovie(MovieObject.Movie movie){
        return mapFavoriteMovies.containsKey(movie.getId());
    }

    public List<MovieObject.Movie> getFavoriteMovies(){return this.favoriteMovies;}


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final LayoutItemFilmBinding binding;
        private final IMovieItemClickListener itemClickListener;
        private MovieObject.Movie movie;

        public ViewHolder(@NonNull LayoutItemFilmBinding bindingTemp, IMovieItemClickListener itemClickListenerTemp) {
            super(bindingTemp.getRoot());
            this.binding = bindingTemp;
            this.itemClickListener = itemClickListenerTemp;
            this.binding.getRoot().setOnClickListener(this);
        }

        public void setMovie(MovieObject.Movie a){
            this.movie = a;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.itemClicked(movie);
        }
    }

}
