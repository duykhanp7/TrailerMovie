package com.example.movies.resources;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.example.movies.api.APIGetData;
import com.example.movies.listener.update.IUpdateData;
import com.example.movies.model.genres.GenreObject;
import com.example.movies.model.languages.Language;
import com.example.movies.model.movie.MovieObject;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieResources extends BaseObservable {


    public final IUpdateData updateData;
    //GENRE LIST
    public GenreObject GenresMovie;
    public GenreObject GenresTVShow;

    //LANGUAGES (ISO 639-1 tags) )
    public List<Language> languages;
    public Map<String, String> mapLanguages;

    //MAP NAME WITH ID GENRES
    public static Map<String, String> mapGenresIDMovie;
    public static Map<String, String> mapGenresIDTVShow;

    public MovieResources(IUpdateData updateData) {
        this.updateData = updateData;
        //mapAdapter = new HashMap<>();
        mapGenresIDMovie = new HashMap<>();
        mapGenresIDTVShow = new HashMap<>();
        //GENRE
        GenresMovie = new GenreObject();
        GenresTVShow = new GenreObject();
        //LANGUAGES
        languages = new ArrayList<>();
        mapLanguages = new HashMap<>();

        //RUN SOME FUNCTIONS
        //GET GENRES AND LANGUAGES
        getMovieGenresAPI(Utils.TYPE_MOVIE);
        getMovieGenresAPI(Utils.TYPE_TV_SHOW);
        getLanguages();
    }


    public void fetchDataAgain(){
        Utils.titleGenresMovie.clear();
        Utils.titleGenresTVShow.clear();
        languages.clear();
        mapLanguages.clear();
        getMovieGenresAPI(Utils.TYPE_MOVIE);
        getMovieGenresAPI(Utils.TYPE_TV_SHOW);
        getLanguages();
    }

    //GET MOVIES POPULAR AT PAGE INDEX -> PAGE > 0
    public synchronized void getMoviesAPIAtPageIndex(String typeMovieOrTVShow, String type, int page) {
        String mapType = getNameGenres(type);
        APIGetData.apiGetData.getMovies(typeMovieOrTVShow, mapType, Utils.API_MOVIE_KEY, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
            @Override
            public void onResponse(@NonNull Call<MovieObject> call, @NonNull Response<MovieObject> response) {

                try {
                    assert response.body() != null;
                    if (response.body().getMoviesList().size() > 0) {
                        updateData.update(response.body().getMoviesList(), typeMovieOrTVShow, type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<MovieObject> call, @NonNull Throwable t) {
            }
        });
    }

    //GET GENRES MOVIE
    public void getMovieGenresAPI(String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                APIGetData.apiGetData.getMovieGenres(type, Utils.API_MOVIE_KEY).enqueue(new Callback<GenreObject>() {
                    @Override
                    public void onResponse(@NonNull Call<GenreObject> call, @NonNull Response<GenreObject> response) {
                        try {
                            assert response.body() != null;
                            if (type.equals(Utils.TYPE_MOVIE)) {
                                GenresMovie = response.body();
                                Utils.titleGenresMovie.add(Utils.NowPlaying);
                                Utils.titleGenresMovie.add(Utils.Popular);
                                Utils.titleGenresMovie.add(Utils.TopRated);
                                Utils.titleGenresMovie.add(Utils.UpComing);
                                for (GenreObject.Genre item : response.body().getGenres()) {
                                    Utils.titleGenresMovie.add(item.getNameGenre());
                                    mapGenresIDMovie.put(item.getNameGenre().replace("\\s{2,}", "").trim(), item.getIdGenre());
                                }
                            } else {
                                GenresTVShow = response.body();
                                Utils.titleGenresTVShow.add(Utils.AiringToday);
                                Utils.titleGenresTVShow.add(Utils.OnTheAir);
                                for (GenreObject.Genre item : response.body().getGenres()) {
                                    Utils.titleGenresTVShow.add(item.getNameGenre());
                                    mapGenresIDTVShow.put(item.getNameGenre().replace("\\s{2,}", "").trim(), item.getIdGenre());
                                }
                            }
                            getAllMoviesFromDiscoverByGenre(type);
                            updateData.updateTitle();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GenreObject> call, @NonNull Throwable t) {

                    }
                });
            }
        }).start();
    }


    //GET MOVIES BY GENRE ID
    public synchronized void getAllMoviesByGenre(String typeMovieOrTVShow, String type, String page) {
        String id_genre = getIdGenreByNameId(typeMovieOrTVShow, type);
        APIGetData.apiGetData.getMoviesByGenreID(typeMovieOrTVShow, Utils.API_MOVIE_KEY, id_genre, page).enqueue(new Callback<MovieObject>() {
            @Override
            public void onResponse(@NonNull Call<MovieObject> call, @NonNull Response<MovieObject> response) {
                try {
                    assert response.body() != null;
                    if (response.body().getMoviesList().size() > 0) {
                        //Objects.requireNonNull(mapAdapter.get(type)).add(response.body());
                        updateData.update(response.body().getMoviesList(), typeMovieOrTVShow, type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieObject> call, @NonNull Throwable t) {
            }
        });
    }

    //LOOP FOR GET ALL MOVIES BY GENRE
    public void getAllMoviesFromDiscoverByGenre(String type) {
        if (type.equals(Utils.TYPE_MOVIE)) {
            for (int i = 0; i < GenresMovie.getGenres().size(); i++) {
                int finalI = i;
                new Thread(() -> {
                    GenreObject.Genre item = GenresMovie.getGenres().get(finalI);
                    getAllMoviesByGenre(type, Utils.typeArrayMovie.get(finalI), String.valueOf(1));
                }).start();
            }
        } else if (type.equals(Utils.TYPE_TV_SHOW)) {
            for (int i = 0; i < GenresTVShow.getGenres().size(); i++) {
                int finalI = i;
                new Thread(() -> {
                    GenreObject.Genre item = GenresTVShow.getGenres().get(finalI);
                    getAllMoviesByGenre(type, Utils.typeArrayTVShow.get(finalI), String.valueOf(1));
                }).start();
            }
        }
    }


    //GET LANGUAGES
    public synchronized void getLanguages() {
        APIGetData.apiGetData.getLanguages().enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(@NonNull Call<List<Language>> call, @NonNull Response<List<Language>> response) {
                try {
                    assert response.body() != null;
                    languages.addAll(response.body());

                    for (Language e : languages) {
                        mapLanguages.put(e.getIso_639_1(), e.getEnglish_name());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Language>> call, @NonNull Throwable t) {

            }
        });
    }

    public String getIdGenreByNameId(String type, String str) {
        if (type.equals(Utils.TYPE_MOVIE)) {
            for (GenreObject.Genre item : GenresMovie.getGenres()) {
                if (item.getNameGenre().trim().equals(str)) {
                    return item.getIdGenre();
                }
            }
        } else {
            for (GenreObject.Genre item : GenresTVShow.getGenres()) {
                if (item.getNameGenre().trim().equals(str)) {
                    return item.getIdGenre();
                }
            }
        }
        return "";
    }

    public String getNameGenres(String text) {
        if (text.equals(Utils.NowPlaying)) {
            return Utils.now_playing;
        } else if (text.equals(Utils.Popular)) {
            return Utils.popular;
        } else if (text.equals(Utils.UpComing)) {
            return Utils.upcoming;
        } else if (text.equals(Utils.TopRated)) {
            return Utils.top_rated;
        } else if (text.equals(Utils.AiringToday)) {
            return Utils.airing_today;
        } else if (text.equals(Utils.OnTheAir)) {
            return Utils.on_the_air;
        }

        return Utils.now_playing;
    }


    //GENRES
    @Bindable
    public GenreObject getGenresMovie() {
        return GenresMovie;
    }

    public void setGenresMovie(GenreObject genresMovie) {
        GenresMovie = genresMovie;
        this.notifyPropertyChanged(BR.genresMovie);
    }

}
