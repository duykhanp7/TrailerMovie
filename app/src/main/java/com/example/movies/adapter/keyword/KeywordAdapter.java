package com.example.movies.adapter.keyword;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.data.model.keyword.Keyword;
import com.example.movies.databinding.LayoutItemKeywordBinding;
import com.example.movies.listener.chip.IOnChipClickListener;
import com.example.movies.ui.activity.main.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.ViewHolder> {

    private List<Keyword> keywordList = new ArrayList<>();
    private Map<String, Keyword> keywordMap = new HashMap<>();
    private IOnChipClickListener iOnChipClickListener;

    public KeywordAdapter(List<Keyword> list, IOnChipClickListener i) {
        this.keywordList = list;
        this.iOnChipClickListener = i;
        list.forEach(item -> {
            keywordMap.put(item.getKeyword().concat(item.getType()), item);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemKeywordBinding binding = LayoutItemKeywordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i("AAA", "BIND KEYWORD");
        Keyword keyword = keywordList.get(position);
        holder.bindData(keyword);
    }

    @Override
    public int getItemCount() {
        return this.keywordList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addKeyword(Keyword a) {
        if (!keywordMap.containsKey(a.getKeyword().concat(a.getType()))) {
            int size = keywordList.size();
            this.keywordList.add(size, a);
            keywordMap.put(a.getKeyword().concat(a.getType()), a);
            notifyItemInserted(size);
        }
    }

    public void setKeywordList(List<Keyword> list) {
        this.keywordList.addAll(list);
        list.forEach(item -> {
            keywordMap.put(item.getKeyword().concat(item.getType()), item);
        });
    }

    public List<Keyword> getKeywordList() {
        return this.keywordList;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        LayoutItemKeywordBinding keywordBinding;
        Keyword keyword;

        public ViewHolder(@NonNull LayoutItemKeywordBinding binding) {
            super(binding.getRoot());
            this.keywordBinding = binding;
        }

        void bindData(Keyword a) {
            this.keyword = a;
            this.keywordBinding.setKeyword(this.keyword);
            bindClick();
        }

        void bindClick() {

            keywordBinding.chipKeyword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iOnChipClickListener.onChipClick(keyword);
                }
            });

            keywordBinding.chipKeyword.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.deleteKeyword(keyword);
                    int pos = getAbsoluteAdapterPosition();
                    keywordList.remove(pos);
                    notifyItemRemoved(pos);
                    MainActivity.showOrHideOtherLayoutEmpty();
                }
            });

        }
    }

}
