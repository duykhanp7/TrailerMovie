package com.example.movies.ui.bottomsheet;

import static com.example.movies.ui.activity.main.MainActivity.keywordLiveData;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableField;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.movies.R;
import com.example.movies.adapter.movie.BottomSheetMovieByTypeAdapter;
import com.example.movies.databinding.LayoutBottomSheetExpandShowFilmsByTypeBinding;
import com.example.movies.listener.movie.IMovieItemClickListener;
import com.example.movies.listener.statusbar.IOnChangeStatusBarColor;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.utils.BindingUtils;
import com.example.movies.utils.ThemeUtils;
import com.example.movies.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetExpandShowMoviesByType extends BottomSheetDialogFragment {


    private LayoutBottomSheetExpandShowFilmsByTypeBinding binding;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Dialog dialog;
    private List<MovieObject.Movie> listMovies = new ArrayList<>();
    private int page = 1;
    private String typeMovieOrTVShow = Utils.TYPE_MOVIE;
    private String typeMovie = Utils.Action;
    private IMovieItemClickListener itemClickListener;
    private IOnChangeStatusBarColor iOnChangeStatusBarColor;
    private Context mContext;
    private ObservableField<Boolean> isSearching = new ObservableField<>();
    private BottomSheetMovieByTypeAdapter itemAdapter;

    public BottomSheetExpandShowMoviesByType(Context context,String typeMovieOrTVShowA, String typeA, List<MovieObject.Movie> listA, IMovieItemClickListener itemClickListenerA,IOnChangeStatusBarColor i,int pageA) {
        this.listMovies = listA;
        this.typeMovieOrTVShow = typeMovieOrTVShowA;
        this.typeMovie = typeA;
        this.itemClickListener = itemClickListenerA;
        this.iOnChangeStatusBarColor = i;
        this.mContext = context;
        this.page = pageA;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutBottomSheetExpandShowFilmsByTypeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setDraggable(false);

        dialog.findViewById(R.id.layoutMainShowMoviesByType).setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels + 500);
        //dialog.getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(requireContext(),android.R.color.transparent));
        dialog.getWindow().getDecorView().setBackground(null);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        itemAdapter = new BottomSheetMovieByTypeAdapter(typeMovieOrTVShow,typeMovie,listMovies,itemClickListener);
        BottomSheetMovieByTypeAdapter.pageTemp = 1;
        itemAdapter.setTypeMovieOrTVShow(typeMovieOrTVShow);
        itemAdapter.setType(typeMovie);
        itemAdapter.setPage(page);

        binding.setIsSearching(isSearching.get());

        binding.recyclerViewByType.setLayoutManager(layoutManager);
        binding.recyclerViewByType.setHasFixedSize(true);
        binding.recyclerViewByType.setAdapter(itemAdapter);

        binding.textViewMovieGenres.setText(BindingUtils.getTitleResources(getContext(),typeMovie));

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("AAA","ON TEXT SUBMIT : "+query.toString());
                closeKeyboard();
                itemAdapter.isSearching = true;
                BottomSheetMovieByTypeAdapter.pageTemp = 1;
                itemAdapter.totalPage = 0;
                itemAdapter.isRefresh = false;
                itemAdapter.getMoviesFromAPIOnSearch(1,query.toString());
                keywordLiveData.postValue(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("AAA","ON TEXT CHANGE");
                if(newText.trim().isEmpty()){
                    binding.recyclerViewByType.smoothScrollToPosition(0);
                    itemAdapter.isSearching = false;
                    BottomSheetMovieByTypeAdapter.pageTemp = 1;
                    itemAdapter.totalPage = 0;
                    itemAdapter.inflateToOldList();
                    return true;
                }
                return false;
            }
        });

        binding.searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearching.set(true);
                binding.layoutTextViewGenre.setVisibility(View.GONE);
                binding.layoutSearchView.setVisibility(View.VISIBLE);
                binding.layoutIconSearch.setVisibility(View.GONE);
            }
        });


        binding.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemAdapter.isSearching = false;
                dismiss();
            }
        });

    }


    //CLOSE KEYBOARD
    public void closeKeyboard() {
        @SuppressLint("ServiceCast") InputMethodManager inputMethodManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.searchView.getWindowToken(), 0);
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        iOnChangeStatusBarColor.changeStatusBarColor(ContextCompat.getColor(mContext,R.color.status_bar_show_bottom_sheet_movie));
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        itemAdapter.isSearching = false;
        if(ThemeUtils.getCurrentTheme() == AppCompatDelegate.MODE_NIGHT_YES){
            Log.i("AAA","NIGHT");
            iOnChangeStatusBarColor.changeStatusBarColor(ContextCompat.getColor(mContext,R.color.black));
        }
        else{
            Log.i("AAA","NIGHT NOT");
            iOnChangeStatusBarColor.changeStatusBarColor(ContextCompat.getColor(mContext,R.color.white));
        }
    }

}
