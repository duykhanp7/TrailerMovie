package com.example.movies.activity.main;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.movies.activity.details.DetailsCharacterActivity;
import com.example.movies.activity.details.DetailsMovieActivity;
import com.example.movies.activity.details.UserProfileActivity;
import com.example.movies.activity.loginorsignup.LoginOrSignUpActivity;
import com.example.movies.adapter.cast.CastAdapter;
import com.example.movies.adapter.crew.CrewAdapter;
import com.example.movies.adapter.resources.AdapterManager;
import com.example.movies.adapter.movie.FavoriteMoviesAdapter;
import com.example.movies.adapter.resources.ParentAdapter;
import com.example.movies.adapter.search.SearchMovieAdapter;
import com.example.movies.adapter.spinner.SpinnerAdapter;
import com.example.movies.api.APIGetData;
import com.example.movies.bottomsheet.BottomSheetChangeLanguage;
import com.example.movies.bottomsheet.BottomSheetChangePassword;
import com.example.movies.bottomsheet.BottomSheetChangeTheme;
import com.example.movies.databinding.ActivityMainBinding;
import com.example.movies.databinding.LayoutConfirmLogOutBinding;
import com.example.movies.firebase.FirebaseObjectFavoriteCastCrewManager;
import com.example.movies.firebase.FirebaseObjectMoviesManager;
import com.example.movies.listener.cast.ICastItemClickListener;
import com.example.movies.listener.crew.ICrewItemClickListener;
import com.example.movies.listener.statusbar.IOnChangeStatusBarColor;
import com.example.movies.listener.update.IOnRefreshData;
import com.example.movies.listener.update.IUpdateAdapter;
import com.example.movies.listener.update.IUpdateData;
import com.example.movies.listener.spinner.OnItemSpinnerClickListener;
import com.example.movies.model.anothers.IDMovieObject;
import com.example.movies.model.anothers.ItemSpinner;
import com.example.movies.model.anothers.Person;
import com.example.movies.model.cast.Cast;
import com.example.movies.model.crew.Crew;
import com.example.movies.model.movie.MovieObject;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.R;
import com.example.movies.model.users.UserProfile;
import com.example.movies.resources.MovieResources;
import com.example.movies.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//PROBLEMS
//REMOVE PERSON FAVORITES BUT RECYCLER VIEW NOT UPDATE
public class MainActivity extends AppCompatActivity implements IMovieItemClickListener, IUpdateData, OnItemSpinnerClickListener, IUpdateAdapter, NestedScrollView.OnScrollChangeListener, View.OnScrollChangeListener, SwipeRefreshLayout.OnRefreshListener, IOnRefreshData, ICastItemClickListener, ICrewItemClickListener, IOnChangeStatusBarColor {

    ActivityMainBinding activityMainBinding;
    public static MovieResources movieResources;

    //ADAPTER FOR MOVIE
    public AdapterManager adapterManagerMovie;
    public ParentAdapter parentAdapterMovie;
    public static ObservableField<ParentAdapter> parentAdapterMovieObservableField = new ObservableField<>();

    //ADAPTER FOR TV SHOW
    public AdapterManager adapterManagerTVShow;
    public ParentAdapter parentAdapterTVShow;
    public static ObservableField<ParentAdapter> parentAdapterTVShowObservableField = new ObservableField<>();

    public ObservableField<Boolean> isSearchMovie = new ObservableField<>(false);
    public ObservableField<Boolean> hasSearched = new ObservableField<>(false);
    public ObservableField<SearchMovieAdapter> searchMovieAdapterObservableField = new ObservableField<>(new SearchMovieAdapter(this));
    SpinnerAdapter spinnerAdapter;
    SpinnerAdapter spinnerAdapterForFavoriteCastOrCrew;
    SpinnerAdapter spinnerAdapterForOthers;
    //FAVORITE ADAPTER
    public static ObservableField<FavoriteMoviesAdapter> favoriteMoviesAdapterObservableField = new ObservableField<>();
    public static ObservableField<FavoriteMoviesAdapter> watchRecentlyMoviesAdapterObservableField = new ObservableField<>();
    public static ObservableField<CastAdapter> favoriteCastAdapterObservableField = new ObservableField<>();
    public static ObservableField<CrewAdapter> favoriteCrewAdapterObservableField = new ObservableField<>();
    public static int positionTab = 0;
    public static String name = "PHAM DUY KHANG";
    //FIREBASE OBJECT TO SAVE AND REMOVE MOVIE OF FAVORITES FOLDER
    public static FirebaseObjectMoviesManager firebaseObjectFavoriteMoviesManager;
    public static FirebaseObjectMoviesManager firebaseObjectWatchRecentlyMoviesManager;
    //FIREBASE FAVORITE CAST OR CREW
    private static FirebaseObjectFavoriteCastCrewManager firebaseObjectFavoriteCastCrewManager;
    //OBSERVABLE FIELD MAIN ACTIVITY
    public static ObservableField<MainActivity> mainActivityObservableField = new ObservableField<>();
    //SAU KHI NHẬN DỮ LIỆU THÌ GỬI SANG ALARMADAPTER ĐỂ CẬP NHẬT DỮ LIỆU
    //ACTIVITY RESULT : GET DATA LIST TRAILER RETURN
    public final ActivityResultLauncher<Intent> mResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
            });
    //
    FirebaseUser firebaseUser;
    //USER PROFILE CURRENT
    private boolean isFirstLoadUserProfile = false;
    private boolean isFirstInitializedUserProfile = false;
    private UserProfile userProfile;
    //ALERT DIALOG LOG OUT
    private AlertDialog alertDialogConfirmLogOut;


    public static ObservableField<Integer> observableFieldPositionFavoritesType = new ObservableField<>(0);
    public static ObservableField<Integer> observableFieldPositionOtherType = new ObservableField<>(0);

    private ActivityResultLauncher<Intent> launchUserDetailInformation = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Bundle bundle = result.getData().getBundleExtra("bundle");
                userProfile = (UserProfile) bundle.getSerializable("user");
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //applyLanguages();
        //ADAPTER MANAGER
        adapterManagerMovie = new AdapterManager(Utils.TYPE_MOVIE, this, this, this);
        adapterManagerTVShow = new AdapterManager(Utils.TYPE_TV_SHOW, this, this, this);

        //MOVIE RESOURCES
        movieResources = new MovieResources(this);
        //FIREBASE OBJECT
        firebaseObjectFavoriteMoviesManager = new FirebaseObjectMoviesManager(Utils.FIREBASE_FAVORITE_MOVIES_FOLDER);
        firebaseObjectWatchRecentlyMoviesManager = new FirebaseObjectMoviesManager(Utils.FIREBASE_WATCH_RECENTLY_MOVIES_FOLDER);
        firebaseObjectFavoriteCastCrewManager = new FirebaseObjectFavoriteCastCrewManager();
        //FAVORITES ADAPTER
        favoriteMoviesAdapterObservableField.set(new FavoriteMoviesAdapter(firebaseObjectFavoriteMoviesManager.getMovieFavoritesFromAPI(), firebaseObjectFavoriteMoviesManager.getAllIDMovieFavoritesFromFirebase(), this));
        watchRecentlyMoviesAdapterObservableField.set(new FavoriteMoviesAdapter(firebaseObjectWatchRecentlyMoviesManager.getMovieFavoritesFromAPI(), firebaseObjectWatchRecentlyMoviesManager.getAllIDMovieFavoritesFromFirebase(), this));
        favoriteCastAdapterObservableField.set(new CastAdapter(this));
        favoriteCrewAdapterObservableField.set(new CrewAdapter(this));
        //PARENT ADAPTER MOVIE
        parentAdapterMovie = new ParentAdapter(adapterManagerMovie,getSupportFragmentManager() ,this,this);
        parentAdapterTVShow = new ParentAdapter(adapterManagerTVShow,getSupportFragmentManager(), this,this);
        //PARENT ADAPTER TV SHOW
        parentAdapterMovieObservableField.set(parentAdapterMovie);
        parentAdapterTVShowObservableField.set(parentAdapterTVShow);

        Thread a, b, c, d, e, f;
        a = new Thread(() -> movieResources.getMoviesAPIAtPageIndex(Utils.TYPE_MOVIE, Utils.NowPlaying, 1));
        b = new Thread(() -> movieResources.getMoviesAPIAtPageIndex(Utils.TYPE_MOVIE, Utils.Popular, 1));
        c = new Thread(() -> movieResources.getMoviesAPIAtPageIndex(Utils.TYPE_MOVIE, Utils.TopRated, 1));
        d = new Thread(() -> movieResources.getMoviesAPIAtPageIndex(Utils.TYPE_MOVIE, Utils.UpComing, 1));

        e = new Thread(() -> movieResources.getMoviesAPIAtPageIndex(Utils.TYPE_TV_SHOW, Utils.AiringToday, 1));
        f = new Thread(() -> movieResources.getMoviesAPIAtPageIndex(Utils.TYPE_TV_SHOW, Utils.OnTheAir, 1));

        a.start();
        b.start();
        c.start();
        d.start();
        e.start();
        f.start();
        //BINDING
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setMain(this);
        //getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        //ADD SNAP HELPER TO RECYCLER VIEW
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(activityMainBinding.recyclerViewListMovies);

        activityMainBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch(query);
                activityMainBinding.searchView.clearFocus();
                closeKeyboard();
                hideAllViewsAndJustShowRecyclerViewSearch(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        //NESTED SCROLL CHANGE LISTENER
        activityMainBinding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) this);


        //SCROLL ON RECYCLER VIEW SEARCH CHANGED
        activityMainBinding.recyclerViewSearchMovie.setOnScrollChangeListener(this);
        activityMainBinding.layoutIncludeTVShow.nestedScrollViewTVShow.setOnScrollChangeListener((View.OnScrollChangeListener) this);
        activityMainBinding.swipeRefreshLayout.setOnRefreshListener(this);
        activityMainBinding.layoutFavoriteBinding.recyclerViewFavorite.setOnScrollChangeListener(this);

        //Objects.requireNonNull(favoriteMoviesAdapterObservableField.get()).getMovieFavoritesFromAPI();
        mainActivityObservableField.set(this);

        //INITIALIZE SPINNER
        initializedAdapterForSpinner();
        initializedAdapterForSpinnerFavoriteMovieCastOrCrew();
        initializedAdapterForSpinnerOthers();
        //GET CAST AND CREW
        Map<String, Person> idCastObjects = firebaseObjectFavoriteCastCrewManager.getAllIDCastFavoritesFromFirebase();
        Map<String, Person> idCrewObjects = firebaseObjectFavoriteCastCrewManager.getAllIDCrewFavoritesFromFirebase();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Objects.requireNonNull(favoriteCastAdapterObservableField.get()).addMapIDCast(idCastObjects);
                Objects.requireNonNull(favoriteCrewAdapterObservableField.get()).addMapIDCrew(idCrewObjects);
            }
        }, 2000);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Objects.requireNonNull(favoriteCastAdapterObservableField.get()).getCastsFromAPI();
                Objects.requireNonNull(favoriteCrewAdapterObservableField.get()).getCrewsFromAPI();
                activityMainBinding.swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);

        //ON BUTTON LOG OUT CLICK
        activityMainBinding.layoutIncludeSettings.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogConfirmLogOut();
            }
        });

        //ON BUTTON PASSWORD CLICK
        activityMainBinding.layoutIncludeSettings.passwordManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String provider = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getProviderData().get(1).getProviderId();
                if (provider.equals(Utils.GOOGLE_PROVIDER)) {
                    Toast.makeText(getApplicationContext(), R.string.acc_auth_by_gg_no_change_pass, Toast.LENGTH_LONG).show();
                } else if (provider.equals(Utils.FACEBOOK_PROVIDER)) {
                    Toast.makeText(getApplicationContext(), R.string.acc_auth_by_fb_not_change_pass, Toast.LENGTH_LONG).show();
                } else if (provider.equals(Utils.PASSWORD_PROVIDER)) {
                    BottomSheetChangePassword bottomSheetChangePassword = new BottomSheetChangePassword(getApplicationContext());
                    bottomSheetChangePassword.show(getSupportFragmentManager(), "AAA");
                    bottomSheetChangePassword.setStyle(DialogFragment.STYLE_NORMAL, R.style.changeBackgroundOfBottomSheetChangePassword);
                }

            }
        });


        //ON BUTTON CHANGE THEMES
        activityMainBinding.layoutIncludeSettings.themesManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetChangeTheme bottomSheetChangeTheme = new BottomSheetChangeTheme();
                bottomSheetChangeTheme.show(getSupportFragmentManager(), "AAA");
                bottomSheetChangeTheme.setStyle(DialogFragment.STYLE_NORMAL, R.style.changeBackgroundOfBottomSheetChangePassword);
            }
        });

        //ON BUTTON INFORMATION CLICKED
        activityMainBinding.layoutIncludeSettings.buttonInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent userDetailInformation = new Intent(MainActivity.this, UserProfileActivity.class);
                bundle.putSerializable("user", userProfile);
                userDetailInformation.putExtra("bundle", bundle);
                launchUserDetailInformation.launch(userDetailInformation);
            }
        });

        activityMainBinding.layoutIncludeSettings.changeLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetChangeLanguage bottomSheetChangeLanguage = new BottomSheetChangeLanguage(getApplicationContext());
                bottomSheetChangeLanguage.show(getSupportFragmentManager(), "AAA");
                bottomSheetChangeLanguage.setStyle(DialogFragment.STYLE_NORMAL, R.style.changeBackgroundOfBottomSheetChangePassword);
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        initializedInformationForAccountAuthByGoogleAndFacebook();
        getUserProfileCurrent();
    }

    private void hideAllViewsAndJustShowRecyclerViewSearch(boolean visibility) {
        activityMainBinding.mainLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
        activityMainBinding.layoutFavorite.setVisibility(visibility ? View.GONE : View.VISIBLE);
        activityMainBinding.layoutSetting.setVisibility(visibility ? View.GONE : View.VISIBLE);
        activityMainBinding.layoutTvShow.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    private void resetViewsToNormalBeforeSearch() {
        switch (positionTab) {
            case 0:
                showTabHome();
                break;
            case 1:
                showTabTVShow();
                break;
            case 2:
                showTabFavorite();
                break;
            case 3:
                showTabSetting();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void onSearch(String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String typeMovieOrTVShow = positionTab == 0 ? Utils.TYPE_MOVIE : Utils.TYPE_TV_SHOW;
                Objects.requireNonNull(searchMovieAdapterObservableField.get()).setTypeMovieOrTVShow(typeMovieOrTVShow);
                String keyword = query.trim();
                hasSearched.set(true);
                Objects.requireNonNull(searchMovieAdapterObservableField.get()).setKeyword(keyword);
                APIGetData.apiGetData.getMovieByKeyword(typeMovieOrTVShow, keyword, "1").enqueue(new Callback<MovieObject>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieObject> call, @NonNull Response<MovieObject> response) {
                        try {
                            Objects.requireNonNull(searchMovieAdapterObservableField.get()).setMoviesSearch(Objects.requireNonNull(response.body()).getMoviesList());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieObject> call, @NonNull Throwable t) {

                    }
                });
            }
        }).start();
    }

    //OVERRIDE FUNCTION FOR ITEM CLICKED
    @Override
    public void itemClicked(MovieObject.Movie item) {
        Intent intent = new Intent(MainActivity.this, DetailsMovieActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }


    //UPDATE ADAPTER WHEN COMPLETED GET DATA FROM API
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void update(List<MovieObject.Movie> movies, String typeMovieOrTVShow, String type) {
        if (typeMovieOrTVShow.equals(Utils.TYPE_MOVIE)) {
            for (MovieObject.Movie movie : movies) {
                movie.setTypeMovieOrTVShow(Utils.TYPE_MOVIE);
            }
            Objects.requireNonNull(Objects.requireNonNull(adapterManagerMovie.mapListMoviesObservableFieldAdapter.get(type)).get()).addMovieList(movies);
        } else if (typeMovieOrTVShow.equals(Utils.TYPE_TV_SHOW)) {
            for (MovieObject.Movie movie : movies) {
                movie.setTypeMovieOrTVShow(Utils.TYPE_TV_SHOW);
            }
            Objects.requireNonNull(Objects.requireNonNull(adapterManagerTVShow.mapListMoviesObservableFieldAdapter.get(type)).get()).addMovieList(movies);
        }
    }

    @Override
    public void updateTitle() {
        if (positionTab == 0) {
            parentAdapterMovie.updateTitleGenre();
        } else if (positionTab == 1) {
            parentAdapterTVShow.updateTitleGenre();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onButtonSearchClickedMain() {
        isSearchMovie.set(true);
        changeWeightSumLayoutToNormalNotSearch(false);
        openKeyboard();
    }

    public void logOut() {
        if (FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getProviderId().equals("facebook.com")) {

            if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null) {
                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        LoginManager.getInstance().logOut();
                    }
                }).executeAsync();
            } else {
            }
        }
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(), R.string.log_out_success, Toast.LENGTH_LONG).show();
        startActivityLogin();
    }


    public void showAlertDialogConfirmLogOut() {

        LayoutConfirmLogOutBinding binding = LayoutConfirmLogOutBinding.inflate(getLayoutInflater());
        binding.buttonSureLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
                alertDialogConfirmLogOut.dismiss();
            }
        });
        binding.buttonCancelLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogConfirmLogOut.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(binding.getRoot());
        alertDialogConfirmLogOut = builder.create();
        alertDialogConfirmLogOut.show();
    }

    @Override
    public void onBackPressed() {
        hasSearched.set(false);
        if (Boolean.TRUE.equals(isSearchMovie.get())) {
            changeWeightSumLayoutToNormalNotSearch(true);
            closeKeyboard();
            resetViewsToNormalBeforeSearch();
        } else {
            super.onBackPressed();
        }
        isSearchMovie.set(false);
    }

    //OPEN KEYBOARD
    public void openKeyboard() {
        activityMainBinding.searchView.requestFocus();
        @SuppressLint("ServiceCast") InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(activityMainBinding.searchView, InputMethodManager.SHOW_FORCED);
    }

    //CLOSE KEYBOARD
    public void closeKeyboard() {
        @SuppressLint("ServiceCast") InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activityMainBinding.searchView.getWindowToken(), 0);
    }

    //ON BUTTON SCROLL TO TOP CLICKED
    public void onButtonScrollToTopClicked() {
        if (positionTab == 0) {
            if (Boolean.TRUE.equals(hasSearched.get())) {
                activityMainBinding.recyclerViewSearchMovie.post(new Runnable() {
                    @Override
                    public void run() {
                        activityMainBinding.recyclerViewSearchMovie.smoothScrollToPosition(0);
                    }
                });
            } else {
                activityMainBinding.scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        activityMainBinding.scrollView.smoothScrollTo(0, 0);
                    }
                });
            }
        } else if (positionTab == 1) {
            if (Boolean.TRUE.equals(hasSearched.get())) {
                activityMainBinding.recyclerViewSearchMovie.post(new Runnable() {
                    @Override
                    public void run() {
                        activityMainBinding.recyclerViewSearchMovie.smoothScrollToPosition(0);
                    }
                });
            } else {
                activityMainBinding.layoutIncludeTVShow.recyclerViewTVShow.post(new Runnable() {
                    @Override
                    public void run() {
                        activityMainBinding.layoutIncludeTVShow.nestedScrollViewTVShow.smoothScrollTo(0, 0);
                    }
                });
            }
        } else if (positionTab == 2) {
            activityMainBinding.layoutFavoriteBinding.recyclerViewFavorite.post(new Runnable() {
                @Override
                public void run() {
                    activityMainBinding.layoutFavoriteBinding.recyclerViewFavorite.smoothScrollToPosition(0);
                }
            });
        }
    }


    public void changeWeightSumLayoutToNormalNotSearch(boolean state) {
        LinearLayout.LayoutParams paramsFirst;
        LinearLayout.LayoutParams paramsSecond;
        if (state) {
            paramsFirst = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT, 2.5F);
            paramsSecond = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT, 0.5F);
            activityMainBinding.spinnerMenus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            activityMainBinding.spinnerMenus.setPadding(180, 0, 100, 0);
        } else {
            paramsFirst = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT, 1);
            paramsSecond = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT, 2);
            activityMainBinding.spinnerMenus.setPadding(0, 0, 0, 0);
            activityMainBinding.spinnerMenus.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
        activityMainBinding.layoutToolbarFirst.setLayoutParams(paramsFirst);
        activityMainBinding.layoutToolbarSecond.setLayoutParams(paramsSecond);
    }


    //INITIALIZED ADAPTER FOR SPINNER
    public void initializedAdapterForSpinner() {
        List<ItemSpinner> itemMenus = new ArrayList<>();
        itemMenus.add(new ItemSpinner(getString(R.string.home), Utils.HOME_ID));
        itemMenus.add(new ItemSpinner(getString(R.string.tv_shows), Utils.TV_SHOWS_ID));
        itemMenus.add(new ItemSpinner(getString(R.string.favorite), Utils.FAVORITE_ID));
        itemMenus.add(new ItemSpinner(getString(R.string.others), Utils.OTHERS_ID));
        itemMenus.add(new ItemSpinner(getString(R.string.setting), Utils.SETTING_ID));
        spinnerAdapter = new SpinnerAdapter(getApplicationContext(), R.layout.item_spinner, itemMenus);
        activityMainBinding.spinnerMenus.setAdapter(spinnerAdapter);
        activityMainBinding.spinnerMenus.setPadding(180, 0, 100, 0);
        activityMainBinding.spinnerMenus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                positionTab = position;
                ItemSpinner itemSpinner = (ItemSpinner) adapterView.getItemAtPosition(position);
                onItemSpinnerClicked(itemSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    //INITIALIZED SPINNER FOR FAVORITES MOVIE, CAST OR CREW
    public void initializedAdapterForSpinnerFavoriteMovieCastOrCrew() {
        List<ItemSpinner> itemMenus = new ArrayList<>();
        itemMenus.add(new ItemSpinner(getString(R.string.movies), Utils.FAVORITE_MOVIES));
        itemMenus.add(new ItemSpinner(getString(R.string.casts), Utils.FAVORITE_CASTS));
        itemMenus.add(new ItemSpinner(getString(R.string.crews), Utils.FAVORITE_CREWS));
        spinnerAdapterForFavoriteCastOrCrew = new SpinnerAdapter(getApplicationContext(), R.layout.item_spinner, itemMenus);
        activityMainBinding.layoutFavoriteBinding.spinnerMovieCastCrew.setAdapter(spinnerAdapterForFavoriteCastOrCrew);
        activityMainBinding.layoutFavoriteBinding.spinnerMovieCastCrew.setPadding(180, 0, 100, 0);
        activityMainBinding.layoutFavoriteBinding.spinnerMovieCastCrew.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ItemSpinner itemSpinner = (ItemSpinner) adapterView.getItemAtPosition(i);
                onItemSpinnerClicked(itemSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    //INITIALIZED SPINNER FOR FAVORITES MOVIE, CAST OR CREW
    public void initializedAdapterForSpinnerOthers() {
        List<ItemSpinner> itemMenus = new ArrayList<>();
        itemMenus.add(new ItemSpinner(getString(R.string.recently), Utils.MOVIES_WATCH_RECENTLY));
        itemMenus.add(new ItemSpinner(getString(R.string.key_word_search), Utils.KEYWORDS_SEARCH));
        spinnerAdapterForOthers = new SpinnerAdapter(getApplicationContext(), R.layout.item_spinner, itemMenus);
        activityMainBinding.layoutIncludeOthers.spinnerOthers.setAdapter(spinnerAdapterForOthers);
        activityMainBinding.layoutIncludeOthers.spinnerOthers.setPadding(180, 0, 100, 0);
        activityMainBinding.layoutIncludeOthers.spinnerOthers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ItemSpinner itemSpinner = (ItemSpinner) adapterView.getItemAtPosition(i);
                onItemSpinnerClicked(itemSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onItemSpinnerClicked(ItemSpinner itemSpinner) {
        switch (itemSpinner.getId()) {
            case Utils.HOME_ID:
                showTabHome();
                break;
            case Utils.TV_SHOWS_ID:
                showTabTVShow();
                break;
            case Utils.FAVORITE_ID:
                showTabFavorite();
                break;
            case Utils.SETTING_ID:
                showTabSetting();
                break;
            case Utils.OTHERS_ID:
                showTabOthers();
                break;
            case Utils.FAVORITE_MOVIES:
                observableFieldPositionFavoritesType.set(0);
                break;
            case Utils.FAVORITE_CASTS:
                observableFieldPositionFavoritesType.set(1);
                break;
            case Utils.FAVORITE_CREWS:
                observableFieldPositionFavoritesType.set(2);
                break;
            case Utils.MOVIES_WATCH_RECENTLY:
                observableFieldPositionOtherType.set(0);
                break;
            case Utils.KEYWORDS_SEARCH:
                observableFieldPositionOtherType.set(1);
                break;
            default:
                activityMainBinding.swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }


    private void showTabHome() {
        activityMainBinding.mainLayout.setVisibility(View.VISIBLE);
        activityMainBinding.iconSearchMovie.setVisibility(View.VISIBLE);
        activityMainBinding.layoutFavorite.setVisibility(View.GONE);
        activityMainBinding.layoutSetting.setVisibility(View.GONE);
        activityMainBinding.layoutTvShow.setVisibility(View.GONE);
        activityMainBinding.layoutOthers.setVisibility(View.GONE);

        if (activityMainBinding.scrollView.canScrollVertically(-1)) {
            activityMainBinding.buttonScrollToTopMain.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_show));
        }

    }

    private void showTabTVShow() {
        activityMainBinding.layoutTvShow.setVisibility(View.VISIBLE);
        activityMainBinding.iconSearchMovie.setVisibility(View.VISIBLE);
        activityMainBinding.mainLayout.setVisibility(View.GONE);
        activityMainBinding.layoutFavorite.setVisibility(View.GONE);
        activityMainBinding.layoutSetting.setVisibility(View.GONE);
        activityMainBinding.layoutOthers.setVisibility(View.GONE);

        if (activityMainBinding.layoutIncludeTVShow.nestedScrollViewTVShow.canScrollVertically(-1)) {
            activityMainBinding.buttonScrollToTopMain.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_show));
        }

    }


    private void showTabFavorite() {
        activityMainBinding.layoutFavorite.setVisibility(View.VISIBLE);
        activityMainBinding.iconSearchMovie.setVisibility(View.GONE);
        activityMainBinding.mainLayout.setVisibility(View.GONE);
        activityMainBinding.layoutSetting.setVisibility(View.GONE);
        activityMainBinding.layoutTvShow.setVisibility(View.GONE);
        activityMainBinding.layoutOthers.setVisibility(View.GONE);

        if (activityMainBinding.layoutFavoriteBinding.recyclerViewFavorite.canScrollVertically(-1)) {
            activityMainBinding.buttonScrollToTopMain.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_show));
        }

    }

    private void showTabSetting() {
        activityMainBinding.layoutSetting.setVisibility(View.VISIBLE);
        activityMainBinding.iconSearchMovie.setVisibility(View.GONE);
        activityMainBinding.layoutFavorite.setVisibility(View.GONE);
        activityMainBinding.mainLayout.setVisibility(View.GONE);
        activityMainBinding.layoutTvShow.setVisibility(View.GONE);
        activityMainBinding.layoutOthers.setVisibility(View.GONE);
        activityMainBinding.buttonScrollToTopMain.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_hide));
    }

    private void showTabOthers() {
        activityMainBinding.layoutSetting.setVisibility(View.GONE);
        activityMainBinding.iconSearchMovie.setVisibility(View.GONE);
        activityMainBinding.layoutFavorite.setVisibility(View.GONE);
        activityMainBinding.mainLayout.setVisibility(View.GONE);
        activityMainBinding.layoutTvShow.setVisibility(View.GONE);
        activityMainBinding.layoutOthers.setVisibility(View.VISIBLE);
        if (activityMainBinding.layoutIncludeOthers.recyclerWatchRecentlyMovies.canScrollVertically(-1)) {
            activityMainBinding.buttonScrollToTopMain.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_show));
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void updateAdapter(String type) {
        if (type.equals(Utils.TYPE_MOVIE)) {
            Objects.requireNonNull(parentAdapterMovieObservableField.get()).notifyDataSetChanged();
        } else if (type.equals(Utils.TYPE_TV_SHOW)) {
            Objects.requireNonNull(parentAdapterTVShowObservableField.get()).notifyDataSetChanged();
        }
    }

    //NESTED SCROLL
    @Override
    public void onScrollChange(NestedScrollView view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        viewScrollListener(view);
    }

    //ON RECYCLER VIEW SCROLL
    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
        viewScrollListener(view);
    }


    private void viewScrollListener(View view) {
        if (!view.canScrollVertically(-1)) {
            activityMainBinding.buttonScrollToTopMain.post(new Runnable() {
                @Override
                public void run() {
                    activityMainBinding.buttonScrollToTopMain.setVisibility(View.GONE);
                    Animation animationGone = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_hide);
                    activityMainBinding.buttonScrollToTopMain.startAnimation(animationGone);
                }
            });
        } else {
            activityMainBinding.buttonScrollToTopMain.post(new Runnable() {
                @Override
                public void run() {
                    activityMainBinding.buttonScrollToTopMain.setVisibility(View.VISIBLE);
                    Animation animationShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_floating_show);
                    activityMainBinding.buttonScrollToTopMain.startAnimation(animationShow);
                }
            });
        }
    }

    //ADD TO FAVORITE
    public static void addMovieToFavorites(MovieObject.Movie movie) {
        String type = movie.getTypeMovieOrTVShow();
        Objects.requireNonNull(favoriteMoviesAdapterObservableField.get()).addMovieFavoriteList(movie, type);
        firebaseObjectFavoriteMoviesManager.addNewMovieIDToFavoriteFolder(movie, type);
    }

    //ADD TO WATCH RECENTLY
    public static void addMovieToWatchRecently(MovieObject.Movie movie) {
        if(!isMovieAddedToWatchRecently(movie)){
            String type = movie.getTypeMovieOrTVShow();
            Objects.requireNonNull(watchRecentlyMoviesAdapterObservableField.get()).addMovieFavoriteList(movie, type);
            firebaseObjectWatchRecentlyMoviesManager.addNewMovieIDToFavoriteFolder(movie, type);
        }
    }

    //REMOVE OUT OF FAVORITE
    public static void removeMovieOutOfFavorites(MovieObject.Movie movie) {
        Objects.requireNonNull(favoriteMoviesAdapterObservableField.get()).removeOutOfFavoriteList(movie);
        firebaseObjectFavoriteMoviesManager.removeMovieIDOutOfFavoriteFolder(movie);
    }

    //ADD CAST TO FAVORITE
    public static void addCastToFavorite(Cast cast) {
        Objects.requireNonNull(favoriteCastAdapterObservableField.get()).addCast(cast);
        firebaseObjectFavoriteCastCrewManager.addCastToFavoriteFolder(cast);
    }

    //ADD CREW TO FAVORITE
    public static void addCrewToFavorite(Crew crew) {
        Objects.requireNonNull(favoriteCrewAdapterObservableField.get()).addCrew(crew);
        firebaseObjectFavoriteCastCrewManager.addCrewToFavoriteFolder(crew);
    }

    //REMOVE CAST OUT OF FAVORITE
    public static void removeCastOutOfFavorite(Cast cast) {
        Objects.requireNonNull(favoriteCastAdapterObservableField.get()).removeCast(cast);
        firebaseObjectFavoriteCastCrewManager.removeCastOutOfFavoriteFolder(cast);
    }

    //REMOVE CREW OUT OF FAVORITE
    public static void removeCrewOutOfFavorite(Crew crew) {
        Objects.requireNonNull(favoriteCrewAdapterObservableField.get()).removeCrew(crew);
        firebaseObjectFavoriteCastCrewManager.removeCrewOutOfFavoriteFolder(crew);
    }

    //CHECK MOVIE ADDED IN FAVORITES
    public static boolean isMovieAddedToFavorites(MovieObject.Movie movie) {
        return Objects.requireNonNull(favoriteMoviesAdapterObservableField.get()).containsMovie(movie);
    }

    //CHECK MOVIE IS ADDED TO WATCH RECENTLY
    public static boolean isMovieAddedToWatchRecently(MovieObject.Movie movie) {
        return Objects.requireNonNull(watchRecentlyMoviesAdapterObservableField.get()).containsMovie(movie);
    }


    public static boolean isCastAddedToFavorites(Cast cast) {
        return Objects.requireNonNull(favoriteCastAdapterObservableField.get()).containsCast(cast);
    }

    public static boolean isCrewAddedToFavorites(Crew crew) {
        return Objects.requireNonNull(favoriteCrewAdapterObservableField.get()).containsCrew(crew);
    }

    public void startActivityLogin() {
        Intent intentLoginOrSignUpActivity = new Intent(MainActivity.this, LoginOrSignUpActivity.class);
        startActivity(intentLoginOrSignUpActivity);
    }

    //ON REFRESH LAYOUT;
    @Override
    public void onRefresh() {

        switch (positionTab) {
            case 0:
                //REFRESH LAYOUT HOME
                if (Boolean.TRUE.equals(hasSearched.get())) {
                    String keyWord = activityMainBinding.searchView.getQuery().toString();
                    onSearch(keyWord);
                    activityMainBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    Objects.requireNonNull(parentAdapterMovieObservableField.get()).onRefreshData();
                }
                break;
            case 1:
                //REFRESH LAYOUT TV SHOWS
                if (Boolean.TRUE.equals(hasSearched.get())) {
                    String keyWord = activityMainBinding.searchView.getQuery().toString();
                    onSearch(keyWord);
                    activityMainBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    Objects.requireNonNull(parentAdapterTVShowObservableField.get()).onRefreshData();
                }
                break;
            case 2:
                //REFRESH LAYOUT FAVORITES
                firebaseObjectFavoriteMoviesManager.firstLoadIDMovie = true;
                firebaseObjectFavoriteCastCrewManager.firstLoadIDCast = true;
                firebaseObjectFavoriteCastCrewManager.firstLoadIDCrew = true;
                Map<String, IDMovieObject> idMovieObjects = firebaseObjectFavoriteMoviesManager.getAllIDMovieFavoritesFromFirebase();
                Map<String, Person> idCastObjects = firebaseObjectFavoriteCastCrewManager.getAllIDCastFavoritesFromFirebase();
                Map<String, Person> idCrewObjects = firebaseObjectFavoriteCastCrewManager.getAllIDCrewFavoritesFromFirebase();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Objects.requireNonNull(favoriteMoviesAdapterObservableField.get()).addMapIDMovie(idMovieObjects);
                        Objects.requireNonNull(watchRecentlyMoviesAdapterObservableField.get()).addMapIDMovie(idMovieObjects);
                        Objects.requireNonNull(favoriteCastAdapterObservableField.get()).addMapIDCast(idCastObjects);
                        Objects.requireNonNull(favoriteCrewAdapterObservableField.get()).addMapIDCrew(idCrewObjects);
                    }
                }, 2000);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Objects.requireNonNull(favoriteMoviesAdapterObservableField.get()).getMovieFavoritesFromAPI();
                        Objects.requireNonNull(watchRecentlyMoviesAdapterObservableField.get()).getMovieFavoritesFromAPI();
                        Objects.requireNonNull(favoriteCastAdapterObservableField.get()).getCastsFromAPI();
                        Objects.requireNonNull(favoriteCrewAdapterObservableField.get()).getCrewsFromAPI();
                        activityMainBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
                //FAVORITE MOVIES, CASTS, CREWS
                break;
            case 3:
                //REFRESH LAYOUT SETTINGS
                activityMainBinding.swipeRefreshLayout.setRefreshing(false);
                break;
            default:
                activityMainBinding.swipeRefreshLayout.setRefreshing(false);
                break;
        }

    }

    @Override
    public void onStopRefreshData() {
        switch (positionTab) {
            case 0:
                //STOP REFRESH LAYOUT HOME
                Objects.requireNonNull(parentAdapterMovieObservableField.get()).stopRefreshData();
                activityMainBinding.swipeRefreshLayout.setRefreshing(false);
                break;
            case 1:
                //STOP REFRESH LAYOUT TV SHOWS
                Objects.requireNonNull(parentAdapterTVShowObservableField.get()).stopRefreshData();
                activityMainBinding.swipeRefreshLayout.setRefreshing(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemCastClick(Cast cast) {
        new Thread(() -> APIGetData.apiGetData.getCastDetails(cast.getId()).enqueue(new Callback<Cast>() {
            @Override
            public void onResponse(@NonNull Call<Cast> call, @NonNull Response<Cast> response) {
                try {
                    Cast castDetails = response.body();
                    String type = Utils.TYPE_CAST;
                    Intent intentCastClick = new Intent(MainActivity.this, DetailsCharacterActivity.class);
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

    @Override
    public void onItemCrewClick(Crew crew) {
        new Thread(() -> APIGetData.apiGetData.getCrewDetails(crew.getId()).enqueue(new Callback<Crew>() {
            @Override
            public void onResponse(@NonNull Call<Crew> call, @NonNull Response<Crew> response) {
                try {
                    Crew crewDetails = response.body();
                    String type = Utils.TYPE_CREW;
                    Intent intentCastClick = new Intent(MainActivity.this, DetailsCharacterActivity.class);
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

    public void getUserProfileCurrent() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    if (!isFirstLoadUserProfile) {
                        userProfile = snapshot.getValue(UserProfile.class);
                        isFirstLoadUserProfile = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //INITIALIZED INFORMATION FOR ACCOUNT AUTH BY FACEBOOK AND GOOGLE
    public void initializedInformationForAccountAuthByGoogleAndFacebook() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isFirstInitializedUserProfile) {
                    if (!snapshot.hasChildren()) {
                        UserProfile userProfile = new UserProfile(getFirstName(Objects.requireNonNull(firebaseUser.getDisplayName())), getLastName(firebaseUser.getDisplayName()), "", "", "", "");
                        reference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER)
                                .child(firebaseUser.getUid()).setValue(userProfile)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public String getFirstName(String text) {
        String[] textArr = text.split(" ");
        return textArr[0];
    }

    public String getLastName(String text) {
        String[] textArr = text.split(" ");
        String txt = "";
        for (int i = 1; i < textArr.length; i++) {
            txt = (" " + textArr[i]).concat(txt);
        }
        return txt;
    }

    public void applyLanguages() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("lang", Context.MODE_PRIVATE);
        String lang = sharedPreferences.getString("lang", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getApplicationContext().createConfigurationContext(configuration);
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(updateBaseContextLocale(newBase));
//    }


    @SuppressLint("ObsoleteSdkInt")
    private Context updateBaseContextLocale(Context context) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("lang", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("lang", "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale);
        }

        return updateResourcesLocaleLegacy(context, locale);
    }

    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    @Override
    public void changeStatusBarColor(int color) {
        getWindow().setStatusBarColor(color);
    }
}
