package com.example.movies.data.api;

import com.example.movies.data.model.cast.Cast;
import com.example.movies.data.model.crew.Crew;
import com.example.movies.data.model.genres.GenreObject;
import com.example.movies.data.model.languages.Language;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.data.model.videos.TrailerObject;

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

    //https://api.themoviedb.org/3/movie/popular?api_key=904b3059ddd54e71c45dc72502d59375&page=1;

    /**
     * Trả về danh sách phim LATEST, NOW PLAYING, POPULAR, TOP RATE AND UP COMING BY GENRE
     */
    @GET("3/{movieOrTV}/{type}")
    Call<MovieObject> getMovies(@Path("movieOrTV") String movieOrTV, @Path("type") String type, @Query("api_key") String api_key, @Query("page") String page);

    //https://api.themoviedb.org/3/movie/{id}/videos?api_key=904b3059ddd54e71c45dc72502d59375

    /**
     * Trả về danh sách các trailer của phim dựa trên id phim
     */
    @GET("3/{movieOrTV}/{id}/videos")
    Call<TrailerObject> getTrailers(@Path("movieOrTV") String movieOrTV, @Path("id") String id, @Query("api_key") String api_key);

    //https://api.themoviedb.org/3/genre/movie/list?api_key=904b3059ddd54e71c45dc72502d59375

    /**
     * Trả về danh sách thể loại phim hoặc chương trình tv
     */
    @GET("/3/genre/{movieOrTV}/list")
    Call<GenreObject> getMovieGenres(@Path("movieOrTV") String movieOrTV, @Query("api_key") String api_key);

    // PAGE RANGE 1 - 500
    //https://api.themoviedb.org/3/discover/movie?api_key=904b3059ddd54e71c45dc72502d59375&with_genres=28&page=1

    /**
     * Trả về danh sách phim dựa trên thể loại chương trình
     */
    @GET("/3/discover/{movieOrTV}")
    Call<MovieObject> getMoviesByGenreID(@Path("movieOrTV") String movieOrTV
            , @Query("api_key") String api_key
            , @Query("with_genres") String with_genresId
            , @Query("page") String pageNumber);

    //https://api.themoviedb.org/3/movie/414906?api_key=904b3059ddd54e71c45dc72502d59375&append_to_response=videos,credits,recommendations,similar,external_ids

    /**
     * Trả về thông tin của một bộ phim
     */
    @GET("3/{movieOrTV}/{id}?api_key=904b3059ddd54e71c45dc72502d59375&append_to_response=videos,credits,recommendations,similar,external_ids")
    Call<MovieObject.Movie> getDetailsMovieInformation(@Path("movieOrTV") String movieOrTV, @Path("id") String id);

    //https://api.themoviedb.org/3/movie/{movie_id}/{type}?api_key=904b3059ddd54e71c45dc72502d59375&page=1
    //TYPE : recommendations or similar

    /**
     * Trả về danh sách các bộ phim đề xuất, tương tự với bộ phim hiện tại
     */
    @GET("3/{movieOrTV}/{id}/{type}")
    Call<MovieObject> getMoviesByIdAndTitle(@Path("movieOrTV") String movieOrTV
            , @Path("id") String id
            , @Path("type") String type
            , @Query("api_key") String api_key
            , @Query("page") String page);

    //https://api.themoviedb.org/3/configuration/languages?api_key=904b3059ddd54e71c45dc72502d59375

    /**
     * Trả về danh sách các ngôn ngữ
     */
    @GET("3/configuration/languages?api_key=904b3059ddd54e71c45dc72502d59375")
    Call<List<Language>> getLanguages();

    //https://api.themoviedb.org/3/person/11288?api_key=904b3059ddd54e71c45dc72502d59375&append_to_response=movie_credits,tv_credits,external_ids,images

    /**
     * Trả về thông tin của diễn viên
     */
    @GET("3/person/{id}?api_key=904b3059ddd54e71c45dc72502d59375&append_to_response=movie_credits,tv_credits,external_ids,images")
    Call<Cast> getCastDetails(@Path("id") String id);

    /**
     * Trả về thông tin chi tiết của đạo diễn
     */
    @GET("3/person/{id}?api_key=904b3059ddd54e71c45dc72502d59375&append_to_response=movie_credits,tv_credits,external_ids,images")
    Call<Crew> getCrewDetails(@Path("id") String id);

    //https://api.themoviedb.org/3/search/movie?api_key=904b3059ddd54e71c45dc72502d59375&query=the+avengers&page=2

    /**
     * Trả về danh sách các bộ phim dựa trên keyword
     */
    @GET("3/search/{movieOrTV}?api_key=904b3059ddd54e71c45dc72502d59375")
    Call<MovieObject> getMovieByKeyword(@Path("movieOrTV") String movieOrTV, @Query("query") String keyword, @Query("page") String page);
}
