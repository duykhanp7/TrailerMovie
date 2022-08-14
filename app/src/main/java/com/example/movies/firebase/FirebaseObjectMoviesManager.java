package com.example.movies.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.movies.api.APIGetData;
import com.example.movies.model.anothers.IDMovieObject;
import com.example.movies.model.movie.MovieObject;
import com.example.movies.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseObjectMoviesManager {

    private final DatabaseReference databaseReference;
    private final FirebaseUser firebaseUser;
    private String folderName = "";

    public FirebaseObjectMoviesManager(String folderNameTemp) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        folderName = folderNameTemp;
    }

    public boolean firstLoadIDMovie = true;
    public boolean firstLoadMovie = true;

    public void addNewMovieIDToFavoriteFolder(MovieObject.Movie movie, String type) {
        DatabaseReference reference = databaseReference.child(folderName);
        IDMovieObject idMovieObject = new IDMovieObject(movie.getId(), type);
        reference.child(Objects.requireNonNull(firebaseUser.getUid())).child(movie.getId()).setValue(idMovieObject);
    }

    public void removeMovieIDOutOfFavoriteFolder(MovieObject.Movie movie) {
        DatabaseReference reference = databaseReference.child(folderName).child(Objects.requireNonNull(firebaseUser.getUid())).child(movie.getId());
        reference.removeValue();
    }


    public Map<String, IDMovieObject> getAllIDMovieFavoritesFromFirebase() {
        Map<String, IDMovieObject> idMovieObjects = new HashMap<>();
        DatabaseReference reference = databaseReference.child(folderName).child(Objects.requireNonNull(firebaseUser.getUid()));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firstLoadIDMovie) {
                    for (DataSnapshot a : snapshot.getChildren()) {
                        Map<String, String> mapValue = (Map<String, String>) a.getValue();
                        if (mapValue != null) {
                            String id = mapValue.get("id");
                            String type = mapValue.get("type");
                            idMovieObjects.put(id, new IDMovieObject(id, type));
                        }
                    }
                }
                firstLoadIDMovie = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return idMovieObjects;
    }


    public List<MovieObject.Movie> getMovieFavoritesFromAPI() {
        List<MovieObject.Movie> list = new ArrayList<>();
        DatabaseReference reference = databaseReference.child(folderName).child(Objects.requireNonNull(firebaseUser.getUid()));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firstLoadMovie) {
                    for (DataSnapshot a : snapshot.getChildren()) {
                        Map<String, String> mapValue = (Map<String, String>) a.getValue();
                        if (mapValue != null) {
                            String id = mapValue.get("id");
                            String type = mapValue.get("type");
                            APIGetData.apiGetData.getDetailsMovieInformation(type, id).enqueue(new Callback<MovieObject.Movie>() {
                                @Override
                                public void onResponse(Call<MovieObject.Movie> call, Response<MovieObject.Movie> response) {
                                    if (response.body() != null) {
                                        MovieObject.Movie movie = response.body();
                                        Objects.requireNonNull(movie).setTypeMovieOrTVShow(type);
                                        list.add(movie);
                                    }
                                }

                                @Override
                                public void onFailure(Call<MovieObject.Movie> call, Throwable t) {

                                }
                            });
                        }
                    }
                    firstLoadMovie = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list;
    }

}
