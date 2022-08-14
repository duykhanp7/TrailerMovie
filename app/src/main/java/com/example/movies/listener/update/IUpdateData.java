package com.example.movies.listener.update;

import com.example.movies.model.movie.MovieObject;
import java.util.List;

public interface IUpdateData {
    void update(List<MovieObject.Movie> movies,String typeMovieOrTVShow,String type);
    void updateTitle();
}
