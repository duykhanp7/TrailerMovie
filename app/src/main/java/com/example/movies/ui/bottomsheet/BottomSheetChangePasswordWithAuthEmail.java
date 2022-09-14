package com.example.movies.ui.bottomsheet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movies.R;
import com.example.movies.data.firebase.FirebaseObjectUserManager;
import com.example.movies.databinding.BottomSheetChangePasswordAuthBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class BottomSheetChangePasswordWithAuthEmail extends BottomSheetDialogFragment {

    private BottomSheetChangePasswordAuthBinding bottomSheetChangePasswordAuthBinding;
    private Context context;

    public BottomSheetChangePasswordWithAuthEmail(Context contextA) {
        this.context = contextA;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bottomSheetChangePasswordAuthBinding = BottomSheetChangePasswordAuthBinding.inflate(getLayoutInflater());
        return bottomSheetChangePasswordAuthBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindClick();

    }

    public void bindObserver() {

    }

    public void bindClick() {

        bottomSheetChangePasswordAuthBinding.buttonSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmail();
            }
        });

        bottomSheetChangePasswordAuthBinding.buttonConfirmChanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCode();
            }
        });
    }

    public void showHideViewInputEmail(int visibility) {
        bottomSheetChangePasswordAuthBinding.textInputEmailLinkedWithThisAccountLayout.setVisibility(visibility);
    }

    public void showHideViewInputCode(int visibility) {
        bottomSheetChangePasswordAuthBinding.textInputCodeLayout.setVisibility(visibility);
    }

    public void showHideViewInputNewPassword(int visibility) {
        bottomSheetChangePasswordAuthBinding.textInputNewPasswordLayout.setVisibility(visibility);
    }

    public void showHideViewInputConfirmNewPassword(int visibility) {
        bottomSheetChangePasswordAuthBinding.textInputNewPasswordConfirmLayout.setVisibility(visibility);
    }

    public void showHideButtonSendCode(int visibility) {
        bottomSheetChangePasswordAuthBinding.buttonSendCode.setVisibility(View.GONE);
    }

    public void checkEmail() {
        String email = Objects.requireNonNull(bottomSheetChangePasswordAuthBinding.textInputEmailLinkedWithThisAccount.getText()).toString();
        if (!email.isEmpty()) {
            //Kiểm tra email đã nhập có hợp  lệ không
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toasty.info(context, context.getResources().getString(R.string.waiting_check_email), Toasty.LENGTH_SHORT, true).show();
                FirebaseObjectUserManager.checkUserEmailIsExists(email);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (FirebaseObjectUserManager.emailExists) {
                            Toasty.success(context, context.getResources().getString(R.string.code_sent_to_email), Toasty.LENGTH_SHORT, true).show();
                            FirebaseObjectUserManager.removeDatabaseReferenceCheckEmailExists();
                        } else {
                            Toasty.warning(context, context.getResources().getString(R.string.email_not_reference_to_any_acc), Toasty.LENGTH_SHORT, true).show();
                        }
                    }
                }, 2000);
            }
            //Thông báo email không hợp lệ
            else {
                Toasty.warning(context, context.getResources().getString(R.string.invalid_email), Toasty.LENGTH_SHORT, true).show();
            }

        } else {
            Toasty.warning(context, context.getResources().getString(R.string.enter_email_link_account), Toasty.LENGTH_SHORT, true).show();
        }
    }


    public void submitCode() {
        String code = Objects.requireNonNull(bottomSheetChangePasswordAuthBinding.textInputCode.getText()).toString();
        if (!code.isEmpty()) {
            showHideViewInputEmail(View.GONE);
            showHideViewInputCode(View.GONE);
            showHideButtonSendCode(View.GONE);
            showHideViewInputNewPassword(View.VISIBLE);
            showHideViewInputConfirmNewPassword(View.VISIBLE);
        } else {
            Toasty.warning(context, context.getResources().getString(R.string.enter_code_sent), Toasty.LENGTH_SHORT, true).show();
        }
    }


}
