package com.example.movies.data.firebase;

import androidx.annotation.NonNull;

import com.example.movies.data.model.users.UserProfile;
import com.example.movies.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * Lưu thông tin tài khoản users
 */
public class FirebaseObjectUserManager {

    private final DatabaseReference databaseReference;


    public FirebaseObjectUserManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Thêm user
     */
    public void addUser(UserProfile user) {
        FirebaseUser userAuth = FirebaseAuth.getInstance().getCurrentUser();
        if (userAuth != null) {
            DatabaseReference reference = databaseReference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER);
            reference.child(Objects.requireNonNull(userAuth).getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    FirebaseAuth.getInstance().signOut();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    /**
     * Xóa user
     */
    public void removeUser(UserProfile user) {
        FirebaseUser userAuth = FirebaseAuth.getInstance().getCurrentUser();
        if (userAuth != null) {
            DatabaseReference reference = databaseReference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER).child(Objects.requireNonNull(userAuth).getUid());
            reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

}
