package com.example.movies.ui.bottomsheet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movies.R;
import com.example.movies.data.model.setting.Setting;
import com.example.movies.data.room.DatabaseRepository;
import com.example.movies.databinding.BottomSheetChangeLanguageBinding;
import com.example.movies.listener.language.IOnLanguageChange;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class BottomSheetChangeLanguage extends BottomSheetDialogFragment {

    private BottomSheetChangeLanguageBinding binding;
    private Context context;
    private String languages;
    private final IOnLanguageChange iOnLanguageChange;
    private DatabaseRepository databaseRepository;

    public BottomSheetChangeLanguage(Context contextTemp, IOnLanguageChange i) {
        this.context = contextTemp;
        this.iOnLanguageChange = i;
        databaseRepository = new DatabaseRepository(contextTemp);
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


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Setting setting = databaseRepository.getUserSetting(Objects.requireNonNull(user).getUid());
        Log.i("AAA","SETTING BEFORE LANGUAGE : "+setting.getLanguage()+" "+setting.getTheme());

        binding.languagesVietNam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languages = "vi";
                if (!setting.getLanguage().equals(languages)) {
                    setting.setLanguage(languages);
                    databaseRepository.updateSetting(setting);
                    iOnLanguageChange.onLanguageChange(languages);
                } else {
                    Toasty.warning(context, R.string.text_current_language_vi, Toasty.LENGTH_SHORT, true).show();
                }
                dismiss();
            }
        });

        binding.languageUnitedStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languages = "en";
                if (!setting.getLanguage().equals(languages)) {
                    setting.setLanguage(languages);
                    databaseRepository.updateSetting(setting);
                    iOnLanguageChange.onLanguageChange(languages);
                } else {
                    Toasty.warning(context, R.string.text_current_language_us, Toasty.LENGTH_SHORT, true).show();
                }
                dismiss();
            }
        });
    }
}
