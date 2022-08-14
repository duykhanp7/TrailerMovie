package com.example.movies.bottomsheet;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.movies.databinding.BottomSheetChooseImageOptionsBinding;
import com.example.movies.listener.chooseimageoption.IOnItemImageOptionClick;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetChooseImageOptions extends BottomSheetDialogFragment {

    private BottomSheetChooseImageOptionsBinding binding;
    private int selection = 0;
    private IOnItemImageOptionClick iOnItemImageOptionClick;

    public BottomSheetChooseImageOptions(IOnItemImageOptionClick i) {
        this.iOnItemImageOptionClick = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetChooseImageOptionsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.layoutChooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selection = 1;
                iOnItemImageOptionClick.onItemImageOptionClick(selection);
                dismiss();
            }
        });

        binding.layoutTakeAPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selection = 2;
                iOnItemImageOptionClick.onItemImageOptionClick(selection);
                dismiss();
            }
        });

    }

    public int getSelection() {
        return selection;
    }


    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        selection = 0;
        super.show(manager, tag);
    }
}
