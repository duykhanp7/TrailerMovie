package com.example.movies.adapter.cast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.databinding.ItemCastDetailsLayoutBinding;
import com.example.movies.listener.cast.ICastItemClickListener;
import com.example.movies.data.model.cast.Cast;
import com.example.movies.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter chi tiết diễn viên
 */
public class CastDetailsAdapter extends RecyclerView.Adapter<CastDetailsAdapter.ViewHolder> {

    List<Cast> casts;
    ICastItemClickListener iCastItemClickListener;

    public CastDetailsAdapter(ICastItemClickListener iCastItemClickListener) {
        casts = new ArrayList<>();
        this.iCastItemClickListener = iCastItemClickListener;
    }

    /**
     * Thêm danh sách diễn viên*/
    public void setCasts(List<Cast> casts) {
        this.casts = casts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCastDetailsLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_cast_details_layout, parent, false);
        return new ViewHolder(binding, iCastItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cast cast = casts.get(position);
        holder.bindData(cast);
    }

    @Override
    public int getItemCount() {
        return casts.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemCastDetailsLayoutBinding binding;
        Cast cast;
        ICastItemClickListener iCastItemClickListener;

        public ViewHolder(@NonNull ItemCastDetailsLayoutBinding binding, ICastItemClickListener iCastItemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.iCastItemClickListener = iCastItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        public void bindData(Cast cast) {
            this.cast = cast;
            binding.setItem(cast);
            setUpUI();
        }

        /**
         * Chỉnh sửa giao diện dựa theo người dùng đang dùng chế độ nền tối hay nền sáng
         * AppCompatDelegate.MODE_NIGHT_NO : nền sáng
         * AppCompatDelegate.MODE_NIGHT_YES : nền sáng
         */
        public void setUpUI() {
            if (ThemeUtils.getCurrentTheme() == AppCompatDelegate.MODE_NIGHT_NO) {
                binding.layoutCast.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.color.white));
                binding.layoutParent.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.color.white));
                binding.textTitleRole.setTextColor(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.black));
                binding.textTitleRole2.setTextColor(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.black));
            } else if (ThemeUtils.getCurrentTheme() == AppCompatDelegate.MODE_NIGHT_YES) {
                binding.layoutCast.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.color.black));
                binding.layoutParent.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.color.black));
                binding.textTitleRole.setTextColor(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.white));
                binding.textTitleRole2.setTextColor(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.white));
            }
        }

        @Override
        public void onClick(View view) {
            iCastItemClickListener.onItemCastClick(cast);
        }
    }
}
