package com.example.movies.adapter.resources;

import static com.example.movies.activity.main.MainActivity.movieResources;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.adapter.movie.MoviesAdapterByGenres;
import com.example.movies.bottomsheet.BottomSheetExpandShowMoviesByType;
import com.example.movies.databinding.ItemRecyclerViewBinding;
import com.example.movies.listener.statusbar.IOnChangeStatusBarColor;
import com.example.movies.listener.update.IOnRefreshData;
import com.example.movies.model.movie.MovieObject;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> {

    public AdapterManager adapterManager;
    List<ItemRecyclerViewBinding> bindings = new ArrayList<>();
    List<String> listTitleGenres = new ArrayList<>();
    IOnRefreshData iOnRefreshData;
    FragmentManager fragmentManager;
    private IOnChangeStatusBarColor iOnChangeStatusBarColor;

    public ParentAdapter(AdapterManager adapterManager, FragmentManager fragmentManagerA, IOnRefreshData iOnRefreshDataTemp, IOnChangeStatusBarColor i) {
        this.adapterManager = adapterManager;
        this.iOnRefreshData = iOnRefreshDataTemp;
        this.fragmentManager = fragmentManagerA;
        this.iOnChangeStatusBarColor = i;
        if (adapterManager.typeMovieOrTVShow.equals(Utils.TYPE_MOVIE)) {
            listTitleGenres = Utils.titleGenresMovie;
        } else if (adapterManager.typeMovieOrTVShow.equals(Utils.TYPE_TV_SHOW)) {
            listTitleGenres = Utils.titleGenresTVShow;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecyclerViewBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_recycler_view, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (adapterManager.mapListMoviesObservableFieldAdapter.size() > 0 && listTitleGenres.size() > 0) {
            bindings.add(holder.binding);
            MoviesAdapterByGenres item = Objects.requireNonNull(adapterManager.mapListMoviesObservableFieldAdapter.get(listTitleGenres.get(position))).get();
            if (position <= Utils.titleGenresMovie.size() - 1) {
                Objects.requireNonNull(item).setTitle(listTitleGenres.get(position));
            }
            holder.binding.setItem(item);
            holder.setItemAdapter(item);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                holder.binding.shimmerLayoutItemRecyclerView.post(() -> holder.binding.shimmerLayoutItemRecyclerView.setVisibility(View.GONE));
                holder.binding.layoutItemRecyclerView.post(() -> holder.binding.layoutItemRecyclerView.setVisibility(View.VISIBLE));
            }, 1500);

            holder.binding.moreMovieByType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomSheetExpandShowMoviesByType bottomSheetExpandShowMoviesByType = new BottomSheetExpandShowMoviesByType(view.getContext(), adapterManager.typeMovieOrTVShow, listTitleGenres.get(holder.getAbsoluteAdapterPosition()), new ArrayList<>(holder.itemAdapter.getMovieList()),holder.itemAdapter.singleItemClicked,iOnChangeStatusBarColor,holder.itemAdapter.page);
                    bottomSheetExpandShowMoviesByType.show(fragmentManager, "AAA");
                }
            });

        }
        else{
            movieResources.fetchDataAgain();
        }


    }

    @Override
    public int getItemCount() {
        return adapterManager.mapListMoviesObservableFieldAdapter.size();
    }


    public void onRefreshData() {
        shimmerLayout(true);
        adapterManager.onRefreshData();
    }


    public void stopRefreshData() {
        shimmerLayout(false);
    }

    private void shimmerLayout(boolean b) {
        for (ItemRecyclerViewBinding item : bindings) {
            item.layoutItemRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    item.layoutItemRecyclerView.setVisibility(b ? View.GONE : View.VISIBLE);
                }
            });
            item.shimmerLayoutItemRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    item.shimmerLayoutItemRecyclerView.setVisibility(b ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    public void setValuesUpdateMovies(String type, MovieObject.Movie item) {
        for (MovieObject.Movie a : Objects.requireNonNull(Objects.requireNonNull(adapterManager.mapListMoviesObservableFieldAdapter.get(type)).get()).getMovieList()) {
            if (a.getId().trim().equals(item.getId().trim())) {
                a.setMovie(item);
                break;
            }
        }
    }

    public void updateTitleGenre() {
        for (int i = 0; i < bindings.size(); i++) {
            int finalI = i;
            String title = listTitleGenres.get(finalI);
            bindings.get(i).titleGenre.post(() -> bindings.get(finalI).titleGenre.setText(title));
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemRecyclerViewBinding binding;
        private MoviesAdapterByGenres itemAdapter;

        public ViewHolder(@NonNull ItemRecyclerViewBinding item) {
            super(item.getRoot());
            this.binding = item;
        }

        public void setItemAdapter(MoviesAdapterByGenres item) {
            this.itemAdapter = item;
        }

    }

}
