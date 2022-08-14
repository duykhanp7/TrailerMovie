package com.example.movies.adapter.cast;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.api.APIGetData;
import com.example.movies.databinding.ItemCastLayoutBinding;
import com.example.movies.listener.cast.ICastItemClickListener;
import com.example.movies.model.anothers.IDMovieObject;
import com.example.movies.model.anothers.Person;
import com.example.movies.model.cast.Cast;
import com.example.movies.model.movie.MovieObject;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    public List<Cast> casts;
    public Map<String, Person> personMap;
    public static ObservableField<List<Cast>> castObservable = new ObservableField<>();
    ICastItemClickListener iCastItemClickListener;

    public CastAdapter(ICastItemClickListener iCastItemClickListener) {
        casts = new ArrayList<>();
        personMap = new HashMap<>();
        castObservable.set(casts);
        this.iCastItemClickListener = iCastItemClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCasts(List<Cast> casts) {
        this.casts = casts;
        castObservable.set(casts);
        //notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCastList(List<Cast> castList) {
        this.casts = castList;
        notifyDataSetChanged();
    }

    public void addCast(Cast cast) {
        int size = casts.size();
        casts.add(size, cast);
        personMap.put(cast.getId(), new Person(cast.getId(), Utils.TYPE_CAST));
        notifyItemInserted(size);
    }

    public void removeCast(Cast cast) {
        int positionItemRemove = getPositionCast(cast);
        this.casts.remove(positionItemRemove);
        this.personMap.remove(cast.getId());
        this.notifyItemRemoved(positionItemRemove);
    }

    private int getPositionCast(Cast cast) {
        for (int i = 0; i < casts.size(); i++) {
            if (casts.get(i).getId().equals(cast.getId())) {
                return i;
            }
        }
        return -1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addMapIDCast(Map<String, Person> map) {
        personMap.clear();
        personMap.putAll(map);
    }

    public boolean containsCast(Cast cast) {
        return personMap.containsKey(cast.getId());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCastLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_cast_layout, parent, false);
        return new ViewHolder(binding, iCastItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cast cast = casts.get(position);
        holder.binding.setItem(cast);
        holder.setCast(cast);
    }

    @Override
    public int getItemCount() {
        return casts.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void getCastsFromAPI() {
        casts.clear();
        notifyDataSetChanged();
        for (String key : personMap.keySet()) {
            Person person = personMap.get(key);
            APIGetData.apiGetData.getCastDetails(person.getId()).enqueue(new Callback<Cast>() {
                @Override
                public void onResponse(Call<Cast> call, Response<Cast> response) {
                    int size = getItemCount();
                    casts.add(size, response.body());
                    notifyItemInserted(size);
                }

                @Override
                public void onFailure(Call<Cast> call, Throwable t) {

                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemCastLayoutBinding binding;
        ICastItemClickListener iCastItemClickListener;
        Cast cast;

        public ViewHolder(@NonNull ItemCastLayoutBinding binding, ICastItemClickListener iCastItemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.iCastItemClickListener = iCastItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        public void setCast(Cast cast) {
            this.cast = cast;
        }

        @Override
        public void onClick(View view) {
            iCastItemClickListener.onItemCastClick(cast);
        }
    }
}
