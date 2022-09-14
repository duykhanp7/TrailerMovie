package com.example.movies.data.firebase;

import static com.example.movies.ui.activity.details.DetailsMovieActivity.mutableLiveDataUserChanged;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movies.data.model.users.UserProfile;
import com.example.movies.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

/**
 * Lưu thông tin tài khoản users
 */
public class FirebaseObjectUserManager {

    private static DatabaseReference databaseReference;
    private static ValueEventListener valueEventListener;
    private static DatabaseReference referenceCheckEmailExists;
    public static Boolean emailExists;

    public FirebaseObjectUserManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        emailExists = false;
        observerPropertiesUserChanged();
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

            saveEmailCreatedByEmailAndPassword(user.getEmail());

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

    /**
     * Thêm email của user vào danh sách users đăng kí tài khoản bằng email và password
     */
    public void saveEmailCreatedByEmailAndPassword(String email) {
        email = email.replaceAll(Utils.SPECIFIC_CHARACTERS,"");
        DatabaseReference reference = databaseReference.child(Utils.FIREBASE_USERS_EMAIL).child(email);
        reference.setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    /**
     * Kiểm tra xem email này có khớp với bất kì tài khoản nào không
     */
    public static void checkUserEmailIsExists(String email) {
        emailExists = false;
        referenceCheckEmailExists = databaseReference.child(Utils.FIREBASE_USERS_EMAIL).child(email.replaceAll(Utils.SPECIFIC_CHARACTERS,""));
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                emailExists = snapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        referenceCheckEmailExists.addValueEventListener(valueEventListener);
    }

    /**
     * Loại bỏ listener check email exists
     */
    public static void removeDatabaseReferenceCheckEmailExists() {
        emailExists = false;
        referenceCheckEmailExists.removeEventListener(valueEventListener);
    }

    /**
     * Quan sát và cập nhật mỗi khi thông tin users thay đổi
     */
    public void observerPropertiesUserChanged() {
        DatabaseReference reference = databaseReference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String, String> mapValues = (Map<String, String>) snapshot.getValue();
                UserProfile userProfile = UserProfile.fromMap(Objects.requireNonNull(mapValues));
                Log.i("AAA", "DATA USER CHANGE : " + mapValues + " -- " + userProfile);
                if (mutableLiveDataUserChanged != null) {
                    mutableLiveDataUserChanged.postValue(userProfile);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
