package com.example.movies.listener.movie;

import com.example.movies.data.model.movie.MovieObject;

public interface IRecommendationsClickListener {
    void onRecommendationItemClick(MovieObject.Movie movie);
}
