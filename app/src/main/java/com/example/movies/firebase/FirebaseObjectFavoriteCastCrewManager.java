package com.example.movies.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.movies.api.APIGetData;
import com.example.movies.model.anothers.Person;
import com.example.movies.model.cast.Cast;
import com.example.movies.model.crew.Crew;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseObjectFavoriteCastCrewManager {


    private final DatabaseReference databaseReference;
    private final FirebaseUser currentUser;
    public boolean firstLoadIDCast = true;
    public boolean firstLoadIDCrew = true;
    public boolean firstLoadCastObject = true;
    public boolean firstLoadCrewObject = true;

    public FirebaseObjectFavoriteCastCrewManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    public void addCastToFavoriteFolder(Cast cast) {
        DatabaseReference reference = databaseReference.child(Utils.FIREBASE_FAVORITE_CAST_AND_CREW_FOLDER);
        Person person = new Person(cast.getId(), Utils.TYPE_CAST);
        reference.child(currentUser.getUid()).child(Utils.FIREBASE_CAST_FOLDER).child(cast.getId()).setValue(person);
    }

    public void addCrewToFavoriteFolder(Crew crew) {
        DatabaseReference reference = databaseReference.child(Utils.FIREBASE_FAVORITE_CAST_AND_CREW_FOLDER);
        Person person = new Person(crew.getId(), Utils.TYPE_CREW);
        reference.child(currentUser.getUid()).child(Utils.FIREBASE_CREW_FOLDER).child(crew.getId()).setValue(person);
    }


    public void removeCastOutOfFavoriteFolder(Cast cast) {
        DatabaseReference reference = databaseReference.child(Utils.FIREBASE_FAVORITE_CAST_AND_CREW_FOLDER);
        reference.child(currentUser.getUid()).child(Utils.FIREBASE_CAST_FOLDER).child(cast.getId()).removeValue();
    }

    public void removeCrewOutOfFavoriteFolder(Crew crew) {
        DatabaseReference reference = databaseReference.child(Utils.FIREBASE_FAVORITE_CAST_AND_CREW_FOLDER);
        reference.child(currentUser.getUid()).child(Utils.FIREBASE_CREW_FOLDER).child(crew.getId()).removeValue();
    }


    public Map<String, Person> getAllIDCastFavoritesFromFirebase() {
        Map<String, Person> personMap = new HashMap<>();
        DatabaseReference reference = databaseReference.child(Utils.FIREBASE_FAVORITE_CAST_AND_CREW_FOLDER)
                .child(currentUser.getUid()).child(Utils.FIREBASE_CAST_FOLDER);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firstLoadIDCast) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        Map<String, String> mapValue = (Map<String, String>) item.getValue();
                        if (mapValue != null) {
                            String id = mapValue.get("id");
                            String type = mapValue.get("type");
                            Person person = new Person(id, type);
                            personMap.put(id, person);
                        }
                    }
                    firstLoadIDCast = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return personMap;
    }

    public Map<String, Person> getAllIDCrewFavoritesFromFirebase() {
        Map<String, Person> personMap = new HashMap<>();
        DatabaseReference reference = databaseReference.child(Utils.FIREBASE_FAVORITE_CAST_AND_CREW_FOLDER)
                .child(currentUser.getUid()).child(Utils.FIREBASE_CREW_FOLDER);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firstLoadIDCrew) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        Map<String, String> mapValue = (Map<String, String>) item.getValue();
                        if (mapValue != null) {
                            String id = mapValue.get("id");
                            String type = mapValue.get("type");
                            Person person = new Person(id, type);
                            personMap.put(id, person);
                        }
                    }
                    firstLoadIDCrew = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return personMap;
    }

    public List<Cast> getAllCastFromAPI() {
        List<Cast> casts = new ArrayList<>();
        DatabaseReference reference = databaseReference.child(Utils.FIREBASE_FAVORITE_CAST_AND_CREW_FOLDER)
                .child(currentUser.getUid()).child(Utils.FAVORITE_CASTS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firstLoadCastObject) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        Map<String, String> mapValue = (Map<String, String>) item.getValue();
                        if (mapValue != null) {
                            String id = mapValue.get("id");
                            APIGetData.apiGetData.getCastDetails(id).enqueue(new Callback<Cast>() {
                                @Override
                                public void onResponse(Call<Cast> call, Response<Cast> response) {
                                    if (response.body() != null) {
                                        casts.add(response.body());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Cast> call, Throwable t) {

                                }
                            });
                        }
                    }
                    firstLoadCastObject = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return casts;
    }


    public List<Crew> getAllCrewFromAPI() {
        List<Crew> crews = new ArrayList<>();
        DatabaseReference reference = databaseReference.child(Utils.FIREBASE_FAVORITE_CAST_AND_CREW_FOLDER)
                .child(currentUser.getUid()).child(Utils.FAVORITE_CREWS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firstLoadCrewObject) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        Map<String, String> mapValue = (Map<String, String>) item.getValue();
                        if (mapValue != null) {
                            String id = mapValue.get("id");
                            APIGetData.apiGetData.getCrewDetails(id).enqueue(new Callback<Crew>() {
                                @Override
                                public void onResponse(Call<Crew> call, Response<Crew> response) {
                                    if (response.body() != null) {
                                        crews.add(response.body());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Crew> call, Throwable t) {

                                }
                            });
                        }
                    }
                    firstLoadCrewObject = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return crews;
    }

}
