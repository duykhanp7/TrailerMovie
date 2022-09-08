package com.example.movies.ui.activity.details;

import static com.example.movies.ui.activity.main.MainActivity.globalContextLanguage;
import static com.example.movies.ui.activity.main.MainActivity.globalSetting;
import static com.example.movies.ui.activity.main.MainActivity.positionTab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.movies.R;
import com.example.movies.helper.TranslateUtils;
import com.example.movies.ui.activity.main.MainActivity;
import com.example.movies.adapter.movie.MovieAdapterByIDOfCastCrew;
import com.example.movies.data.api.APIGetData;
import com.example.movies.databinding.ActivityDetailsCharacterBinding;
import com.example.movies.listener.movie.IMovieItemByCastCrewIDClickListener;
import com.example.movies.data.model.cast.Cast;
import com.example.movies.data.model.crew.Crew;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.utils.Utils;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsCharacterActivity extends AppCompatActivity implements IMovieItemByCastCrewIDClickListener {

    Bundle bundle;
    public String type;
    public Cast castMain = new Cast();
    public Crew crewMain = new Crew();
    public ObservableField<Cast> castObservableField = new ObservableField<>();
    public ObservableField<Crew> crewObservableField = new ObservableField<>();
    public ObservableField<String> bioStringObservableField = new ObservableField<>();
    public ActivityDetailsCharacterBinding binding;
    public boolean buttonFavoritesClicked;
    public ObservableField<Boolean> buttonFavoritesClickedObservable = new ObservableField<>();
    //ADAPTER
    public MovieAdapterByIDOfCastCrew movieAdapterByIDOfCastCrew;
    public ObservableField<MovieAdapterByIDOfCastCrew> movieAdapterByIDOfCastCrewObservableField;
    //ANIMATION FOR BUTTON SCROLL TO TOP
    Animation animationGone;
    Animation animationShow;
    String typeMovieOrTCShow;
    private MutableLiveData<Boolean> isReloadFinish = new MutableLiveData<>(false);
    private Observer<Cast> castObserver;
    protected Observer<Crew> crewObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonFavoritesClickedObservable.set(false);
        //INITIALIZE
        //ANIMATIONS
        animationGone = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_hide);
        animationShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_show);
        //FIREBASE FAVORITES CAST OR CREW MANAGER
        bundle = getIntent().getBundleExtra("bundle");
        type = bundle.getString("type");
        typeMovieOrTCShow = MainActivity.getTypeByPositionTab();

        if (type.equals(Utils.TYPE_CAST)) {
            castMain = (Cast) bundle.getSerializable("cast");
            bioStringObservableField.set(castMain.getBiography());
            castObservableField.set(castMain);
            movieAdapterByIDOfCastCrew = new MovieAdapterByIDOfCastCrew(castMain.getMovie_credits().getCasts(), this);
        } else {
            crewMain = (Crew) bundle.getSerializable("crew");
            bioStringObservableField.set(crewMain.getBiography());
            crewObservableField.set(crewMain);
            movieAdapterByIDOfCastCrew = new MovieAdapterByIDOfCastCrew(crewMain.getMovie_credits().getCrews(), this);
        }
        movieAdapterByIDOfCastCrewObservableField = new ObservableField<>(movieAdapterByIDOfCastCrew);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_details_character);
        binding.setMain(this);

        bindObserver();
        bindClick();
        startShimmer();
        loadData();


    }


    public void bindClick() {
        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!v.canScrollVertically(-1)) {
                binding.buttonScrollToTop.setVisibility(View.GONE);
                binding.buttonScrollToTop.startAnimation(animationGone);
            } else {
                binding.buttonScrollToTop.startAnimation(animationShow);
                binding.buttonScrollToTop.setVisibility(View.VISIBLE);
            }
        });

        binding.detailCharacterRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.layoutNotShimmer.setVisibility(View.GONE);
                binding.layoutShimmer.setVisibility(View.VISIBLE);
                loadData();
            }
        });
    }

    public void bindObserver() {

        TranslateUtils.translateLiveDataCast = new MutableLiveData<>();
        TranslateUtils.translateLiveDataCrew = new MutableLiveData<>();

        castObserver = new Observer<Cast>() {
            @Override
            public void onChanged(Cast cast) {
                castMain = cast;
                castObservableField.set(castMain);
                isReloadFinish.postValue(true);
            }
        };

        crewObserver = new Observer<Crew>() {
            @Override
            public void onChanged(Crew crew) {
                crewMain = crew;
                crewObservableField.set(crewMain);
                isReloadFinish.postValue(true);
            }
        };

        TranslateUtils.translateLiveDataCast.observe(this, castObserver);

        TranslateUtils.translateLiveDataCrew.observe(this, crewObserver);


        /**
         * Bắt sự kiện sau khi reload xong*/
        isReloadFinish.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFinish) {
                if (isFinish) {
                    if (type.equals(Utils.TYPE_CAST)) {
                        bioStringObservableField.set(castMain.getBiography());
                        stopShimmer();
                        movieAdapterByIDOfCastCrew = new MovieAdapterByIDOfCastCrew(castMain.getMovie_credits().getCasts(), new IMovieItemByCastCrewIDClickListener() {
                            @Override
                            public void onItemClicked(MovieObject.Movie movie) {
                                //getDetailsMovieInformation(movie);
                                startActivityDetail(movie);
                            }
                        });
                    } else {
                        bioStringObservableField.set(crewMain.getBiography());
                        stopShimmer();
                        movieAdapterByIDOfCastCrew = new MovieAdapterByIDOfCastCrew(crewMain.getMovie_credits().getCrews(), new IMovieItemByCastCrewIDClickListener() {
                            @Override
                            public void onItemClicked(MovieObject.Movie movie) {
                                //getDetailsMovieInformation(movie);
                                startActivityDetail(movie);
                            }
                        });
                    }
                    binding.detailCharacterRefresh.setRefreshing(false);
                    changeBackgroundIconFavorite(type, castMain, crewMain);
                }
            }
        });
    }

    public void startShimmer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.layoutShimmer.setVisibility(View.VISIBLE);
                binding.layoutNotShimmer.setVisibility(View.GONE);
            }
        });
    }

    public void stopShimmer() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.layoutNotShimmer.setVisibility(View.VISIBLE);
                binding.layoutShimmer.setVisibility(View.GONE);
            }
        }, 500);
    }

    public void loadData() {

        if (type.equals(Utils.TYPE_CAST)) {
            new Thread(() -> APIGetData.apiGetData.getCastDetails(castMain.getId()).enqueue(new Callback<Cast>() {
                @Override
                public void onResponse(@NonNull Call<Cast> call, @NonNull Response<Cast> response) {
                    try {
                        castMain = response.body();

                        if (globalSetting.getLanguage().equals("vi")) {
                            TranslateUtils.setCast(castMain);
                            TranslateUtils.translate(castMain.getBiography(), 2);
                        } else {
                            isReloadFinish.postValue(true);
                        }

                    } catch (Exception e) {
                        Toasty.warning(getApplicationContext(), globalContextLanguage.getResources().getString(R.string.somthing_went_wrong), Toasty.LENGTH_SHORT, true).show();
                        startShimmer();
                        binding.detailCharacterRefresh.setRefreshing(true);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Cast> call, @NonNull Throwable t) {
                    Toasty.warning(getApplicationContext(), globalContextLanguage.getResources().getString(R.string.somthing_went_wrong), Toasty.LENGTH_SHORT, true).show();
                    startShimmer();
                    binding.detailCharacterRefresh.setRefreshing(true);
                }
            })).start();
        } else {

            new Thread(() -> APIGetData.apiGetData.getCrewDetails(crewMain.getId()).enqueue(new Callback<Crew>() {
                @Override
                public void onResponse(@NonNull Call<Crew> call, @NonNull Response<Crew> response) {
                    try {
                        crewMain = response.body();
                        if (globalSetting.getLanguage().equals("vi")) {
                            TranslateUtils.setCrew(crewMain);
                            TranslateUtils.translate(crewMain.getBiography(), 3);
                        } else {
                            isReloadFinish.postValue(true);
                        }
                    } catch (Exception e) {
                        Toasty.warning(getApplicationContext(), globalContextLanguage.getResources().getString(R.string.somthing_went_wrong), Toasty.LENGTH_SHORT, true).show();
                        binding.layoutNotShimmer.setVisibility(View.VISIBLE);
                        binding.layoutShimmer.setVisibility(View.GONE);
                        binding.detailCharacterRefresh.setRefreshing(false);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Crew> call, @NonNull Throwable t) {
                    Toasty.warning(getApplicationContext(), globalContextLanguage.getResources().getString(R.string.somthing_went_wrong), Toasty.LENGTH_SHORT, true).show();
                    binding.layoutNotShimmer.setVisibility(View.VISIBLE);
                    binding.layoutShimmer.setVisibility(View.GONE);
                    binding.detailCharacterRefresh.setRefreshing(false);
                }
            })).start();
        }

    }

    //BUTTON ADD TO FAVORITES
    public void onButtonAddToFavorites(View view) {
        runOnUiThread(() -> {
            String content = "";
            if (!buttonFavoritesClicked) {
                binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.favorites_ic_clicked));
                //ADD CAST OR CREW
                content = globalContextLanguage.getResources().getString(R.string.add_to_favorite_success);
                if (type.equals(Utils.TYPE_CAST)) {
                    MainActivity.addCastToFavorite(castMain);
                } else if (type.equals(Utils.TYPE_CREW)) {
                    MainActivity.addCrewToFavorite(crewMain);
                }
            } else {
                binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.favorites_ic_normal));
                //REMOVE CAST OR CREW
                content = globalContextLanguage.getResources().getString(R.string.delete_out_of_favorite_success);
                if (type.equals(Utils.TYPE_CAST)) {
                    MainActivity.removeCastOutOfFavorite(castMain);
                } else if (type.equals(Utils.TYPE_CREW)) {
                    MainActivity.removeCrewOutOfFavorite(crewMain);
                }
            }
            MainActivity.showOrHideFavoriteLayoutEmpty();
            Toasty.success(getApplicationContext(), content, Toast.LENGTH_SHORT, true).show();
            buttonFavoritesClicked = !buttonFavoritesClicked;
        });
    }


    @Override
    public void onBackPressed() {
        removeObserver();
        buttonFavoritesClicked = false;
        super.onBackPressed();
    }

    //ON CLICK ON BUTTON BACK PRESSED
    public void onIconBackPressed(View view) {
        removeObserver();
        finish();
    }

    public void removeObserver() {
        TranslateUtils.translateLiveDataCast.removeObserver(castObserver);
        TranslateUtils.translateLiveDataCrew.removeObserver(crewObserver);
    }

    @Override
    public void onItemClicked(MovieObject.Movie movie) {
        //getDetailsMovieInformation(movie);
        startActivityDetail(movie);
    }

    public void getDetailsMovieInformation(MovieObject.Movie movie) {
        APIGetData.apiGetData.getDetailsMovieInformation(typeMovieOrTCShow, movie.getId()).enqueue(new Callback<MovieObject.Movie>() {
            @Override
            public void onResponse(@NonNull Call<MovieObject.Movie> call, @NonNull Response<MovieObject.Movie> response) {
                try {
                    MovieObject.Movie item = response.body();
                    Intent intentDetailsMovie = new Intent(DetailsCharacterActivity.this, DetailsMovieActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item", item);
                    intentDetailsMovie.putExtra("bundle", bundle);
                    startActivity(intentDetailsMovie);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieObject.Movie> call, @NonNull Throwable t) {

            }
        });
    }


    public void startActivityDetail(MovieObject.Movie movie) {
        movie.getTrailers().setTrailersList(new ArrayList<>());
        movie.getSimilar().setMoviesList(new ArrayList<>());
        movie.getRecommendations().setMoviesList(new ArrayList<>());
        Intent intentDetailsMovie = new Intent(DetailsCharacterActivity.this, DetailsMovieActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", movie);
        intentDetailsMovie.putExtra("bundle", bundle);
        startActivity(intentDetailsMovie);
    }

    //ON BUTTON SCROLL TO TOP CLICKED
    public void onButtonScrollToTopClicked() {
        binding.nestedScrollView.smoothScrollTo(0, 0);
    }


    public void changeBackgroundIconFavorite(String type, Cast cast, Crew crew) {
        //CHECK MOVIE ADDED TO FAVORITES
        boolean check = false;
        if (type.equals(Utils.TYPE_CAST)) {
            check = MainActivity.isCastAddedToFavorites(cast);
        } else if (type.equals(Utils.TYPE_CREW)) {
            check = MainActivity.isCrewAddedToFavorites(crew);
        }
        buttonFavoritesClickedObservable.set(check);
        buttonFavoritesClicked = check;

        if (Boolean.TRUE.equals(buttonFavoritesClickedObservable.get())) {
            binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.favorites_ic_clicked));
        } else {
            binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.favorites_ic_normal));
        }
    }

}