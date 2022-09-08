package com.example.movies.ui.bottomsheet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movies.R;
import com.example.movies.databinding.BottomSheetChangeFirstLastNameBinding;
import com.example.movies.listener.changename.IOnChangeDisplayName;
import com.example.movies.data.model.users.UserProfile;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class BottomSheetChangeFirstLastName extends BottomSheetDialogFragment {

    private BottomSheetChangeFirstLastNameBinding binding;
    private boolean isFirstNameValid = false;
    private boolean isLastNameValid = false;
    private UserProfile userProfile;
    private IOnChangeDisplayName iOnChangeDisplayName;

    public BottomSheetChangeFirstLastName(UserProfile userProfileTemp, IOnChangeDisplayName i){
        this.userProfile = userProfileTemp;
        iOnChangeDisplayName = i;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetChangeFirstLastNameBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setUser(userProfile);

        binding.textInputCurrentFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onFirstNameChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.textInputCurrentLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onLastNameChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.buttonConfirmNameChanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFirstNameValid){
                    binding.textInputCurrentFirstNameLayout.setErrorEnabled(true);
                    binding.textInputCurrentFirstNameLayout.setError(getString(R.string.require_not_null));
                }
                if(!isLastNameValid){
                    binding.textInputCurrentLastNameLayout.setErrorEnabled(true);
                    binding.textInputCurrentLastNameLayout.setError(getString(R.string.require_not_null));
                }
                if(isFirstNameValid && isLastNameValid){
                    userProfile.setFirstName(Objects.requireNonNull(binding.textInputCurrentFirstName.getText()).toString());
                    userProfile.setLastName(Objects.requireNonNull(binding.textInputCurrentLastName.getText()).toString());
                    iOnChangeDisplayName.onChangeDisplayName(userProfile);

                    Toasty.success(requireContext(), R.string.change_name_success,Toast.LENGTH_SHORT,true).show();


                    dismiss();
                }
            }
        });

    }


    public UserProfile getUserProfile(){
        return userProfile;
    }

    public void setUserProfile(UserProfile user){
        this.userProfile = user;
    }

    public void onFirstNameChanged(CharSequence charSequence){
        if(charSequence.toString().isEmpty()){
            isFirstNameValid = false;
            binding.textInputCurrentFirstNameLayout.setErrorEnabled(true);
            binding.textInputCurrentFirstNameLayout.setError("(*) Requires is not null.");
        }
        else{
            isFirstNameValid = true;
            binding.textInputCurrentFirstNameLayout.setErrorEnabled(false);
        }
    }

    public void onLastNameChanged(CharSequence charSequence){
        if(charSequence.toString().isEmpty()){
            isLastNameValid = false;
            binding.textInputCurrentLastNameLayout.setErrorEnabled(true);
            binding.textInputCurrentLastNameLayout.setError("(*) Requires is not null.");
        }
        else{
            isLastNameValid = true;
            binding.textInputCurrentLastNameLayout.setErrorEnabled(false);
        }
    }



}
