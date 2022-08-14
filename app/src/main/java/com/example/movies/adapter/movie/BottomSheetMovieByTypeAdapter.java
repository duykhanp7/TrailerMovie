package com.example.movies.adapter.movie;

import static com.example.movies.activity.details.DetailsMovieActivity.chipTextClicked;
import static com.example.movies.activity.main.MainActivity.movieResources;
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
import com.example.movies.api.APIGetData;
import com.example.movies.databinding.LayoutItemFilmBinding;
import com.example.movies.listener.movie.IMovieItemClickListener;
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

public class BottomSheetMovieByTypeAdapter extends RecyclerView.Adapter<BottomSheetMovieByTypeAdapter.ViewHolder> {

    public int page = 1;
    public String type;
    public  String title="";
    public List<MovieObject.Movie> movieList;
    public List<MovieObject.Movie> movieListOld;
    public IMovieItemClickListener singleItemClicked;
    public String keywordSearch = "";
    public String typeMovieOrTVShow;
    public Boolean isSearching = false;
    public static int pageTemp = 1;
    public int totalPage = 0;
    public Boolean isRefresh = false;


    //CONSTRUCTOR
    public BottomSheetMovieByTypeAdapter(String typeMovieOrTVShowTemp, String type, List<MovieObject.Movie> items, IMovieItemClickListener singleItemClickedTemp){
        this.type = type.replace("\\s{2,}","");
        this.movieList = items;
        this.movieListOld = new ArrayList<>(items);
        this.singleItemClicked = singleItemClickedTemp;
        this.typeMovieOrTVShow = typeMovieOrTVShowTemp;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemFilmBinding layoutItemFilmBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_film,parent,false);
        return new ViewHolder(layoutItemFilmBinding, movieList,singleItemClicked,type,typeMovieOrTVShow);
    }

    @Override
    public synchronized void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MovieObject.Movie item = movieList.get(position);
        holder.layoutItemFilmBinding.setItemFilm(item);
        holder.setIsRecyclable(false);
        if(position >= movieList.size()-3){
            Log.i("AAA","LOAD NEXT");
            ++page;
            if(type.equals(Utils.NowPlaying) || type.equals(Utils.Popular) || type.equals(Utils.TopRated) || type.equals(Utils.UpComing) ||type.equals(Utils.AiringToday) || type.equals(Utils.OnTheAir)){
                Log.i("AAA","TYPE 1");
                if(!isSearching){
                    new Thread(() -> getMoviesAPIAtPageIndex(typeMovieOrTVShow,type,page)).start();
                }
                else{
                    Log.i("AAA","BBBBBBBBBBBBB");
                    ++pageTemp;
                    if(pageTemp <= totalPage){
                        new Thread(() -> getMoviesFromAPI(pageTemp,keywordSearch)).start();
                    }
                }
            }
            else{
                Log.i("AAA","TYPE 2");
                if(!isSearching){
                    new Thread(() -> getAllMoviesByGenre(typeMovieOrTVShow,type, String.valueOf(page))).start();
                }
                else {
                    ++pageTemp;
                    Log.i("AAA"," PAGE TEMP "+pageTemp);
                    if(pageTemp <= totalPage){
                        new Thread(() -> getMoviesFromAPI(pageTemp,keywordSearch)).start();
                    }
                }
            }
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.layoutItemFilmBinding.shimmerLayoutMovie.post(()->holder.layoutItemFilmBinding.shimmerLayoutMovie.setVisibility(View.GONE));
                holder.layoutItemFilmBinding.layoutMovieNotShimmer.post(()->holder.layoutItemFilmBinding.layoutMovieNotShimmer.setVisibility(View.VISIBLE));
            }
        },500);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void inflateToOldList(){
        if(!isRefresh){
            this.movieList = new ArrayList<>();
            this.movieList.addAll(new ArrayList<>(movieListOld));
            notifyDataSetChanged();
            isRefresh = true;
        }
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MovieObject.Movie> getMovieList() {
        return movieList;
    }

    public void setPage(int i){
        this.page = i;
    }


    public String getTypeMovieOrTVShow() {
        return typeMovieOrTVShow;
    }

    public void setTypeMovieOrTVShow(String typeMovieOrTVShow) {
        this.typeMovieOrTVShow = typeMovieOrTVShow;
    }

    public void addMovieList(List<MovieObject.Movie> movieList){
        int size = this.movieList.size();
        this.movieList.addAll(movieList);
        this.movieListOld.addAll(new ArrayList<>(movieList));
        notifyItemRangeInserted(size,movieList.size());
    }


    public synchronized void getMoviesFromAPIOnSearch(int page, String keySearch) {

        this.keywordSearch = keySearch;

        String idGenres;

        idGenres = mapGenresIDMovie.get(type);

        if (keySearch.isEmpty()) {
            Log.i("AAA"," KEY SEARCH NULL");
            inflateToOldList();
        } else {
            Log.i("AAA","TYPE SEARCH ON SEARCH "+typeMovieOrTVShow+" -- "+type+" -- "+page+" -- "+idGenres);
            APIGetData.apiGetData.getMovieByKeyword(typeMovieOrTVShow, keySearch, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<MovieObject> call, Response<MovieObject> response) {
                    try {
                        assert response.body() != null;
                        totalPage = Integer.parseInt(response.body().getTotal_pages());
                        Log.i("AAA","TOTAL PAGE : "+totalPage);
                        //List<MovieObject.Movie> filter = filterMoviesByKeyword(response.body().getMoviesList(), idGenres,keySearch);\

                        List<MovieObject.Movie> filter = new ArrayList<>();// = filterMoviesByKeyword(response.body().getMoviesList(), idGenres,keySearch);
                        if(idGenres != null){
                            filter = filterMoviesByKeyword(response.body().getMoviesList(), idGenres,keySearch);
                        }
                        else{
                            filter = response.body().getMoviesList();
                        }

                        if (filter.size() > 0) {
                            movieList = new ArrayList<>();
                            movieList.addAll(filter);
                            notifyDataSetChanged();
                        } else {
                            pageTemp = page;
                            ++pageTemp;
                            if(pageTemp <= totalPage){
                                getMoviesFromAPIOnSearch(pageTemp, keySearch);
                                setPage(pageTemp);
                            }
                        }
                        Log.i("AAA","FILTER SIZE "+filter.size());
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

    public synchronized void getMoviesFromAPI(int page, String keySearch) {

        this.keywordSearch = keySearch;

        String idGenres;

        idGenres = mapGenresIDMovie.get(type);

        if (keySearch.isEmpty()) {
            Log.i("AAA"," KEY SEARCH NULL");
            inflateToOldList();
        } else {
            Log.i("AAA","TYPE SEARCH ON SCROLL "+typeMovieOrTVShow+" -- "+type+" -- "+page+" -- "+idGenres);
            APIGetData.apiGetData.getMovieByKeyword(typeMovieOrTVShow, keySearch, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<MovieObject> call, Response<MovieObject> response) {
                    try {
                        assert response.body() != null;
                        totalPage = Integer.parseInt(response.body().getTotal_pages());
                        Log.i("AAA","TOTAL PAGE : "+totalPage);
                        List<MovieObject.Movie> filter = new ArrayList<>();// = filterMoviesByKeyword(response.body().getMoviesList(), idGenres,keySearch);
                        if(idGenres != null){
                            filter = filterMoviesByKeyword(response.body().getMoviesList(), idGenres,keySearch);
                        }
                        else{
                            filter = response.body().getMoviesList();
                        }
                        if (filter.size() > 0) {
                            int size = movieList.size();
                            movieList.addAll(filter);
                            notifyItemRangeInserted(size,movieList.size());
                        } else {
                            pageTemp = page;
                            ++pageTemp;
                            if(pageTemp <= totalPage){
                                getMoviesFromAPI(pageTemp, keySearch);
                                setPage(pageTemp);
                            }
                        }
                        Log.i("AAA","FILTER SIZE "+filter.size());
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
    public synchronized List<MovieObject.Movie> filterMoviesByKeyword(List<MovieObject.Movie> list, String idGenre,String keySearch) {
        List<MovieObject.Movie> moviesFilter;
        Map<String, MovieObject.Movie> moviesMap = new HashMap<>();
        //REPLACE ALL SPECIFIC CHARACTERS WITH SPACE, THEN REPLACE TWO OR MORE SPACE WITH ONE SPACE
        keySearch = keySearch.replaceAll(Utils.SPECIFIC_CHARACTERS, " ").replaceAll(Utils.REGEX_DOUBLE_SPACE, " ");
        String[] str = keySearch.split(" ");
        for (MovieObject.Movie a : list) {
            if (a.getGenre_ids().contains(idGenre)) {
                for (String item : str) {
                    if (Objects.equals(typeMovieOrTVShow, Utils.TYPE_MOVIE)) {
                        if (a.getTitle().toLowerCase().trim().contains(item.toLowerCase().trim()) || a.getOriginal_title().toLowerCase().trim().contains(item.toLowerCase().trim()) || a.getTitle().toLowerCase().trim().equals(item.toLowerCase().trim()) || a.getOriginal_title().toLowerCase().trim().equals(item.toLowerCase().trim())) {
                            moviesMap.put(a.getId(), a);
                        }
                    } else if (Objects.equals(typeMovieOrTVShow, Utils.TYPE_TV_SHOW)) {
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

    //GET MOVIES POPULAR AT PAGE INDEX -> PAGE > 0
    public synchronized void getMoviesAPIAtPageIndex(String typeMovieOrTVShow, String type, int page) {
        String mapType = getNameGenres(type);
        APIGetData.apiGetData.getMovies(typeMovieOrTVShow, mapType, Utils.API_MOVIE_KEY, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
            @Override
            public void onResponse(@NonNull Call<MovieObject> call, @NonNull Response<MovieObject> response) {

                try {
                    assert response.body() != null;
                    totalPage = Integer.parseInt(response.body().getTotal_pages());
                    if (response.body().getMoviesList().size() > 0) {
                        addMovieList(response.body().getMoviesList());
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

    //GET MOVIES BY GENRE ID
    public synchronized void getAllMoviesByGenre(String typeMovieOrTVShow, String type, String page) {
        String id_genre = getIdGenreByNameId(typeMovieOrTVShow, type);
        APIGetData.apiGetData.getMoviesByGenreID(typeMovieOrTVShow, Utils.API_MOVIE_KEY, id_genre, page).enqueue(new Callback<MovieObject>() {
            @Override
            public void onResponse(@NonNull Call<MovieObject> call, @NonNull Response<MovieObject> response) {
                try {
                    assert response.body() != null;
                    totalPage = Integer.parseInt(response.body().getTotal_pages());
                    if (response.body().getMoviesList().size() > 0) {
                        addMovieList(response.body().getMoviesList());
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

    public String getIdGenreByNameId(String type, String str) {
        if (type.equals(Utils.TYPE_MOVIE)) {
            for (GenreObject.Genre item : movieResources.GenresMovie.getGenres()) {
                if (item.getNameGenre().trim().equals(str)) {
                    return item.getIdGenre();
                }
            }
        } else {
            for (GenreObject.Genre item : movieResources.GenresTVShow.getGenres()) {
                if (item.getNameGenre().trim().equals(str)) {
                    return item.getIdGenre();
                }
            }
        }
        return "";
    }


    public String getNameGenres(String text) {
        switch (text) {
            case Utils.NowPlaying:
                return Utils.now_playing;
            case Utils.Popular:
                return Utils.popular;
            case Utils.UpComing:
                return Utils.upcoming;
            case Utils.TopRated:
                return Utils.top_rated;
            case Utils.AiringToday:
                return Utils.airing_today;
            case Utils.OnTheAir:
                return Utils.on_the_air;
        }

        return Utils.now_playing;
    }

    //VIEW HOLDER
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutItemFilmBinding layoutItemFilmBinding;
        List<MovieObject.Movie> movieList;
        IMovieItemClickListener singleItemClicked;
        String type,typeMovieOrTVShow;
        public ViewHolder(@NonNull LayoutItemFilmBinding layoutItemFilmBindingA, List<MovieObject.Movie> movieList, IMovieItemClickListener singleItemClicked, String type,String typeMovieOrTVShowTemp) {
            super(layoutItemFilmBindingA.getRoot());
            this.type = type;
            this.typeMovieOrTVShow = typeMovieOrTVShowTemp;
            layoutItemFilmBinding = layoutItemFilmBindingA;
            this.movieList = movieList;
            this.singleItemClicked = singleItemClicked;
            layoutItemFilmBindingA.getRoot().setOnClickListener(this);
            setTypeForMovie();
        }

        //ITEM CLICKED
        @Override
        public void onClick(View view) {
            MovieObject.Movie item = movieList.get(getAbsoluteAdapterPosition());
            chipTextClicked.set(type);
            singleItemClicked.itemClicked(item);
        }

        public void setTypeForMovie(){
            for (MovieObject.Movie movie : movieList){
                movie.setTypeMovieOrTVShow(typeMovieOrTVShow);
            }
        }

    }

}
