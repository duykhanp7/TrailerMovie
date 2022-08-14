package com.example.movies.bottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movies.R;
import com.example.movies.databinding.BottomSheetChangeLanguageBinding;
import com.example.movies.locale.LocaleHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

public class BottomSheetChangeLanguage extends BottomSheetDialogFragment {

    private BottomSheetChangeLanguageBinding binding;
    private Context context;
    private String languages = "en";

    public BottomSheetChangeLanguage(Context contextTemp){
        this.context = contextTemp;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetChangeLanguageBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.languagesVietNam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languages = "vi";
                LocaleHelper.setLocale(context,languages);
                dismiss();
            }
        });

        binding.languageUnitedStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languages = "en";
                LocaleHelper.setLocale(context,languages);
                dismiss();
            }
        });
    }


}
