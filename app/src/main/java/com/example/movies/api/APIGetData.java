package com.example.movies.api;

import com.example.movies.model.cast.Cast;
import com.example.movies.model.crew.Crew;
import com.example.movies.model.genres.GenreObject;
import com.example.movies.model.languages.Language;
import com.example.movies.model.movie.MovieObject;
import com.example.movies.model.videos.TrailerObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface APIGetData {

    //LINK API : https://api.themoviedb.org/3/movie/popular?api_key=904b3059ddd54e71c45dc72502d59375;
    String baseUrl = "https://api.themoviedb.org/";
    APIGetData apiGetData = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(APIGetData.class);


    //GET LIST MOVIES LATEST, NOW PLAYING, POPULAR, TOP RATE AND UP COMING BY GENRE
    //https://api.themoviedb.org/3/movie/popular?api_key=904b3059ddd54e71c45dc72502d59375&page=1;
    @GET("3/{movieOrTV}/{type}")
    Call<MovieObject> getMovies(@Path("movieOrTV") String movieOrTV, @Path("type") String type, @Query("api_key") String api_key, @Query("page") String page);

    //GET VIDEOS TRAILER OF MOVIE WITH ID.
    //https://api.themoviedb.org/3/movie/{id}/videos?api_key=904b3059ddd54e71c45dc72502d59375
    @GET("3/{movieOrTV}/{id}/videos")
    Call<TrailerObject> getTrailers(@Path("movieOrTV") String movieOrTV, @Path("id") String id, @Query("api_key") String api_key);

    //GET LIST GENRE MOVIES.
    //https://api.themoviedb.org/3/genre/movie/list?api_key=904b3059ddd54e71c45dc72502d59375
    @GET("/3/genre/{movieOrTV}/list")
    Call<GenreObject> getMovieGenres(@Path("movieOrTV") String movieOrTV, @Query("api_key") String api_key);

    //GET LIST MOVIES BY GENRE ID.
    // PAGE RANGE 1 - 500
    //https://api.themoviedb.org/3/discover/movie?api_key=904b3059ddd54e71c45dc72502d59375&with_genres=28&page=1
    @GET("/3/discover/{movieOrTV}")
    Call<MovieObject> getMoviesByGenreID(@Path("movieOrTV") String movieOrTV
            , @Query("api_key") String api_key
            , @Query("with_genres") String with_genresId
            , @Query("page") String pageNumber);

    //GET DETAILS INFORMATION AND TRAILERS
    //https://api.themoviedb.org/3/movie/414906?api_key=904b3059ddd54e71c45dc72502d59375&append_to_response=videos,credits,recommendations,similar,external_ids
    @GET("3/{movieOrTV}/{id}?api_key=904b3059ddd54e71c45dc72502d59375&append_to_response=videos,credits,recommendations,similar,external_ids")
    Call<MovieObject.Movie> getDetailsMovieInformation(@Path("movieOrTV") String movieOrTV, @Path("id") String id);

    //GET MOVIES RECOMMENDATION BY MOVIE ID
    //https://api.themoviedb.org/3/movie/{movie_id}/{type}?api_key=904b3059ddd54e71c45dc72502d59375&page=1
    //TYPE : recommendations or similar
    @GET("3/{movieOrTV}/{id}/{type}")
    Call<MovieObject> getMoviesByIdAndTitle(@Path("movieOrTV") String movieOrTV
            , @Path("id") String id
            , @Path("type") String type
            , @Query("api_key") String api_key
            , @Query("page") String page);

    //GET THE LIST OF LANGUAGE (ISO 639-1 tags)
    //https://api.themoviedb.org/3/configuration/languages?api_key=904b3059ddd54e71c45dc72502d59375
    @GET("3/configuration/languages?api_key=904b3059ddd54e71c45dc72502d59375")
    Call<List<Language>> getLanguages();

    //GET DETAILS INFORMATION OF CAST OR CREW
    //https://api.themoviedb.org/3/person/11288?api_key=904b3059ddd54e71c45dc72502d59375&append_to_response=movie_credits,tv_credits,external_ids,images
    @GET("3/person/{id}?api_key=904b3059ddd54e71c45dc72502d59375&append_to_response=movie_credits,tv_credits,external_ids,images")
    Call<Cast> getCastDetails(@Path("id") String id);

    //GET CREW DETAILS
    @GET("3/person/{id}?api_key=904b3059ddd54e71c45dc72502d59375&append_to_response=movie_credits,tv_credits,external_ids,images")
    Call<Crew> getCrewDetails(@Path("id") String id);

    //GET MOVIES BY KEYWORD, THEN FILTER IT WITH GENRES ID
    //https://api.themoviedb.org/3/search/movie?api_key=904b3059ddd54e71c45dc72502d59375&query=the+avengers&page=2
    @GET("3/search/{movieOrTV}?api_key=904b3059ddd54e71c45dc72502d59375")
    Call<MovieObject> getMovieByKeyword(@Path("movieOrTV") String movieOrTV, @Query("query") String keyword, @Query("page") String page);
}
