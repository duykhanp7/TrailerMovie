package com.example.movies.data.firebase;

import androidx.annotation.NonNull;

import com.example.movies.data.api.APIGetData;
import com.example.movies.data.model.anothers.IDMovieObject;
import com.example.movies.data.model.movie.MovieObject;
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

/**
 * Lưu thông tin của các bộ phim được thêm vào yêu thích
 */
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

    /**
     * Thêm phim vào danh sách yêu thích
     */
    public void addNewMovieIDToFavoriteFolder(MovieObject.Movie movie, String type) {
        if(firebaseUser != null){
            DatabaseReference reference = databaseReference.child(folderName);
            IDMovieObject idMovieObject = new IDMovieObject(movie.getId(), type);
            reference.child(Objects.requireNonNull(firebaseUser.getUid())).child(movie.getId()).setValue(idMovieObject);
        }
    }

    /**
     * Xóa khỏi danh sách yêu thích
     */
    public void removeMovieIDOutOfFavoriteFolder(MovieObject.Movie movie) {
        if (firebaseUser != null) {
            DatabaseReference reference = databaseReference.child(folderName).child(Objects.requireNonNull(firebaseUser.getUid())).child(movie.getId());
            reference.removeValue();
        }
    }


    /**
     * Trả về danh sách id các bộ phim yêu thích lấy từ Firebase
     */
    public Map<String, IDMovieObject> getAllIDMovieFavoritesFromFirebase() {
        Map<String, IDMovieObject> idMovieObjects = new HashMap<>();

        if (firebaseUser != null) {
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
        }

        return idMovieObjects;
    }

    /**
     * Trả về danh sách phim từ API
     */
    public List<MovieObject.Movie> getMovieFavoritesFromAPI() {
        List<MovieObject.Movie> list = new ArrayList<>();

        if (firebaseUser != null) {
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
        }
        return list;
    }

}
