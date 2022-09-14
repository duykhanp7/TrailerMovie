package com.example.movies.data.firebase;


import static com.example.movies.ui.activity.details.DetailsMovieActivity.mutableLiveDataAddComment;
import static com.example.movies.ui.activity.details.DetailsMovieActivity.mutableLiveDataDeleteComment;
import static com.example.movies.ui.activity.details.DetailsMovieActivity.mutableLiveDataUpdateComment;
import static com.example.movies.ui.activity.main.MainActivity.userProfile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movies.data.model.comment.Comment;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.data.model.videos.TrailerObject;
import com.example.movies.helper.TimeUtils;
import com.example.movies.ui.activity.main.MainActivity;
import com.example.movies.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FirebaseObjectCommentManager {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference referenceLoad;
    //Mode 1 load data, 2 add data
    private int mode = 2;

    public FirebaseObjectCommentManager() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(Utils.COMMENT_FOLDER);
    }


    /**
     * Thêm một comment mới vào firebase
     */
    public void addComment(MovieObject.Movie movie, TrailerObject.Trailer trailer, Comment newComment) {
        Log.i("AAA", "ADD COMMENT CHILD : " + movie.getId() + " -- " + trailer.getId() + " -- " + newComment.getId());
        DatabaseReference reference = databaseReference.child(movie.getId()).child(trailer.getId()).child(newComment.getId());
        reference.setValue(newComment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("AAA", "ON COMPLETE");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("AAA", "ON FAIL");
            }
        });
    }

    /**
     * Xóa một comment ở firebase
     */
    public void deleteComment(MovieObject.Movie movie, TrailerObject.Trailer trailer, Comment newComment) {
        DatabaseReference reference = databaseReference.child(movie.getId()).child(trailer.getId()).child(newComment.getId());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("AAA", "ON COMPLETE");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("AAA", "ON FAIL");
            }
        });
    }


    /**
     * Update comment ở firebase
     */
    public void updateComment(MovieObject.Movie movie, TrailerObject.Trailer trailer, Comment newComment) {
        DatabaseReference reference = databaseReference.child(movie.getId()).child(trailer.getId()).child(newComment.getId());
        reference.setValue(newComment).addOnCompleteListener(new OnCompleteListener<Void>() {
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
     * Get comment của trailier
     */
    public List<Comment> getAllComment(MovieObject.Movie movie, TrailerObject.Trailer trailer) {

        List<Comment> comments = new ArrayList<>();
        referenceLoad = databaseReference.child(movie.getId()).child(trailer.getId());
        referenceLoad.orderByChild("timeComment").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String, String> mapValues = (Map<String, String>) snapshot.getValue();
                Comment comment = commentFromMap(mapValues);
                if (mode == 2) {
                    mutableLiveDataAddComment.postValue(comment);
                } else {
                    comments.add(comment);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String, String> mapValues = (Map<String, String>) snapshot.getValue();
                Comment comment = commentFromMap(mapValues);
                mutableLiveDataUpdateComment.postValue(comment);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Map<String, String> mapValues = (Map<String, String>) snapshot.getValue();
                Comment comment = commentFromMap(mapValues);
                Log.i("AAA", "ITEM DELETE : " + comment.getTextComment());
                mutableLiveDataDeleteComment.postValue(comment);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return comments;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Comment commentFromMap(Map<String, String> mapValues) {
        Comment comment = new Comment();
        comment.setId(Objects.requireNonNull(mapValues).get("id"));
        comment.setMovieID(String.valueOf(mapValues.get("movieID")));
        comment.setNameDisplay(mapValues.get("nameDisplay"));
        comment.setPathImage(mapValues.get("pathImage"));
        comment.setTextComment(mapValues.get("textComment"));
        comment.setTimeComment(mapValues.get("timeComment"));
        comment.setUserId(mapValues.get("userId"));
        comment.setTrailerID(String.valueOf(mapValues.get("trailerID")));
        return comment;
    }

    public static String getUserNameDisplay() {
        String name = MainActivity.userProfile.getFirstName().concat(" ".concat(MainActivity.userProfile.getLastName())).replace("\\s{2,}", " ");
        return name.replace("\\s{2,}", " ");
    }

    public static String getUserPathImage() {
        return userProfile.getPathImage();
    }

    public static String getUserUID() {
        return FirebaseAuth.getInstance().getUid();
    }


}
