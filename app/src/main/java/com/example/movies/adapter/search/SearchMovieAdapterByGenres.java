package com.example.movies.adapter.search;

import static com.example.movies.activity.details.DetailsMovieActivity.chipTextClicked;
import static com.example.movies.activity.details.DetailsMovieActivity.typeMovieOrTVShowObservableField;
import static com.example.movies.activity.main.MainActivity.positionTab;
import static com.example.movies.resources.MovieResources.mapGenresIDMovie;
import static com.example.movies.resources.MovieResources.mapGenresIDTVShow;

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
import com.example.movies.activity.details.DetailsMovieActivity;
import com.example.movies.api.APIGetData;
import com.example.movies.databinding.LayoutItemFilmBinding;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.model.movie.MovieObject;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMovieAdapterByGenres extends RecyclerView.Adapter<SearchMovieAdapterByGenres.ViewHolder> {

    String keySearch = "";
    int page = 1;
    public static int pageTemp = 1;
    private final List<MovieObject.Movie> movieListFilter;
    private final List<MovieObject.Movie> oldMovieList;
    private final IMovieItemClickListener itemClicked;

    public SearchMovieAdapterByGenres(List<MovieObject.Movie> list, IMovieItemClickListener itemClickedTemp) {
        this.oldMovieList = new ArrayList<>(list);
        this.movieListFilter = new ArrayList<>(list);
        this.itemClicked = itemClickedTemp;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemFilmBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_film, parent, false);
        return new ViewHolder(binding, itemClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieObject.Movie item = movieListFilter.get(position);
        holder.binding.setItemFilm(item);
        holder.setMovie(item);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.binding.shimmerLayoutMovie.post(() -> holder.binding.shimmerLayoutMovie.setVisibility(View.GONE));
                holder.binding.layoutMovieNotShimmer.post(() -> holder.binding.layoutMovieNotShimmer.setVisibility(View.VISIBLE));
            }
        }, 500);

        if (position >= movieListFilter.size() - 4) {
            //LOAD NEW MOVIE FROM API
            ++page;
            getMoviesFromAPI(page, keySearch);
        }
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return movieListFilter.size();
    }


    public void setKeySearch(String key) {
        this.keySearch = key;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearData() {
        page = 1;
        movieListFilter.clear();
        oldMovieList.clear();
        notifyDataSetChanged();
    }

    //GET MOVIES FROM API
    public void getMoviesFromAPI(int page, String key) {
        this.keySearch = key;
        //String genres = DetailsMovieActivity.getCurrentChipText();
        String idGenres;
        if (positionTab == 0) {
            idGenres = mapGenresIDMovie.get(chipTextClicked.get());
        } else {
            idGenres = mapGenresIDTVShow.get(chipTextClicked.get());
        }
        String typeMovieOrTVShow = positionTab == 0 ? Utils.TYPE_MOVIE : Utils.TYPE_TV_SHOW;
        if (keySearch.isEmpty()) {
            APIGetData.apiGetData.getMoviesByGenreID(typeMovieOrTVShow, Utils.API_MOVIE_KEY, idGenres, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<MovieObject> call, Response<MovieObject> response) {
                    try {
                        assert response.body() != null;
                        int size = movieListFilter.size();
                        movieListFilter.addAll(response.body().getMoviesList());
                        notifyItemRangeInserted(size, movieListFilter.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<MovieObject> call, Throwable t) {

                }
            });
        } else {
            APIGetData.apiGetData.getMovieByKeyword(typeMovieOrTVShow, key, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<MovieObject> call, Response<MovieObject> response) {
                    try {
                        assert response.body() != null;
                        List<MovieObject.Movie> filter = filterMoviesByKeyword(response.body().getMoviesList(), idGenres);
                        if (filter.size() > 0) {
                            int size = movieListFilter.size();
                            movieListFilter.addAll(filter);
                            notifyItemRangeInserted(size, movieListFilter.size());
                        } else {
                            pageTemp = page;
                            ++pageTemp;
                            getMoviesFromAPI(pageTemp, key);
                            setPage(pageTemp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<MovieObject> call, Throwable t) {

                }
            });

        }
    }

    //FILTER MOVIE BY KEY SEARCH
    @SuppressLint("NotifyDataSetChanged")
    public synchronized List<MovieObject.Movie> filterMoviesByKeyword(List<MovieObject.Movie> list, String idGenre) {
        List<MovieObject.Movie> moviesFilter;
        Map<String, MovieObject.Movie> moviesMap = new HashMap<>();
        //REPLACE ALL SPECIFIC CHARACTERS WITH SPACE, THEN REPLACE TWO OR MORE SPACE WITH ONE SPACE
        keySearch = keySearch.replaceAll(Utils.SPECIFIC_CHARACTERS, " ").replaceAll(Utils.REGEX_DOUBLE_SPACE, " ");
        String[] str = keySearch.split(" ");
        for (MovieObject.Movie a : list) {
            if (a.getGenre_ids().contains(idGenre)) {
                for (String item : str) {
                    if (positionTab == 0) {
                        if (a.getTitle().toLowerCase().trim().contains(item.toLowerCase().trim()) || a.getOriginal_title().toLowerCase().trim().contains(item.toLowerCase().trim()) || a.getTitle().toLowerCase().trim().equals(item.toLowerCase().trim()) || a.getOriginal_title().toLowerCase().trim().equals(item.toLowerCase().trim())) {
                            moviesMap.put(a.getId(), a);
                        }
                    } else if (positionTab == 1) {
                        if (a.getName().toLowerCase().trim().contains(item.toLowerCase().trim()) || a.getOriginal_name().toLowerCase().trim().contains(item.toLowerCase().trim()) || a.getName().toLowerCase().trim().equals(item.toLowerCase().trim()) || a.getOriginal_name().toLowerCase().trim().equals(item.toLowerCase().trim())) {
                            moviesMap.put(a.getId(), a);
                        }
                    }
                }
            }
        }
        moviesFilter = new ArrayList<>(moviesMap.values());
        return moviesFilter;
    }

    public void setPage(int pageA) {
        this.page = pageA;
    }

    //FILTER MOVIES BY GENRES ID
    public synchronized List<MovieObject.Movie> filterMoviesByGenresID(List<MovieObject.Movie> list, String idGenre) {
        List<MovieObject.Movie> moviesFilter;
        Map<String, MovieObject.Movie> moviesMap = new HashMap<>();
        String genres = DetailsMovieActivity.getCurrentChipText();
        for (MovieObject.Movie item : list) {
            if (item.getGenre_ids().contains(idGenre)) {
                moviesMap.put(item.getId(), item);
            }
        }
        moviesFilter = new ArrayList<>(moviesMap.values());
        return moviesFilter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutItemFilmBinding binding;
        IMovieItemClickListener itemClicked;
        MovieObject.Movie movie;

        public ViewHolder(@NonNull LayoutItemFilmBinding bindingTemp, IMovieItemClickListener itemClickedTemp) {
            super(bindingTemp.getRoot());
            binding = bindingTemp;
            this.itemClicked = itemClickedTemp;
            binding.getRoot().setOnClickListener(this);
        }

        public void setMovie(MovieObject.Movie a) {
            this.movie = a;
        }

        @Override
        public void onClick(View view) {
            itemClicked.itemClicked(movie);
        }
    }
}
