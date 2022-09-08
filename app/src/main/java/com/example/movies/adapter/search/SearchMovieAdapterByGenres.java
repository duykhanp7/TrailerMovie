package com.example.movies.adapter.search;

import static com.example.movies.ui.activity.details.DetailsMovieActivity.chipTextClicked;
import static com.example.movies.ui.activity.main.MainActivity.chipTextFromKeyword;
import static com.example.movies.ui.activity.main.MainActivity.positionTab;
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
import com.example.movies.ui.activity.details.DetailsMovieActivity;
import com.example.movies.data.api.APIGetData;
import com.example.movies.databinding.LayoutItemFilmBinding;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.ui.activity.main.MainActivity;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMovieAdapterByGenres extends RecyclerView.Adapter<SearchMovieAdapterByGenres.ViewHolder> {

    public String keySearch = "";
    int page = 1;
    public static int pageTemp = 1;
    private List<MovieObject.Movie> movieListFilter;
    private final List<MovieObject.Movie> oldMovieList;
    private final IMovieItemClickListener itemClicked;
    private int maxPage = 10;

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
        holder.bindData(item);

        if (position >= movieListFilter.size() - 4) {
            //LOAD NEW MOVIE FROM API
            ++page;
            getMoviesFromAPI(page, keySearch);
        }
        //holder.setIsRecyclable(false);
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

        String typeMovieOrTVShow = MainActivity.getTypeByPositionTab();

        if (positionTab != 0 && positionTab != 1) {
            if (positionTab == 2) {
                typeMovieOrTVShow = Utils.TYPE_TV_SHOW;
            } else {
                typeMovieOrTVShow = chipTextFromKeyword.get();
            }
        }

        Log.i("AAA", "KEY WORD SEARCH : " + keySearch + " -- " + chipTextFromKeyword.get() + " -- " + typeMovieOrTVShow);

        if (keySearch.isEmpty()) {
            String finalTypeMovieOrTVShow = typeMovieOrTVShow;
            APIGetData.apiGetData.getMoviesByGenreID(typeMovieOrTVShow, Utils.API_MOVIE_KEY, idGenres, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<MovieObject> call, Response<MovieObject> response) {
                    try {
                        maxPage = Integer.parseInt(Objects.requireNonNull(response.body()).getTotal_pages());
                        assert response.body() != null;
                        int size = movieListFilter.size();
                        movieListFilter.addAll(response.body().getMoviesList());
                        movieListFilter.forEach((item -> {
                            item.setTypeMovieOrTVShow(finalTypeMovieOrTVShow);
                        }));
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
            String finalTypeMovieOrTVShow1 = typeMovieOrTVShow;
            APIGetData.apiGetData.getMovieByKeyword(typeMovieOrTVShow, key, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<MovieObject> call, Response<MovieObject> response) {
                    try {
                        maxPage = Integer.parseInt(Objects.requireNonNull(response.body()).getTotal_pages());
                        assert response.body() != null;
                        List<MovieObject.Movie> filter = filterMoviesByKeyword(response.body().getMoviesList(), idGenres, finalTypeMovieOrTVShow1);
                        filter.forEach(item -> {
                            item.setTypeMovieOrTVShow(finalTypeMovieOrTVShow1);
                        });
                        if (filter.size() > 0) {
                            int size = movieListFilter.size();
                            movieListFilter.addAll(filter);
                            notifyItemRangeInserted(size, movieListFilter.size());
                        } else {
                            pageTemp = page;
                            ++pageTemp;
                            if (pageTemp <= maxPage) {
                                getMoviesFromAPI(pageTemp, key);
                                setPage(pageTemp);
                            }
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


    //GET MOVIES FROM API
    public void refreshData(int page, String key) {
        this.keySearch = key;
        //String genres = DetailsMovieActivity.getCurrentChipText();
        String idGenres;
        if (positionTab == 0) {
            idGenres = mapGenresIDMovie.get(chipTextClicked.get());
        } else {
            idGenres = mapGenresIDTVShow.get(chipTextClicked.get());
        }
        String typeMovieOrTVShow = MainActivity.getTypeByPositionTab();
        if (keySearch.isEmpty()) {
            APIGetData.apiGetData.getMoviesByGenreID(typeMovieOrTVShow, Utils.API_MOVIE_KEY, idGenres, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<MovieObject> call, @NonNull Response<MovieObject> response) {
                    try {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                assert response.body() != null;
                                movieListFilter = new ArrayList<>();
                                movieListFilter.addAll(response.body().getMoviesList());
                                notifyDataSetChanged();
                                DetailsMovieActivity.isReloadFinish.postValue(true);
                            }
                        }, 2000);
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
                        List<MovieObject.Movie> filter = filterMoviesByKeyword(response.body().getMoviesList(), idGenres, typeMovieOrTVShow);
                        if (filter.size() > 0) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    movieListFilter = new ArrayList<>();
                                    movieListFilter.addAll(filter);
                                    notifyDataSetChanged();
                                    DetailsMovieActivity.isReloadFinish.postValue(true);
                                }
                            }, 2000);

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
    public synchronized List<MovieObject.Movie> filterMoviesByKeyword(List<MovieObject.Movie> list, String idGenre, String typeMovieOrTV) {
        List<MovieObject.Movie> moviesFilter;
        Map<String, MovieObject.Movie> moviesMap = new HashMap<>();
        //REPLACE ALL SPECIFIC CHARACTERS WITH SPACE, THEN REPLACE TWO OR MORE SPACE WITH ONE SPACE
        keySearch = keySearch.replaceAll(Utils.SPECIFIC_CHARACTERS, " ").replaceAll(Utils.REGEX_DOUBLE_SPACE, " ");
        String[] str = keySearch.split(" ");
        for (MovieObject.Movie a : list) {
            if (a.getGenre_ids().contains(idGenre)) {
                for (String item : str) {
                    if (typeMovieOrTV.equals(Utils.TYPE_MOVIE)) {
                        if (a.getTitle().toLowerCase().trim().contains(item.toLowerCase().trim()) || a.getOriginal_title().toLowerCase().trim().contains(item.toLowerCase().trim()) || a.getTitle().toLowerCase().trim().equals(item.toLowerCase().trim()) || a.getOriginal_title().toLowerCase().trim().equals(item.toLowerCase().trim())) {
                            moviesMap.put(a.getId(), a);
                        }
                    } else if (typeMovieOrTV.equals(Utils.TYPE_TV_SHOW)) {
                        if (a.getName().toLowerCase().trim().contains(item.toLowerCase().trim()) || a.getOriginal_name().toLowerCase().trim().contains(item.toLowerCase().trim()) || a.getName().toLowerCase().trim().equals(item.toLowerCase().trim()) || a.getOriginal_name().toLowerCase().trim().equals(item.toLowerCase().trim())) {
                            moviesMap.put(a.getId(), a);
                        }
                    } else {

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

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutItemFilmBinding binding;
        IMovieItemClickListener itemClicked;
        MovieObject.Movie movie;

        public ViewHolder(@NonNull LayoutItemFilmBinding bindingTemp, IMovieItemClickListener itemClickedTemp) {
            super(bindingTemp.getRoot());
            binding = bindingTemp;
            this.itemClicked = itemClickedTemp;
            binding.getRoot().setOnClickListener(this);
        }

        public void bindData(MovieObject.Movie a) {
            this.movie = a;
            binding.setItemFilm(a);

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
            Log.i("AAA", "MOVIE CLICKEEEEEEEEEEEEEEEE : " + movie.getName());
            itemClicked.itemClicked(movie);
        }
    }
}
