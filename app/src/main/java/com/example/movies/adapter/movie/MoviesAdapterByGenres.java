package com.example.movies.adapter.movie;

import static com.example.movies.ui.activity.details.DetailsMovieActivity.chipTextClicked;
import static com.example.movies.ui.activity.main.MainActivity.movieResources;

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
import com.example.movies.databinding.LayoutItemFilmBinding;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MoviesAdapterByGenres extends RecyclerView.Adapter<MoviesAdapterByGenres.ViewHolder> {

    //Page
    public int page = 1;
    //Thể loại
    public String type;
    public String title = "";
    //Danh sách phim
    public List<MovieObject.Movie> movieList;
    public Map<String, MovieObject.Movie> mapMovies = new HashMap<>();
    public IMovieItemClickListener singleItemClicked;
    //Keyword
    public String keywordSearch = "";
    //Loại chương trình
    public String typeMovieOrTVShow;


    //CONSTRUCTOR
    public MoviesAdapterByGenres(String typeMovieOrTVShowTemp, String type, List<MovieObject.Movie> items, IMovieItemClickListener singleItemClickedTemp) {
        this.type = type;
        this.movieList = items;
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
        if (position == movieList.size() - 3) {
            ++page;
            if (type.equals(Utils.NowPlaying) || type.equals(Utils.Popular) || type.equals(Utils.TopRated) || type.equals(Utils.UpComing) || type.equals(Utils.AiringToday) || type.equals(Utils.OnTheAir)) {
                new Thread(() -> movieResources.getMoviesAPIAtPageIndex(typeMovieOrTVShow, type, page)).start();
            } else {
                new Thread(() -> movieResources.getAllMoviesByGenre(typeMovieOrTVShow, type, String.valueOf(page))).start();
            }
        }
        //holder.setIsRecyclable(false);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
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

    public void updateItem(MovieObject.Movie item){
        mapMovies.put(item.getId(),item);
        for (MovieObject.Movie a : movieList) {
            if (a.getId().trim().equals(item.getId().trim())) {
                a.setMovie(item);
                break;
            }
        }
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

    public void addMovieList(List<MovieObject.Movie> movieList) {
        int size = this.movieList.size();
        this.movieList.addAll(movieList);
        for (MovieObject.Movie item : movieList) {
            mapMovies.put(item.getId(), item);
        }
        notifyItemRangeInserted(size, movieList.size());
    }


    @SuppressLint("NotifyDataSetChanged")
    public void onRefreshData() {
        movieList = new ArrayList<>();
        notifyDataSetChanged();
        page = 1;
        if (type.equals(Utils.NowPlaying) || type.equals(Utils.Popular) || type.equals(Utils.TopRated) || type.equals(Utils.UpComing) || type.equals(Utils.AiringToday) || type.equals(Utils.OnTheAir)) {
            new Thread(() -> movieResources.getMoviesAPIAtPageIndex(typeMovieOrTVShow, type, page)).start();
        } else {
            new Thread(() -> movieResources.getAllMoviesByGenre(typeMovieOrTVShow, type, String.valueOf(page))).start();
        }
    }


    //VIEW HOLDER
    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
