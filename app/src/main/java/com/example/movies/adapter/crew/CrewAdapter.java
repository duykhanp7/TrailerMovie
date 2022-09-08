package com.example.movies.adapter.crew;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.data.api.APIGetData;
import com.example.movies.databinding.ItemCrewLayoutBinding;
import com.example.movies.listener.crew.ICrewItemClickListener;
import com.example.movies.data.model.anothers.Person;
import com.example.movies.data.model.crew.Crew;
import com.example.movies.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Adapter đạo diễn, nhà sản xuất....
 */
public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.ViewHolder> {

    public List<Crew> crews;
    public Map<String, Person> personMap;
    public static ObservableField<List<Crew>> crewObservable = new ObservableField<>();
    ICrewItemClickListener iCrewItemClickListener;

    public CrewAdapter(ICrewItemClickListener iCrewItemClickListener) {
        crews = new ArrayList<>();
        personMap = new HashMap<>();
        crewObservable.set(crews);
        this.iCrewItemClickListener = iCrewItemClickListener;
    }

    /**
     * Thêm danh sách các đạo diễn, nhà sản xuất....
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setCrews(List<Crew> crews) {
        this.crews = crews;
        crewObservable.set(crews);
    }

    /**
     * Thêm đạo diễn, nhà sản xuất....
     */
    public void addCrew(Crew crew) {
        int size = crews.size();
        crews.add(size, crew);
        personMap.put(crew.getId(), new Person(crew.getId(), Utils.TYPE_CAST));
        notifyItemInserted(size);
    }

    /**
     * Xóa đạo diễn...
     */
    public void removeCrew(Crew crew) {
        int positionItemRemove = getPositionCrew(crew);
        this.crews.remove(positionItemRemove);
        this.personMap.remove(crew.getId());
        this.notifyItemRemoved(positionItemRemove);
    }

    /**
     * Trả về vị trí của đạo diễn, nhà sản xuất....
     */
    private int getPositionCrew(Crew crew) {
        for (int i = 0; i < crews.size(); i++) {
            if (crews.get(i).getId().equals(crew.getId())) {
                return i;
            }
        }
        return -1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addMapIDCrew(Map<String, Person> map) {
        personMap.clear();
        personMap.putAll(map);

    }

    /**
     * Kiểm tra xem có chứa đạo diễn, nhà sản xuất nào đó hay không
     */
    public boolean containsCrew(Crew crew) {
        return personMap.containsKey(crew.getId());
    }

    /**
     * Lấy danh cách các đạo diễn, nhà sản xuất... từ API
     */
    @SuppressLint("NotifyDataSetChanged")
    public void getCrewsFromAPI() {
        crews.clear();
        notifyDataSetChanged();
        for (String key : personMap.keySet()) {
            Person person = personMap.get(key);
            APIGetData.apiGetData.getCrewDetails(person.getId()).enqueue(new Callback<Crew>() {
                @Override
                public void onResponse(Call<Crew> call, Response<Crew> response) {
                    int size = getItemCount();
                    crews.add(size, response.body());
                    notifyItemInserted(size);
                }

                @Override
                public void onFailure(Call<Crew> call, Throwable t) {

                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCrewLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_crew_layout, parent, false);
        return new ViewHolder(binding, iCrewItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Crew crew = crews.get(position);
        holder.bindData(crew);
    }

    @Override
    public int getItemCount() {
        return crews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemCrewLayoutBinding binding;
        ICrewItemClickListener iCrewItemClickListener;
        public Crew crew;

        public ViewHolder(@NonNull ItemCrewLayoutBinding binding, ICrewItemClickListener iCrewItemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.iCrewItemClickListener = iCrewItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        public void bindData(Crew crew) {
            this.crew = crew;
            binding.setItem(crew);
        }

        @Override
        public void onClick(View view) {
            iCrewItemClickListener.onItemCrewClick(crew);
        }
    }
}
