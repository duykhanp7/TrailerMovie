package com.example.movies.ui.bottomsheet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.movies.data.model.setting.Setting;
import com.example.movies.data.room.DatabaseRepository;
import com.example.movies.databinding.BottomSheetChangeThemeBinding;
import com.example.movies.listener.theme.IOnThemeChange;
import com.example.movies.utils.ThemeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class BottomSheetChangeTheme extends BottomSheetDialogFragment {

    private BottomSheetChangeThemeBinding binding;
    private final DatabaseRepository databaseRepository;
    private Context context;
    private IOnThemeChange iOnThemeChange;

    public BottomSheetChangeTheme(Context context, IOnThemeChange i) {
        this.context = context;
        databaseRepository = new DatabaseRepository(context);
        this.iOnThemeChange = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetChangeThemeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Setting setting = databaseRepository.getUserSetting(Objects.requireNonNull(user).getUid());
        Log.i("AAA","SETTING BEFORE THEME : "+setting.getLanguage()+" "+setting.getTheme());

        binding.lightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getCurrentTheme() == 2){
                    setting.setTheme(AppCompatDelegate.MODE_NIGHT_NO);
                    databaseRepository.updateSetting(setting);
                    ThemeUtils.setTheme(setting.getTheme(),context);
                    //iOnThemeChange.onThemeChange(setting.getTheme());
                }
                dismiss();
            }
        });

        binding.darkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getCurrentTheme() == 1){
                    setting.setTheme(AppCompatDelegate.MODE_NIGHT_YES);
                    databaseRepository.updateSetting(setting);
                    ThemeUtils.setTheme(setting.getTheme(),context);
                    //iOnThemeChange.onThemeChange(setting.getTheme());
                }
                dismiss();
            }
        });
    }

    public int getCurrentTheme(){
        return AppCompatDelegate.getDefaultNightMode();
    }

}
