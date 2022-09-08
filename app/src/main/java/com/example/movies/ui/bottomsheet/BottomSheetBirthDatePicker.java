package com.example.movies.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movies.databinding.BottomSheetChangeBirthDateBinding;
import com.example.movies.listener.changebirthdate.IOnChangeBirthDate;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetBirthDatePicker extends BottomSheetDialogFragment {

    private BottomSheetChangeBirthDateBinding binding;
    private String dateSelected = "";
    private IOnChangeBirthDate iOnChangeBirthDate;

    public BottomSheetBirthDatePicker(IOnChangeBirthDate i) {
        this.iOnChangeBirthDate = i;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetChangeBirthDateBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.birthDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                String date = getDateStringSelected(day, month + 1, year);
                dateSelected = date;
            }
        });

        binding.buttonSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOnChangeBirthDate.onChangeBirthDate(dateSelected);
                dismiss();
            }
        });
    }


    public String getDateStringSelected(int a, int b, int c) {
        return String.format("%s-%s-%s", a, b, c);
    }
}
