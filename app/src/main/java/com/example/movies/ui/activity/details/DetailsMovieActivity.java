package com.example.movies.ui.activity.details;

import static com.example.movies.ui.activity.main.MainActivity.chipTextFromKeyword;
import static com.example.movies.ui.activity.main.MainActivity.getTypeByPositionTab;
import static com.example.movies.ui.activity.main.MainActivity.globalContextLanguage;
import static com.example.movies.ui.activity.main.MainActivity.globalSetting;
import static com.example.movies.ui.activity.main.MainActivity.keywordLiveData;
import static com.example.movies.ui.activity.main.MainActivity.parentAdapterMovieObservableField;
import static com.example.movies.ui.activity.main.MainActivity.positionTab;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.movies.adapter.search.ShimmerSearchAdapter;
import com.example.movies.data.firebase.FirebaseObjectCommentManager;
import com.example.movies.data.model.comment.Comment;
import com.example.movies.data.model.users.UserProfile;
import com.example.movies.helper.TranslateUtils;
import com.example.movies.listener.videos.IOnActionShare;
import com.example.movies.ui.activity.main.MainActivity;
import com.example.movies.ui.activity.youtube.WatchTrailerActivity;
import com.example.movies.adapter.cast.CastAdapter;
import com.example.movies.adapter.cast.CastDetailsAdapter;
import com.example.movies.adapter.crew.CrewAdapter;
import com.example.movies.adapter.crew.CrewDetailsAdapter;
import com.example.movies.adapter.movie.MovieAdapterMovieID;
import com.example.movies.adapter.search.SearchMovieAdapterByGenres;
import com.example.movies.adapter.videos.VideosAdapter;
import com.example.movies.data.api.APIGetData;
import com.example.movies.R;
import com.example.movies.databinding.ActivityMovieDetailsBinding;
import com.example.movies.listener.cast.ICastItemClickListener;
import com.example.movies.listener.crew.ICrewItemClickListener;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.listener.movie.IRecommendationsClickListener;
import com.example.movies.listener.videos.ITrailerItemClickListener;
import com.example.movies.data.model.cast.Cast;
import com.example.movies.data.model.crew.Crew;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.data.model.videos.TrailerObject;
import com.example.movies.utils.ThemeUtils;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsMovieActivity extends AppCompatActivity implements ITrailerItemClickListener, IRecommendationsClickListener, ICastItemClickListener, ICrewItemClickListener, IMovieItemClickListener, IOnActionShare {

    public MovieObject.Movie item;
    public ObservableField<Boolean> buttonFavoritesClickedObservable;
    public Boolean expandableDetailsCastAndCrew;
    public ObservableField<Boolean> expandableDetailsCastAndCrewObservable;
    public static boolean hadSearch = false;
    @SuppressLint("StaticFieldLeak")
    public static ActivityMovieDetailsBinding binding;
    public static MutableLiveData<Boolean> isReloadFinish = new MutableLiveData<>(false);

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
    public ObservableField<CastDetailsAdapter> castDetailsAdapterObservableField;
    public ObservableField<CrewDetailsAdapter> crewDetailsAdapterObservableField;
    public static ObservableField<SearchMovieAdapterByGenres> searchMovieAdapterByGenresObservableField;

    public static ObservableField<Boolean> movieByChipGenres;
    public static ObservableField<String> chipTextClicked = new ObservableField<>();
    public static ObservableField<Boolean> buttonSearchClicked;

    //Live data
    public static MutableLiveData<Comment> mutableLiveDataAddComment;
    public static MutableLiveData<Comment> mutableLiveDataUpdateComment;
    public static MutableLiveData<Comment> mutableLiveDataDeleteComment;
    public static MutableLiveData<UserProfile> mutableLiveDataUserChanged;
    public static MutableLiveData<Boolean> mutableLiveDataBottomSheetDismiss = new MutableLiveData<>();
    //
    public static FirebaseObjectCommentManager firebaseObjectCommentManager;
    //Observer
    private Observer<MovieObject.Movie> movieObserver;
    public ActivityResultLauncher<Intent> launcherActionSend = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
            } else {
            }
        }
    });

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = getIntent().getBundleExtra("bundle");
        item = (MovieObject.Movie) bundle.getSerializable("item");
        expandableDetailsCastAndCrew = false;
        expandableDetailsCastAndCrewObservable = new ObservableField<>(expandableDetailsCastAndCrew);

        castAdapter = new CastAdapter(this);
        crewAdapter = new CrewAdapter(this);
        videosAdapter = new VideosAdapter(this, this, item, getSupportFragmentManager(), this);

        recommendationAdapter = new MovieAdapterMovieID(item.getTypeMovieOrTVShow(), item, this, Utils.recommendations);
        similarAdapter = new MovieAdapterMovieID(item.getTypeMovieOrTVShow(), item, this, Utils.similar);
        castDetailsAdapter = new CastDetailsAdapter(this);
        crewDetailsAdapter = new CrewDetailsAdapter(this);

        castAdapterObservableField = new ObservableField<>(castAdapter);
        crewAdapterObservableField = new ObservableField<>(crewAdapter);
        videosAdapterObservableField = new ObservableField<>(videosAdapter);
        recommendationsAdapterObservableField = new ObservableField<>(recommendationAdapter);
        similarAdapterObservableField = new ObservableField<>(similarAdapter);
        movieObservableField = new ObservableField<>(item);

        castDetailsAdapterObservableField = new ObservableField<>(castDetailsAdapter);
        crewDetailsAdapterObservableField = new ObservableField<>(crewDetailsAdapter);
        //moviesAdapterByGenresObservableFieldDetails = new ObservableField<>();
        searchMovieAdapterByGenresObservableField = new ObservableField<>();
        searchMovieAdapterByGenresObservableField.set(new SearchMovieAdapterByGenres(new ArrayList<>(), this));
        Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).getMoviesFromAPI(1, "");
        movieByChipGenres = new ObservableField<>(false);
        chipTextClicked = new ObservableField<>("Comedy");
        buttonSearchClicked = new ObservableField<>(false);
        buttonFavoritesClickedObservable = new ObservableField<>();

        mutableLiveDataAddComment = new MutableLiveData<>();
        mutableLiveDataUpdateComment = new MutableLiveData<>();
        mutableLiveDataDeleteComment = new MutableLiveData<>();
        mutableLiveDataUserChanged = new MutableLiveData<>();

        firebaseObjectCommentManager = new FirebaseObjectCommentManager();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        binding.setMain(this);

        //setUpUI();
        startShimmer();
        bindObserver();
        setDataLayoutShimmer();
        loadData();
        bindClick();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                firebaseObjectCommentManager.setMode(2);
            }
        }, 3000);

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.layoutNotShimmer.setVisibility(View.VISIBLE);
                        binding.layoutShimmer.setVisibility(View.GONE);
                    }
                });
            }
        }, 1000);
    }

    public void bindObserver() {

        TranslateUtils.translateLiveDataMovie = new MutableLiveData<>();
        movieObserver = new Observer<MovieObject.Movie>() {
            @Override
            public void onChanged(MovieObject.Movie movie) {
                item = movie;
                movieObservableField.set(item);
                isReloadFinish.postValue(true);
            }
        };
        TranslateUtils.translateLiveDataMovie.observe(this, movieObserver);

        /**
         * Bắt sự kiện refresh kết thúc*/
        isReloadFinish.observe(this, new Observer<Boolean>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(Boolean isFinish) {
                if (!Boolean.TRUE.equals(movieByChipGenres.get())) {
                    if (isFinish) {
                        movieObservableField.set(item);

                        bindData();

                        changeBackgroundIconFavorite(item);

                        binding.detailMovieRefresh.setRefreshing(false);
                        stopShimmer();
                    }
                } else {
                    if (isFinish) {
                        binding.recyclerViewByChipGenre.setVisibility(View.VISIBLE);
                        binding.recyclerViewByChipGenreShimmer.setVisibility(View.GONE);
                        binding.detailMovieRefresh.setRefreshing(false);
                    }
                }
            }
        });

        mutableLiveDataAddComment.observe(this, new Observer<Comment>() {
            @Override
            public void onChanged(Comment comment) {
                Objects.requireNonNull(videosAdapterObservableField.get()).addCommentToTrailer(comment);
            }
        });

        mutableLiveDataUpdateComment.observe(this, new Observer<Comment>() {
            @Override
            public void onChanged(Comment comment) {
                Objects.requireNonNull(videosAdapterObservableField.get()).updateCommentInTrailer(comment);
            }
        });

        mutableLiveDataDeleteComment.observe(this, new Observer<Comment>() {
            @Override
            public void onChanged(Comment comment) {
                Log.i("AAA", "POST DELETE : " + comment.getTextComment());
                Objects.requireNonNull(videosAdapterObservableField.get()).deleteCommentInTrailer(comment);
            }
        });


        mutableLiveDataUserChanged.observe(this, new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                if (MainActivity.userProfile != null) {
                    if (Objects.equals(MainActivity.userProfile.getUid(), userProfile.getUid())) {
                        MainActivity.userProfile.setPathImage(userProfile.getPathImage());
                    }
                }
                Objects.requireNonNull(videosAdapterObservableField.get()).updateImageProfileCommentInTrailer(userProfile);
            }
        });

        mutableLiveDataBottomSheetDismiss.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Objects.requireNonNull(videosAdapterObservableField.get()).bottomSheetComment = null;
                }
            }
        });
    }

    public void bindClick() {
        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!v.canScrollVertically(-1)) {
                Animation animationGone = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_hide);
                binding.buttonScrollToTop.startAnimation(animationGone);
                binding.buttonScrollToTop.setVisibility(View.GONE);
            } else {
                Animation animationShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_show);
                binding.buttonScrollToTop.startAnimation(animationShow);
                binding.buttonScrollToTop.setVisibility(View.VISIBLE);
            }
            Objects.requireNonNull(videosAdapterObservableField.get()).resetLayoutViewItemTrailer();
        });

        binding.recyclerViewByChipGenre.setOnScrollChangeListener((view, i, i1, i2, i3) -> {
            if (!view.canScrollVertically(-1)) {
                binding.buttonScrollToTop.setVisibility(View.GONE);
                Animation animationGone = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_hide);
                binding.buttonScrollToTop.startAnimation(animationGone);
            } else {
                Animation animationShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_show);
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
                Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).getMoviesFromAPI(1, query);
                keywordLiveData.postValue(query);
                closeKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    if (hadSearch) {
                        hadSearch = false;
                        Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).clearData();
                        Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).getMoviesFromAPI(1, "");
                        closeKeyboard();
                    }
                }
                return true;
            }
        });


        binding.detailMovieRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!Boolean.TRUE.equals(movieByChipGenres.get())) {
                    item.getTrailers().getTrailersList().clear();
                    startShimmer();
                    loadData();
                    refreshUIDetails();
                } else {
                    binding.recyclerViewByChipGenre.setVisibility(View.GONE);
                    binding.recyclerViewByChipGenreShimmer.setVisibility(View.VISIBLE);
                    Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).refreshData(1, Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).keySearch);
                }
            }
        });
    }

    //FETCH TO GET DETAILS INFORMATION OF MOVIE AND BIND TO VIEW
    @SuppressLint("NotifyDataSetChanged")
    public void loadData() {

        buttonFavoritesClickedObservable.set(false);
        expandableDetailsCastAndCrew = false;
        expandableDetailsCastAndCrewObservable.set(expandableDetailsCastAndCrew);

        String id = item.getId();
        String overView = item.getOverview();
        String typeMovieOrTV = getTypeByPositionTab();

        if (positionTab != 0 && positionTab != 1) {
            if (positionTab == 2) {
                typeMovieOrTV = Utils.TYPE_TV_SHOW;
            } else {
                typeMovieOrTV = chipTextFromKeyword.get();
            }
        }

        if (item.getTrailers().getTrailersList().size() == 0) {
            String finalTypeMovieOrTV = typeMovieOrTV;
            APIGetData.apiGetData.getDetailsMovieInformation(typeMovieOrTV, item.getId()).enqueue(new Callback<MovieObject.Movie>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<MovieObject.Movie> call, @NonNull Response<MovieObject.Movie> response) {
                    try {
                        item = response.body();
                        Objects.requireNonNull(item).setOverview(overView);
                        Objects.requireNonNull(item).setId(id);
                        item.setTypeMovieOrTVShow(finalTypeMovieOrTV);
                        movieObservableField.set(item);
                        if (globalSetting.getLanguage().equals("vi")) {
                            TranslateUtils.setMovie(item);
                            TranslateUtils.translate(item.getOverview(), 1);
                        } else {
                            isReloadFinish.postValue(true);
                        }
                    } catch (Exception e) {
                        Toasty.warning(getApplicationContext(), globalContextLanguage.getResources().getString(R.string.somthing_went_wrong), Toasty.LENGTH_SHORT, true).show();
                        binding.layoutNotShimmer.setVisibility(View.VISIBLE);
                        binding.layoutShimmer.setVisibility(View.GONE);
                        binding.detailMovieRefresh.setRefreshing(false);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieObject.Movie> call, @NonNull Throwable t) {
                }
            });

        }
        //ELSE DO THIS IF TRAILERS IS NOT NULL
        else {
            isReloadFinish.postValue(true);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    public void bindData() {

        String typeMovieOrTVShow = item.getTypeMovieOrTVShow();

        Objects.requireNonNull(item).setTypeMovieOrTVShow(typeMovieOrTVShow);

        for (MovieObject.Movie item : item.getSimilar().getMoviesList()) {
            item.setTypeMovieOrTVShow(typeMovieOrTVShow);
        }

        for (MovieObject.Movie item : item.getRecommendations().getMoviesList()) {
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

        castAdapterObservableField.set(castAdapter);
        crewAdapterObservableField.set(crewAdapter);
        videosAdapterObservableField.set(videosAdapter);
        recommendationsAdapterObservableField.set(recommendationAdapter);
        similarAdapterObservableField.set(similarAdapter);
        castDetailsAdapterObservableField.set(castDetailsAdapter);
        crewDetailsAdapterObservableField.set(crewDetailsAdapter);

        Objects.requireNonNull(castAdapterObservableField.get()).notifyDataSetChanged();
        Objects.requireNonNull(crewAdapterObservableField.get()).notifyDataSetChanged();
        Objects.requireNonNull(videosAdapterObservableField.get()).notifyDataSetChanged();
        Objects.requireNonNull(recommendationsAdapterObservableField.get()).notifyDataSetChanged();
        Objects.requireNonNull(similarAdapterObservableField.get()).notifyDataSetChanged();
        Objects.requireNonNull(castAdapterObservableField.get()).notifyDataSetChanged();
        Objects.requireNonNull(crewAdapterObservableField.get()).notifyDataSetChanged();
        Objects.requireNonNull(parentAdapterMovieObservableField.get()).adapterManager.updateMovieInAllGenres(item);

    }


    public void setUpUI() {
        if (ThemeUtils.getCurrentTheme() == AppCompatDelegate.MODE_NIGHT_NO) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            binding.expandTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            binding.layoutTopCorner.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_corner_top_white));
            binding.toolbarLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.white));
            binding.nestedScrollView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.white));
            binding.recyclerViewCastDetails.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.white));
            binding.recyclerViewCrewDetails.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.white));
            binding.recyclerViewByChipGenre.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.white));
            binding.layoutCharacterDetail.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.white));
            binding.textCharacter.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
            binding.layoutNotShimmer.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.white));
            binding.layoutCharacterMain.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.white));

            binding.textTitleMovie.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
            binding.textTitleCast.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
            binding.textTitleCrew.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
            binding.textTitleMovie.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
            binding.textTitleSimilar.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
            binding.textTitleRecommend.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
        } else if (ThemeUtils.getCurrentTheme() == AppCompatDelegate.MODE_NIGHT_YES) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            getWindow().getDecorView().setSystemUiVisibility(~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            binding.expandTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

            binding.layoutTopCorner.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_corner_top_black));
            binding.toolbarLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.black));
            binding.nestedScrollView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.black));
            binding.recyclerViewCastDetails.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.black));
            binding.recyclerViewCrewDetails.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.black));
            binding.recyclerViewByChipGenre.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.black));
            binding.layoutCharacterDetail.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.black));
            binding.textCharacter.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
            binding.layoutNotShimmer.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.black));
            binding.layoutCharacterMain.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.black));

            binding.textTitleMovie.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
            binding.textTitleCast.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
            binding.textTitleCrew.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
            binding.textTitleMovie.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
            binding.textTitleSimilar.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
            binding.textTitleRecommend.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));

        }

        setDataLayoutShimmer();
    }


    public void setDataLayoutShimmer() {
        List<MovieObject.Movie> list = new ArrayList<>();
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());
        list.add(new MovieObject.Movie());

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        ShimmerSearchAdapter shimmerAdapter = new ShimmerSearchAdapter(list);
        binding.recyclerViewByChipGenreShimmer.setHasFixedSize(true);
        binding.recyclerViewByChipGenreShimmer.setLayoutManager(manager);
        binding.recyclerViewByChipGenreShimmer.setAdapter(shimmerAdapter);
    }

    //ON BACK PRESSED EVENT
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed() {
        if (Boolean.TRUE.equals(movieByChipGenres.get())) {
            if (hadSearch) {
                Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).setKeySearch("");
                Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).clearData();
                Objects.requireNonNull(searchMovieAdapterByGenresObservableField.get()).getMoviesFromAPI(1, "");
            } else {
                binding.searchView.setQuery("", false);
                movieByChipGenres.set(false);
            }
        } else {
            isReloadFinish.postValue(false);
            TranslateUtils.translateLiveDataMovie.removeObserver(movieObserver);
            super.onBackPressed();
        }
        buttonSearchClicked.set(false);
        hadSearch = false;
    }

    //REFRESH LAYOUT WHEN CLICK ON ANOTHER MOVIE ITEM
    public void refreshUIDetails() {
        //changeBackgroundIconFavorite(movie);
        //movieObservableField.set(movie);
        runOnUiThread(() -> {
            binding.nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
            binding.nestedScrollView.smoothScrollTo(0, 0);
            binding.nestedScrollViewShimmer.smoothScrollTo(0, 0);
            binding.recyclerViewCast.smoothScrollToPosition(0);
            binding.recyclerViewRecommendations.smoothScrollToPosition(0);
            binding.recyclerViewSimilar.smoothScrollToPosition(0);
        });
    }


    //BUTTON ADD TO FAVORITES
    public void onButtonAddToFavorites(View view, MovieObject.Movie movie) {
        runOnUiThread(() -> {
            String content = "";
            if (Boolean.FALSE.equals(buttonFavoritesClickedObservable.get())) {
                binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.favorites_ic_clicked));
                MainActivity.addMovieToFavorites(movie);
                content = globalContextLanguage.getResources().getString(R.string.add_to_favorite_success);
            } else {
                binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.favorites_ic_normal));
                MainActivity.removeMovieOutOfFavorites(movie);
                content = globalContextLanguage.getResources().getString(R.string.delete_out_of_favorite_success);
                ;
            }
            MainActivity.showOrHideFavoriteLayoutEmpty();
            buttonFavoritesClickedObservable.set(Boolean.FALSE.equals(buttonFavoritesClickedObservable.get()));
            Toasty.success(getApplicationContext(), content, Toast.LENGTH_SHORT, true).show();
        });
    }


    //BUTTON SHOW DETAILS CAST AND CREW
    public void onButtonShowDetailsCastAndCrew(View view) {
        new Thread(() -> {
            expandableDetailsCastAndCrew = !expandableDetailsCastAndCrew;
            expandableDetailsCastAndCrewObservable.set(expandableDetailsCastAndCrew);
        }).start();
    }

    //ON BUTTON BACK PRESSED
    public void onIconBackPressed(View view) {
        onBackPressed();
    }

    //ON ITEM TRAILER CLICK
    //START WATCH TRAILER IF CLICKED
    @Override
    public void onTrailerItemClick(TrailerObject.Trailer trailer) {
        Intent intentWatchTrailer = new Intent(DetailsMovieActivity.this, WatchTrailerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("trailer", trailer);
        intentWatchTrailer.putExtra("bundle", bundle);
        startActivity(intentWatchTrailer);
    }

    //REFRESH LAYOUT MOVIE DETAILS ON OTHERS MOVIE CLICK
    @Override
    public void onRecommendationItemClick(MovieObject.Movie movie) {
        this.item = movie;
        movieObservableField.set(movie);
        startShimmer();
        loadData();
        refreshUIDetails();
        buttonSearchClicked.set(false);
        movieByChipGenres.set(false);
        hadSearch = false;
        binding.nestedScrollViewShimmer.smoothScrollTo(0, 0);
        binding.nestedScrollView.smoothScrollTo(0, 0);
    }


    //ON ITEM CAST CLICK
    @Override
    public void onItemCastClick(Cast cast) {
        onCastItemClickFunction(cast);
    }

    public void onCastItemClickFunction(Cast cast) {
        new Thread(() -> APIGetData.apiGetData.getCastDetails(cast.getId()).enqueue(new Callback<Cast>() {
            @Override
            public void onResponse(@NonNull Call<Cast> call, @NonNull Response<Cast> response) {
                try {
                    Cast castDetails = response.body();
                    String type = Utils.TYPE_CAST;
                    Intent intentCastClick = new Intent(DetailsMovieActivity.this, DetailsCharacterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", type);
                    bundle.putSerializable("cast", castDetails);
                    intentCastClick.putExtra("bundle", bundle);
                    startActivity(intentCastClick);
                } catch (Exception e) {
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

    public void onCrewItemClickFunction(Crew crew) {
        new Thread(() -> APIGetData.apiGetData.getCrewDetails(crew.getId()).enqueue(new Callback<Crew>() {
            @Override
            public void onResponse(@NonNull Call<Crew> call, @NonNull Response<Crew> response) {
                try {
                    Crew crewDetails = response.body();
                    String type = Utils.TYPE_CREW;
                    Intent intentCastClick = new Intent(DetailsMovieActivity.this, DetailsCharacterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", type);
                    bundle.putSerializable("crew", crewDetails);
                    intentCastClick.putExtra("bundle", bundle);
                    startActivity(intentCastClick);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Crew> call, @NonNull Throwable t) {

            }
        })).start();
    }


    //ON BUTTON SCROLL TO TOP CLICKED
    public void onButtonScrollToTopClicked() {
        binding.nestedScrollView.smoothScrollTo(0, 0);
        if (binding.recyclerViewByChipGenre.getVisibility() == View.VISIBLE) {
            binding.recyclerViewByChipGenre.smoothScrollToPosition(0);
        }
    }

    //ON BUTTON SEARCH CLICKED
    public void onButtonSearchClicked() {
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
    public void openKeyboard() {
        @SuppressLint("ServiceCast") InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(binding.searchView, InputMethodManager.SHOW_FORCED);
    }

    //CLOSE KEYBOARD
    public void closeKeyboard() {
        @SuppressLint("ServiceCast") InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.searchView.getWindowToken(), 0);
    }

    public static String getCurrentChipText() {
        return binding.textViewMovieGenre.getText().toString();
    }


    public void changeBackgroundIconFavorite(MovieObject.Movie movie) {
        //CHECK MOVIE ADDED TO FAVORITES
        boolean check = MainActivity.isMovieAddedToFavorites(movie);
        buttonFavoritesClickedObservable.set(check);
        if (Boolean.TRUE.equals(buttonFavoritesClickedObservable.get())) {
            binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.favorites_ic_clicked));
        } else {
            binding.favoritesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.favorites_ic_normal));
        }
    }


    @Override
    public void itemClicked(MovieObject.Movie item) {
        Log.i("AAA", "ITEM DETAIL ACTIVITY : " + item.getName());
        this.item = item;
        movieObservableField.set(item);
        startShimmer();
        loadData();
        refreshUIDetails();
        buttonSearchClicked.set(false);
        movieByChipGenres.set(false);
        hadSearch = false;
        binding.nestedScrollViewShimmer.smoothScrollTo(0, 0);
        binding.nestedScrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void onActionShare(Intent intent) {
        launcherActionSend.launch(intent);
    }

    public String getStringNeedTranslated(List<TrailerObject.Trailer> list) {
        String textNeedTranslate = "";
        List<String> text = new ArrayList<>();
        for (TrailerObject.Trailer item : list) {
            text.add(item.getName());
        }
        textNeedTranslate = TextUtils.join("~", text);
        return textNeedTranslate;
    }

}
