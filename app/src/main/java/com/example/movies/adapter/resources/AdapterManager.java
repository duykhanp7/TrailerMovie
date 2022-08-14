package com.example.movies.adapter.resources;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.example.movies.adapter.movie.MoviesAdapterByGenres;
import com.example.movies.api.APIGetData;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.listener.update.IOnRefreshData;
import com.example.movies.listener.update.IUpdateAdapter;
import com.example.movies.model.genres.GenreObject;
import com.example.movies.model.movie.MovieObject;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterManager {

    //PROPERTIES
    //TYPE MOVIE OR TV SHOW
    public String typeMovieOrTVShow;
    //MAP MOVIE ADAPTER
    public Map<String, ObservableField<MoviesAdapterByGenres>> mapListMoviesObservableFieldAdapter;
    //GENRES OBJECT
    public List<String> GenresMovie;
    IMovieItemClickListener itemClickListener;
    IUpdateAdapter iUpdateAdapter;
    IOnRefreshData iOnRefreshData;

    public AdapterManager(String type, IMovieItemClickListener itemClickListenerTemp, IUpdateAdapter iUpdateAdapterTemp, IOnRefreshData iOnRefreshDataTemp) {
        this.typeMovieOrTVShow = type;
        //MAP MOVIE ADAPTER
        mapListMoviesObservableFieldAdapter = new HashMap<>();
        //GENRES MOVIE
        GenresMovie = new ArrayList<>();
        this.itemClickListener = itemClickListenerTemp;
        this.iUpdateAdapter = iUpdateAdapterTemp;
        this.iOnRefreshData = iOnRefreshDataTemp;
        getMovieOrTVShowGenres(type);
    }


    public void updateMovieInAllGenres(MovieObject.Movie item) {
        new Thread(() -> {
            for (ObservableField<MoviesAdapterByGenres> adapter : mapListMoviesObservableFieldAdapter.values()) {
                for (MovieObject.Movie a : Objects.requireNonNull(adapter.get()).getMovieList()) {
                    if (a.getId().trim().equals(item.getId().trim())) {
                        a.setMovie(item);
                        break;
                    }
                }
            }
        }).start();
    }

    public void onRefreshData() {
        int pos = 0;
        for (ObservableField<MoviesAdapterByGenres> item : mapListMoviesObservableFieldAdapter.values()) {
            ++pos;
            Objects.requireNonNull(item.get()).onRefreshData();
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                iOnRefreshData.onStopRefreshData();
            }
        }, 1000);
    }

    private synchronized void getMovieOrTVShowGenres(String type) {
        //THREAD GET MOVIE GENRES
        APIGetData.apiGetData.getMovieGenres(type, Utils.API_MOVIE_KEY).enqueue(new Callback<GenreObject>() {
            @Override
            public void onResponse(@NonNull Call<GenreObject> call, @NonNull Response<GenreObject> response) {
                try {
                    assert response.body() != null;

                    if (type.equals(Utils.TYPE_MOVIE)) {

                        GenresMovie.add(Utils.NowPlaying);
                        GenresMovie.add(Utils.Popular);
                        GenresMovie.add(Utils.TopRated);
                        GenresMovie.add(Utils.UpComing);
                    } else if (type.equals(Utils.TYPE_TV_SHOW)) {
                        GenresMovie.add(Utils.AiringToday);
                        GenresMovie.add(Utils.OnTheAir);
                    }

                    for (GenreObject.Genre item : response.body().getGenres()) {
                        GenresMovie.add(item.getNameGenre().replace("\\s{2,}", "").trim());
                    }

                    //ADD MOVIE ADAPTER OBSERVABLE FIELD
                    for (String item : GenresMovie) {
                        ObservableField<MoviesAdapterByGenres> adapter = new ObservableField<MoviesAdapterByGenres>();
                        adapter.set(new MoviesAdapterByGenres(type, item, new ArrayList<>(10), itemClickListener));
                        mapListMoviesObservableFieldAdapter.put(item, adapter);
                    }
                    iUpdateAdapter.updateAdapter(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenreObject> call, @NonNull Throwable t) {

            }
        });
    }
}
