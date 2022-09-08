package com.example.movies.adapter.spinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movies.R;
import com.example.movies.data.model.anothers.ItemSpinner;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<ItemSpinner> {


    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<ItemSpinner> objects) {
        super(context, resource, objects);
    }

    //VIEW FOR ITEM SELECTED
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_selected, parent, false);

        ItemSpinner itemSpinner = this.getItem(position);
        TextView textView;
        textView = layout.findViewById(R.id.itemSpinner);
        textView.setText(itemSpinner.getTitle());

        return layout;
    }

    //VIEW FOR ITEM DROP DOWN
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        parent.setLayoutMode(Spinner.MODE_DIALOG);
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner, parent, false);

        ItemSpinner itemSpinner = this.getItem(position);

        TextView textView;
        textView = convertView.findViewById(R.id.itemSpinner);
        textView.setText(itemSpinner.getTitle());

        return convertView;
    }
}
