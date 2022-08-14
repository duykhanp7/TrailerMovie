package com.example.movies.activity.details;

import static com.example.movies.activity.main.MainActivity.parentAdapterMovieObservableField;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

import com.example.movies.activity.main.MainActivity;
import com.example.movies.activity.youtube.WatchTrailerActivity;
import com.example.movies.adapter.cast.CastAdapter;
import com.example.movies.adapter.cast.CastDetailsAdapter;
import com.example.movies.adapter.crew.CrewAdapter;
import com.example.movies.adapter.crew.CrewDetailsAdapter;
import com.example.movies.adapter.movie.MovieAdapterMovieID;
import com.example.movies.adapter.search.SearchMovieAdapterByGenres;
import com.example.movies.adapter.videos.VideosAdapter;
import com.example.movies.api.APIGetData;
import com.example.movies.R;
import com.example.movies.databinding.ActivityMovieDetailsBinding;
import com.example.movies.listener.cast.ICastItemClickListener;
import com.example.movies.listener.crew.ICrewItemClickListener;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.listener.movie.IRecommendationsClickListener;
import com.example.movies.listener.videos.ITrailerItemClickListener;
import com.example.movies.model.cast.Cast;
import com.example.movies.model.crew.Crew;
import com.example.movies.model.movie.MovieObject;
import com.example.movies.model.videos.TrailerObject;
import com.example.movies.utils.Utils;
import com.google.android.youtube.player.YouTubeBaseActivity;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsMovieActivity extends YouTubeBaseActivity implements ITrailerItemClickListener, IRecommendationsClickListener, ICastItemClickListener, ICrewItemClickListener, IMovieItemClickListener {

    public MovieObject.Movie item;
    boolean stateEmptyWhenFirstClicked = true;
    //boolean buttonFavoritesClicked = false;
    public ObservableField<Boolean> buttonFavoritesClickedObservable;
    public Boolean expandableDetailsCastAndCrew;
    public ObservableField<Boolean> expandableDetailsCastAndCrewObservable;
    public static boolean hadSearch = false;
    @SuppressLint("StaticFieldLeak")
    public static ActivityMovieDetailsBinding binding;
    //public static String typeMovieOrTVShow;
    public static ObservableField<String> typeMovieOrTVShowObservableField;


    CastAdapter castAdapter;
    CrewAdapter crewAdapter;
    VideosAdapter videosAdapter;
    MovieAdapterMovieID recommendationAdapter;
    MovieAdapterMovieID similarAdapter;
    CastDetailsAdapter castDetailsAdapter;
    CrewDetailsAdapter crewDetailsAdapter;

    public ObservableField<CastAdapter> castAdapterObservableField;
    public ObservableField<CrewAdapter> crewAdapterObservableField;
    public ObservableField<VideosAdapter> videosAdapterObservableField;
    public ObservableField<MovieAdapterMovieID> recommendationsAdapterObservableField;
    public ObservableField<MovieAdapterMovieID> similarAdapterObservableField;
    public ObservableField<MovieObject.Movie> movieObservableField;
    public ObservableField<MovieObject.Movie> oldMovieObservableField;
    public ObservableField<CastDetailsAdapter> castDetailsAdapterObservableField;
    public ObservableField<CrewDetailsAdapter> crewDetailsAdapterObservableField;
    //public static ObservableField<MoviesAdapterByGenres> moviesAdapterByGenresObservableFieldDetails;
    public static ObservableField<SearchMovieAdapterByGenres> searchMovieAdapterByGenresObservableField;

    public static ObservableField<Boolean> movieByChipGenres;
    public static ObservableField<String> chipTextClicked = new ObservableField<>();
    public static ObservableField<Boolean> buttonSearchClicked;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = getIntent().getBundleExtra("bundle");
        item = (MovieObject.Movie) bundle.getSerializable("item");
        //typeMovieOrTVShow = positionTab == 0 ? Utils.TYPE_MOVIE : Utils.TYPE_TV_SHOW;
        expandableDetailsCastAndCrew = false;
        expandableDetailsCastAndCrewObservable = new ObservableField<>(expandableDetailsCastAndCrew);

        castAdapter = new CastAdapter(this);
        crewAdapter = new CrewAdapter(this);
        videosAdapter = new VideosAdapter(this,item);

        recommendationAdapter = new MovieAdapterMovieID(item.getTypeMovieOrTVShow(),item,this, Utils.recommendations);
        similarAdapter = new MovieAdapterMovieID(item.getTypeMovieOrTVShow(),item,this,Utils.similar);
        castDetailsAdapter = new CastDetailsAdapter(this);
        crewDetailsAdapter = new CrewDetailsAdapter(this);

        castAdapterObservableField = new ObservableField<>(castAdapter);
        crewAdapterObservableField = new ObservableField<>(crewAdapter);
        videosAdapterObservableField = new ObservableField<>(videosAdapter);
        recommendationsAdapterObservableField = new ObservableField<>(recommendationAdapter);
        similarAdapterObservableField = new ObservableField<>(similarAdapter);
        movieObservableField = new ObservableField<>(item);

        oldMovieObservableField = new ObservableField<>();
        castDetailsAdapterObservableField = new ObservableField<>(castDetailsAdapter);
        crewDetailsAdapterObservableField = new ObservableField<>(crewDetailsAdapter);
        //moviesAdapterByGenresObservableFieldDetails = new ObservableField<>();
        searchMovieAdapterByGenresObservableField = new ObservableField<>();
        searchMovieAdapterByGenresObservableField.set(new SearchMovieAdapterByGenres(new ArrayList<>(),this));
        Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).getMoviesFromAPI(1,"");
        movieByChipGenres = new ObservableField<>(false);
        chipTextClicked = new ObservableField<>("Comedy");
        buttonSearchClicked = new ObservableField<>(false);
        buttonFavoritesClickedObservable = new ObservableField<>();

        binding = DataBindingUtil.setContentView(this,R.layout.activity_movie_details);
        binding.setMain(this);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
            Log.i("AAA","NOT DARK MODE");
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            binding.expandTv.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));

            binding.layoutTopCorner.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_corner_top_white));
            binding.toolbarLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.white));
            binding.nestedScrollView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.white));
            binding.recyclerViewCastDetails.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.white));
            binding.recyclerViewCrewDetails.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.white));
            binding.recyclerViewByChipGenre.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.white));
            binding.layoutCharacterDetail.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.white));
            binding.textCharacter.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.black));
            binding.constraint.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.white));
            binding.layoutCharacterMain.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.white));

            binding.textTitleMovie.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.black));
            binding.textTitleCast.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.black));
            binding.textTitleCrew.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.black));
            binding.textTitleMovie.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.black));
            binding.textTitleSimilar.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.black));
            binding.textTitleRecommend.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.black));
        }
        else if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            Log.i("AAA","DARK MODE");
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            getWindow().getDecorView().setSystemUiVisibility(~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            binding.expandTv.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));

            binding.layoutTopCorner.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_corner_top_black));
            binding.toolbarLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.black));
            binding.nestedScrollView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.black));
            binding.recyclerViewCastDetails.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.black));
            binding.recyclerViewCrewDetails.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.black));
            binding.recyclerViewByChipGenre.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.black));
            binding.layoutCharacterDetail.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.black));
            binding.textCharacter.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.white));
            binding.constraint.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.black));
            binding.layoutCharacterMain.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.black));

            binding.textTitleMovie.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.white));
            binding.textTitleCast.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.white));
            binding.textTitleCrew.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.white));
            binding.textTitleMovie.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.white));
            binding.textTitleSimilar.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.white));
            binding.textTitleRecommend.setTextColor(ContextCompat.getColorStateList(getApplicationContext(),R.color.white));

        }

        renderUIDetails();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeBackgroundIconFavorite(item);
            }
        });
        //runOnUiThread(() -> binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.favorites_ic_normal)));

        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(!v.canScrollVertically(-1)){
                Animation animationGone = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_floating_hide);
                binding.buttonScrollToTop.startAnimation(animationGone);
                binding.buttonScrollToTop.setVisibility(View.GONE);
            }
            else{
                Animation animationShow = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_floating_show);
                binding.buttonScrollToTop.startAnimation(animationShow);
                binding.buttonScrollToTop.setVisibility(View.VISIBLE);
            }
        });

        binding.recyclerViewByChipGenre.setOnScrollChangeListener((view, i, i1, i2, i3) -> {
            if(!view.canScrollVertically(-1)){
                binding.buttonScrollToTop.setVisibility(View.GONE);
                Animation animationGone = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_floating_hide);
                binding.buttonScrollToTop.startAnimation(animationGone);
            }
            else{
                Animation animationShow = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_floating_show);
                binding.buttonScrollToTop.startAnimation(animationShow);
                binding.buttonScrollToTop.setVisibility(View.VISIBLE);
            }
        });


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextSubmit(String query) {
                hadSearch = true;
                Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).clearData();
                Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).getMoviesFromAPI(1,query);
                closeKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    if(hadSearch){
                        hadSearch = false;
                        Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).clearData();
                        Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).getMoviesFromAPI(1,"");
                        closeKeyboard();
                    }
                }
                return true;
            }
        });
    }


    //FETCH TO GET DETAILS INFORMATION OF MOVIE AND BIND TO VIEW
    @SuppressLint("NotifyDataSetChanged")
    public void renderUIDetails(){
        //CHECK IF SIZE OF TRAILER OF ITEM IS GREATER THAN 0
        // IF GREATER THAN : NOT DO FUNCTION IN IF CONDITION BELOW AND DO ELSE
        //DO FUNCTION IF BELOW IF TRAILERS IS EMPTY
        buttonFavoritesClickedObservable.set(false);
        expandableDetailsCastAndCrew = false;
        expandableDetailsCastAndCrewObservable.set(expandableDetailsCastAndCrew);
        String typeMovieOrTVShow = item.getTypeMovieOrTVShow();
        if(item.getTrailers().getTrailersList().size() == 0){
            stateEmptyWhenFirstClicked = true;
            APIGetData.apiGetData.getDetailsMovieInformation(item.getTypeMovieOrTVShow(),item.getId()).enqueue(new Callback<MovieObject.Movie>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<MovieObject.Movie> call, @NonNull Response<MovieObject.Movie> response) {
                    try {
                        item = response.body();

                        Objects.requireNonNull(item).setTypeMovieOrTVShow(typeMovieOrTVShow);


                        for (MovieObject.Movie item : item.getSimilar().getMoviesList()){
                            item.setTypeMovieOrTVShow(typeMovieOrTVShow);
                        }

                        for (MovieObject.Movie item : item.getRecommendations().getMoviesList()){
                            item.setTypeMovieOrTVShow(typeMovieOrTVShow);
                        }

                        movieObservableField.set(item);
                        castAdapter.setCasts(Objects.requireNonNull(item).getStaff().getCasts());
                        crewAdapter.setCrews(item.getStaff().getCrews());
                        videosAdapter.setTrailers(item.getTrailers().getTrailersList());
                        recommendationAdapter.setMoviesList(item.getRecommendations().getMoviesList());
                        similarAdapter.setMoviesList(item.getSimilar().getMoviesList());
                        castDetailsAdapter.setCasts(Objects.requireNonNull(item).getStaff().getCasts());
                        crewDetailsAdapter.setCrews(item.getStaff().getCrews());

                        Objects.requireNonNull(castAdapterObservableField.get()).notifyDataSetChanged();
                        Objects.requireNonNull(crewAdapterObservableField.get()).notifyDataSetChanged();
                        Objects.requireNonNull(videosAdapterObservableField.get()).notifyDataSetChanged();
                        Objects.requireNonNull(recommendationsAdapterObservableField.get()).notifyDataSetChanged();
                        Objects.requireNonNull(similarAdapterObservableField.get()).notifyDataSetChanged();
                        Objects.requireNonNull(castAdapterObservableField.get()).notifyDataSetChanged();
                        Objects.requireNonNull(crewAdapterObservableField.get()).notifyDataSetChanged();
                        Objects.requireNonNull(parentAdapterMovieObservableField.get()).adapterManager.updateMovieInAllGenres(item);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieObject.Movie> call, @NonNull Throwable t) {
                }
            });

        }
        //ELSE DO THIS IF TRAILERS IS NOT NULL
        else{
            stateEmptyWhenFirstClicked = false;// IF NOT NULL TICK IT TO FALSE
            castAdapter.setCasts(Objects.requireNonNull(item).getStaff().getCasts());
            crewAdapter.setCrews(Objects.requireNonNull(item).getStaff().getCrews());
            videosAdapter.setTrailers(item.getTrailers().getTrailersList());
            recommendationAdapter.setMoviesList(item.getRecommendations().getMoviesList());
            similarAdapter.setMoviesList(item.getSimilar().getMoviesList());
            castDetailsAdapter.setCasts(Objects.requireNonNull(item).getStaff().getCasts());
            crewDetailsAdapter.setCrews(item.getStaff().getCrews());

            castAdapterObservableField.set(castAdapter);
            crewAdapterObservableField.set(crewAdapter);
            videosAdapterObservableField.set(videosAdapter);
            recommendationsAdapterObservableField.set(recommendationAdapter);
            similarAdapterObservableField.set(similarAdapter);
            castDetailsAdapterObservableField.set(castDetailsAdapter);
            crewDetailsAdapterObservableField.set(crewDetailsAdapter);

        }
    }


    //ON BACK PRESSED EVENT
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed() {
        if(Boolean.TRUE.equals(movieByChipGenres.get())){
            if(hadSearch){
                Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).setKeySearch("");
                Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).clearData();
                Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).getMoviesFromAPI(1,"");
            }
            else{
                binding.searchView.setQuery("",false);
                movieByChipGenres.set(false);
            }
        }
        else{
            super.onBackPressed();
        }
        buttonSearchClicked.set(false);
        hadSearch = false;
    }

    //REFRESH LAYOUT WHEN CLICK ON ANOTHER MOVIE ITEM
    public void refreshUIDetails(MovieObject.Movie movie){
        renderUIDetails();
        changeBackgroundIconFavorite(movie);
        movieObservableField.set(movie);
        runOnUiThread(() -> {
            binding.nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
            binding.nestedScrollView.smoothScrollTo(0,0);
            binding.recyclerViewCast.smoothScrollToPosition(0);
            binding.recyclerViewRecommendations.smoothScrollToPosition(0);
            binding.recyclerViewSimilar.smoothScrollToPosition(0);
        });
    }


    //BUTTON ADD TO FAVORITES
    public void onButtonAddToFavorites(View view, MovieObject.Movie movie){
        runOnUiThread(() -> {
            if(Boolean.FALSE.equals(buttonFavoritesClickedObservable.get())){
                binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.favorites_ic_clicked));
                MainActivity.addMovieToFavorites(movie);
            }
            else{
                binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.favorites_ic_normal));
                MainActivity.removeMovieOutOfFavorites(movie);
            }
            //buttonFavoritesClicked = !buttonFavoritesClicked;
            buttonFavoritesClickedObservable.set(Boolean.FALSE.equals(buttonFavoritesClickedObservable.get()));
        });
    }


    //BUTTON SHOW DETAILS CAST AND CREW
    public void onButtonShowDetailsCastAndCrew(View view){
        new Thread(() -> {
            expandableDetailsCastAndCrew = !expandableDetailsCastAndCrew;
            expandableDetailsCastAndCrewObservable.set(expandableDetailsCastAndCrew);
        }).start();
    }

    //ON BUTTON BACK PRESSED
    public void onIconBackPressed(View view){
        onBackPressed();
    }

    //ON ITEM TRAILER CLICK
    //START WATCH TRAILER IF CLICKED
    @Override
    public void onTrailerItemClick(TrailerObject.Trailer trailer) {
        Intent intentWatchTrailer = new Intent(DetailsMovieActivity.this, WatchTrailerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("trailer",trailer);
        intentWatchTrailer.putExtra("bundle",bundle);
        startActivity(intentWatchTrailer);
    }

    //REFRESH LAYOUT MOVIE DETAILS ON OTHERS MOVIE CLICK
    @Override
    public void onRecommendationItemClick(MovieObject.Movie movie) {
        this.item = movie;
        refreshUIDetails(movie);
    }


    //ON ITEM CAST CLICK
    @Override
    public void onItemCastClick(Cast cast) {
        onCastItemClickFunction(cast);
    }

    public void onCastItemClickFunction(Cast cast){
        new Thread(() -> APIGetData.apiGetData.getCastDetails(cast.getId()).enqueue(new Callback<Cast>() {
            @Override
            public void onResponse(@NonNull Call<Cast> call, @NonNull Response<Cast> response) {
                try {
                    Cast castDetails = response.body();
                    String type = Utils.TYPE_CAST;
                    Intent intentCastClick = new Intent(DetailsMovieActivity.this,DetailsCharacterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type",type);
                    bundle.putSerializable("cast",castDetails);
                    intentCastClick.putExtra("bundle",bundle);
                    startActivity(intentCastClick);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Cast> call, @NonNull Throwable t) {
            }
        })).start();
    }


    //ON ITEM CREW CLICKED
    @Override
    public void onItemCrewClick(Crew crew) {
        onCrewItemClickFunction(crew);
    }

    public void onCrewItemClickFunction(Crew crew){
        new Thread(() -> APIGetData.apiGetData.getCrewDetails(crew.getId()).enqueue(new Callback<Crew>() {
            @Override
            public void onResponse(@NonNull Call<Crew> call, @NonNull Response<Crew> response) {
                try {
                    Crew crewDetails = response.body();
                    String type = Utils.TYPE_CREW;
                    Intent intentCastClick = new Intent(DetailsMovieActivity.this,DetailsCharacterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type",type);
                    bundle.putSerializable("crew",crewDetails);
                    intentCastClick.putExtra("bundle",bundle);
                    startActivity(intentCastClick);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Crew> call, @NonNull Throwable t) {

            }
        })).start();
    }


    //ON BUTTON SCROLL TO TOP CLICKED
    public void onButtonScrollToTopClicked(){
        binding.nestedScrollView.smoothScrollTo(0,0);
        if(binding.recyclerViewByChipGenre.getVisibility() == View.VISIBLE){
            binding.recyclerViewByChipGenre.smoothScrollToPosition(0);
        }
    }

    //ON BUTTON SEARCH CLICKED
    public void onButtonSearchClicked(){
        binding.searchView.post(new Runnable() {
            @Override
            public void run() {
                binding.searchView.requestFocus();
                openKeyboard();
            }
        });
        buttonSearchClicked.set(true);
    }


    //OPEN KEYBOARD
    public void openKeyboard(){
        @SuppressLint("ServiceCast") InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(binding.searchView,InputMethodManager.SHOW_FORCED);
    }

    //CLOSE KEYBOARD
    public void closeKeyboard(){
        @SuppressLint("ServiceCast") InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.searchView.getWindowToken(),0);
    }

    public static String getCurrentChipText(){
        return binding.textViewMovieGenre.getText().toString();
    }


    public void changeBackgroundIconFavorite(MovieObject.Movie movie){
        //CHECK MOVIE ADDED TO FAVORITES
        boolean check = MainActivity.isMovieAddedToFavorites(movie);
        buttonFavoritesClickedObservable.set(check);
        if(Boolean.TRUE.equals(buttonFavoritesClickedObservable.get())){
            binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.favorites_ic_clicked));
        }
        else{
            binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.favorites_ic_normal));
        }
    }


    @Override
    public void itemClicked(MovieObject.Movie item) {
        this.item = item;
        refreshUIDetails(item);
        buttonSearchClicked.set(false);
        movieByChipGenres.set(false);
        hadSearch = false;
    }
}
