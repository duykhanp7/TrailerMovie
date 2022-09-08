package com.example.movies.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.movies.data.model.anothers.IDMovieObject;
import com.example.movies.data.model.comment.Comment;
import com.example.movies.data.model.keyword.Keyword;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.data.model.videos.TrailerObject;
import com.example.movies.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseObjectKeywordManager {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    boolean isFinishLoadKeyword = false;

    public FirebaseObjectKeywordManager() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(Utils.KEYWORD_FOLDER);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void addKeyword(Keyword keyword) {
        String uid = firebaseUser.getUid();
        DatabaseReference reference = databaseReference.child(uid).child(keyword.getKeywordString());
        reference.setValue(keyword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void deleteKeyword(Keyword keyword) {
        String uid = firebaseUser.getUid();
        DatabaseReference reference = databaseReference.child(uid).child(keyword.getKeywordString());
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


    public List<Keyword> getAllKeywordAlongUserID() {
        String uid = firebaseUser.getUid();
        List<Keyword> keywordList = new ArrayList<>();
        DatabaseReference reference = databaseReference.child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isFinishLoadKeyword) {
                    for (DataSnapshot a : snapshot.getChildren()) {
                        Map<String, String> mapValue = (Map<String, String>) a.getValue();
                        if (mapValue != null) {
                            keywordList.add(Keyword.fromMap(mapValue));
                        }
                    }
                    isFinishLoadKeyword = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return keywordList;
    }


}
