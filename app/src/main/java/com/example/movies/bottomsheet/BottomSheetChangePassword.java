package com.example.movies.bottomsheet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.movies.R;
import com.example.movies.activity.main.MainActivity;
import com.example.movies.databinding.BottomSheetChangePasswordBinding;
import com.example.movies.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

public class BottomSheetChangePassword extends BottomSheetDialogFragment {

    private BottomSheetChangePasswordBinding binding;
    private boolean isCurrentPasswordValid = false;
    private  boolean isNewPasswordValid = false;
    private boolean isNewPasswordConfirmValid = false;
    private final Context context;
    private boolean isFirstLoadPassword = false;
    private String currentPasswordValid = "";


    public BottomSheetChangePassword(Context contextTemp){
        this.context = contextTemp;
        getPasswordOfCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetChangePasswordBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textInputCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onCurrentPasswordChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.textInputNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onNewPasswordChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.textInputNewPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onNewPasswordConfirmChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.buttonConfirmChanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonConfirmChangedClick();
            }
        });

    }

    public void onCurrentPasswordChanged(CharSequence charSequence){
        if(charSequence.toString().isEmpty()){
            isCurrentPasswordValid = false;
            binding.textInputCurrentPasswordLayout.setErrorEnabled(true);
            binding.textInputCurrentPasswordLayout.setError("(*) Request cannot be empty.");
        }
        else{
            isCurrentPasswordValid = true;
            binding.textInputCurrentPasswordLayout.setErrorEnabled(false);
        }
    }


    public void onNewPasswordChanged(CharSequence charSequence){
        if(charSequence.toString().isEmpty()){
            isNewPasswordValid = false;
            binding.textInputNewPasswordLayout.setErrorEnabled(true);
            binding.textInputNewPasswordLayout.setError("(*) Invalid new password.");
        }
        else{
            if(charSequence.toString().length() <= 6){
                isNewPasswordValid = false;
                binding.textInputNewPasswordLayout.setErrorEnabled(true);
                binding.textInputNewPasswordLayout.setError("(*) Password length should be more than six.");
            }
            else{
                isNewPasswordValid = true;
                binding.textInputNewPasswordLayout.setErrorEnabled(false);
            }
        }
    }

    public void onNewPasswordConfirmChanged(CharSequence charSequence){
        String passwordConfirm = Objects.requireNonNull(binding.textInputNewPassword.getText()).toString();
        String currentPasswordConfirm = Objects.requireNonNull(binding.textInputNewPasswordConfirm.getText()).toString();
        if(!passwordConfirm.equals(currentPasswordConfirm) || currentPasswordConfirm.isEmpty()){
            isNewPasswordConfirmValid = false;
            binding.textInputNewPasswordConfirmLayout.setErrorEnabled(true);
            binding.textInputNewPasswordConfirmLayout.setError("(*) Password is not the same.");
        }
        else{
            isNewPasswordConfirmValid = true;
            binding.textInputNewPasswordConfirmLayout.setErrorEnabled(false);
        }
    }

    public void onButtonConfirmChangedClick(){
        String currentPassword = Objects.requireNonNull(binding.textInputCurrentPassword.getText()).toString();
        if(currentPassword.isEmpty()){
            binding.textInputCurrentPasswordLayout.setErrorEnabled(true);
            binding.textInputCurrentPasswordLayout.setError("(*) Request cannot be empty.");
        }
        else{
            if(currentPasswordValid.equals(getPasswordEncrypted(currentPassword))){
                isCurrentPasswordValid = true;
            }
            else{
                isCurrentPasswordValid = false;
                binding.textInputCurrentPasswordLayout.setErrorEnabled(true);
                binding.textInputCurrentPasswordLayout.setError(getString(R.string.current_pass_not_true));
            }
        }
        if(!isNewPasswordValid){
            binding.textInputNewPasswordLayout.setErrorEnabled(true);
            binding.textInputNewPasswordLayout.setError(getString(R.string.invalid_new_pass));
        }
        if(!isNewPasswordConfirmValid){
            binding.textInputNewPasswordConfirmLayout.setErrorEnabled(true);
            binding.textInputNewPasswordConfirmLayout.setError(getString(R.string.pass_not_the_same));
        }
        if(isCurrentPasswordValid && isNewPasswordValid && isNewPasswordConfirmValid){
            String newPassword = binding.textInputNewPassword.getText().toString();
            String password = getPasswordEncrypted(newPassword);
            updatePassword(password);
        }
        else{
            Toast.makeText(context, R.string.change_pass_fail_try_again,Toast.LENGTH_LONG).show();
        }

    }


    public void updatePassword(String password){
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, R.string.change_pass_success,Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, R.string.change_pass_fail,Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER)
                                                     .child(user.getUid()).child("password").setValue(password);


    }

    public void getPasswordOfCurrentUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER).child(Objects.requireNonNull(user).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!isFirstLoadPassword){
                    Map<String,String> map = (Map<String, String>) snapshot.getValue();
                    currentPasswordValid = map.get("password");
                    isFirstLoadPassword = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getPasswordEncrypted(String password){
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    public String getPasswordDecrypted(String passwordEncrypted){
        return new String(Base64.getDecoder().decode(passwordEncrypted));
    }


}
