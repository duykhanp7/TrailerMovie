package com.example.movies.adapter.movie;

import static com.example.movies.ui.activity.details.DetailsMovieActivity.binding;
import static com.example.movies.ui.activity.details.DetailsMovieActivity.chipTextClicked;
import static com.example.movies.ui.activity.main.MainActivity.movieResources;
import static com.example.movies.resources.MovieResources.mapGenresIDMovie;

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
import com.example.movies.databinding.LayoutItemFilmBinding;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.data.model.genres.GenreObject;
import com.example.movies.data.model.movie.MovieObject;
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

    //Page hiện tại
    public int page = 1;
    //Thể loại phim
    public String type;
    public String title = "";
    //Danh sách chứa phim
    public List<MovieObject.Movie> movieList;
    public List<MovieObject.Movie> movieListOld;
    //Event click
    public IMovieItemClickListener singleItemClicked;
    //Từ khóa tìm kiếm
    public String keywordSearch = "";
    //Thể loại phim hay chương trình TVShow
    public String typeMovieOrTVShow;
    //Trạng thái đang tìm kiếm hay không
    public Boolean isSearching = false;
    public static int pageTemp = 1;
    //Tổng số page
    public int totalPage = 0;
    //Trạng thái refresh
    public Boolean isRefresh = false;

    public BottomSheetMovieByTypeAdapter(String typeMovieOrTVShowTemp, String type, List<MovieObject.Movie> items, IMovieItemClickListener singleItemClickedTemp) {
        this.type = type.replace("\\s{2,}", "");
        this.movieList = items;
        this.movieListOld = new ArrayList<>(items);
        this.singleItemClicked = singleItemClickedTemp;
        this.typeMovieOrTVShow = typeMovieOrTVShowTemp;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemFilmBinding layoutItemFilmBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_film, parent, false);
        return new ViewHolder(layoutItemFilmBinding, movieList, singleItemClicked, type, typeMovieOrTVShow);
    }

    @Override
    public synchronized void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MovieObject.Movie item = movieList.get(position);

        holder.bindData(item);

        if (position >= movieList.size() - 3) {
            ++page;
            if (type.equals(Utils.NowPlaying) || type.equals(Utils.Popular) || type.equals(Utils.TopRated) || type.equals(Utils.UpComing) || type.equals(Utils.AiringToday) || type.equals(Utils.OnTheAir)) {
                if (!isSearching) {
                    new Thread(() -> getMoviesAPIAtPageIndex(typeMovieOrTVShow, type, page)).start();
                } else {
                    ++pageTemp;
                    if (pageTemp <= totalPage) {
                        new Thread(() -> getMoviesFromAPI(pageTemp, keywordSearch)).start();
                    }
                }
            } else {
                if (!isSearching) {
                    new Thread(() -> getAllMoviesByGenre(typeMovieOrTVShow, type, String.valueOf(page))).start();
                } else {
                    ++pageTemp;
                    if (pageTemp <= totalPage) {
                        new Thread(() -> getMoviesFromAPI(pageTemp, keywordSearch)).start();
                    }
                }
            }
        }

        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    /**
     * Trả về danh sách các bộ phim thuộc thể loại nào đó nếu xóa keyword tìm kiếm
     */
    @SuppressLint("NotifyDataSetChanged")
    public void inflateToOldList() {
        if (!isRefresh) {
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

    public void setPage(int i) {
        this.page = i;
    }

    public String getTypeMovieOrTVShow() {
        return typeMovieOrTVShow;
    }

    public void setTypeMovieOrTVShow(String typeMovieOrTVShow) {
        this.typeMovieOrTVShow = typeMovieOrTVShow;
    }

    /**
     * Thêm danh sách các bộ phim
     */
    public void addMovieList(List<MovieObject.Movie> movieList) {
        int size = this.movieList.size();
        this.movieList.addAll(movieList);
        this.movieListOld.addAll(new ArrayList<>(movieList));
        notifyItemRangeInserted(size, movieList.size());
    }

    /**
     * Lấy danh sách các bộ phim dựa theo từ khóa tìm kiếm và page
     */
    public synchronized void getMoviesFromAPIOnSearch(int page, String keySearch) {

        this.keywordSearch = keySearch;

        String idGenres;

        idGenres = mapGenresIDMovie.get(type);

        //Nếu keyword là rỗng -> trả về danh sách phim thuộc thể loại đó từ page số 1
        if (keySearch.isEmpty()) {
            inflateToOldList();
        } else {
            APIGetData.apiGetData.getMovieByKeyword(typeMovieOrTVShow, keySearch, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<MovieObject> call, Response<MovieObject> response) {
                    try {
                        assert response.body() != null;
                        totalPage = Integer.parseInt(response.body().getTotal_pages());

                        List<MovieObject.Movie> filter = new ArrayList<>();// = filterMoviesByKeyword(response.body().getMoviesList(), idGenres,keySearch);
                        if (idGenres != null) {
                            filter = filterMoviesByKeyword(response.body().getMoviesList(), idGenres, keySearch);
                        } else {
                            filter = response.body().getMoviesList();
                        }

                        if (filter.size() > 0) {
                            movieList = new ArrayList<>();
                            movieList.addAll(filter);
                            notifyDataSetChanged();
                        } else {
                            pageTemp = page;
                            ++pageTemp;
                            if (pageTemp <= totalPage) {
                                getMoviesFromAPIOnSearch(pageTemp, keySearch);
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


    /**
     * Lấy danh sách các bộ phim thuộc thể loại nào đó dựa vào page và keySearch
     * khi recyclerview scroll
     */
    public synchronized void getMoviesFromAPI(int page, String keySearch) {

        this.keywordSearch = keySearch;

        String idGenres;

        idGenres = mapGenresIDMovie.get(type);

        if (keySearch.isEmpty()) {
            inflateToOldList();
        } else {
            APIGetData.apiGetData.getMovieByKeyword(typeMovieOrTVShow, keySearch, String.valueOf(page)).enqueue(new Callback<MovieObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<MovieObject> call, @NonNull Response<MovieObject> response) {
                    try {
                        assert response.body() != null;
                        totalPage = Integer.parseInt(response.body().getTotal_pages());
                        List<MovieObject.Movie> filter = new ArrayList<>();// = filterMoviesByKeyword(response.body().getMoviesList(), idGenres,keySearch);
                        if (idGenres != null) {
                            filter = filterMoviesByKeyword(response.body().getMoviesList(), idGenres, keySearch);
                        } else {
                            filter = response.body().getMoviesList();
                        }
                        if (filter.size() > 0) {
                            int size = movieList.size();
                            movieList.addAll(filter);
                            notifyItemRangeInserted(size, movieList.size());
                        } else {
                            pageTemp = page;
                            ++pageTemp;
                            if (pageTemp <= totalPage) {
                                getMoviesFromAPI(pageTemp, keySearch);
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

    /**
     * Lọc danh sách các bộ phim dựa theo thể loại và từ khóa tìm kiếm
     */
    @SuppressLint("NotifyDataSetChanged")
    public synchronized List<MovieObject.Movie> filterMoviesByKeyword(List<MovieObject.Movie> list, String idGenre, String keySearch) {
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

    /**
     * Trả về danh sách các bộ phim NowPlaying Popular TopRated UpComing AiringToday OnTheAir
     */
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

    /**
     * Trả về danh sách các bộ phim dựa theo thể loại phim, loại chương trình và page
     */
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

    /**
     * Trả về id của thê loại phim dựa theo tên
     */
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

    /**
     * Trả về tên của thể loại phim
     */
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


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutItemFilmBinding layoutItemFilmBinding;
        List<MovieObject.Movie> movieList;
        IMovieItemClickListener singleItemClicked;
        String type, typeMovieOrTVShow;
        MovieObject.Movie movie;

        public ViewHolder(@NonNull LayoutItemFilmBinding layoutItemFilmBindingA, List<MovieObject.Movie> movieList, IMovieItemClickListener singleItemClicked, String type, String typeMovieOrTVShowTemp) {
            super(layoutItemFilmBindingA.getRoot());
            this.type = type;
            this.typeMovieOrTVShow = typeMovieOrTVShowTemp;
            layoutItemFilmBinding = layoutItemFilmBindingA;
            this.movieList = movieList;
            this.singleItemClicked = singleItemClicked;
            layoutItemFilmBindingA.getRoot().setOnClickListener(this);
            setTypeForMovie();
        }

        public void bindData(MovieObject.Movie a) {
            this.movie = a;
            layoutItemFilmBinding.setItemFilm(this.movie);
            loadData();
        }

        /**
         * Load data và tạo hiệu ứng làm mở khi đang load data
         */
        public void loadData() {
            //Shimmer
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutItemFilmBinding.shimmerLayoutMovie.post(() -> layoutItemFilmBinding.shimmerLayoutMovie.setVisibility(View.GONE));
                    layoutItemFilmBinding.layoutMovieNotShimmer.post(() -> layoutItemFilmBinding.layoutMovieNotShimmer.setVisibility(View.VISIBLE));
                }
            }, 500);
        }

        //ITEM CLICKED
        @Override
        public void onClick(View view) {
            MovieObject.Movie item = movieList.get(getAbsoluteAdapterPosition());
            chipTextClicked.set(type);
            singleItemClicked.itemClicked(item);
        }

        public void setTypeForMovie() {
            for (MovieObject.Movie movie : movieList) {
                movie.setTypeMovieOrTVShow(typeMovieOrTVShow);
            }
        }

    }

}
