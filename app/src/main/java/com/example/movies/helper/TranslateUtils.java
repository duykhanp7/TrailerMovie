package com.example.movies.helper;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.movies.data.model.cast.Cast;
import com.example.movies.data.model.crew.Crew;
import com.example.movies.data.model.movie.MovieObject;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class TranslateUtils {

    private static Translator translator;
    public static MutableLiveData<MovieObject.Movie> translateLiveDataMovie = new MutableLiveData<>();
    public static MutableLiveData<Cast> translateLiveDataCast = new MutableLiveData<>();
    public static MutableLiveData<Crew> translateLiveDataCrew = new MutableLiveData<>();
    private static MovieObject.Movie movie;
    private static Cast cast;
    private static Crew crew;

    public static void createTranslateOption(String src, String des) {
        TranslatorOptions translatorOptions = new TranslatorOptions.Builder().setSourceLanguage(src).setTargetLanguage(des).build();
        translator = Translation.getClient(translatorOptions);
    }

    public static void downloadEnglishModel() {
        DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public static void translate(String text, int mode) {
        translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (mode == 1) {
                    movie.setOverview(s);
                    translateLiveDataMovie.postValue(movie);
                } else if (mode == 2) {
                    cast.setBiography(s);
                    translateLiveDataCast.postValue(cast);
                } else if (mode == 3) {
                    crew.setBiography(s);
                    translateLiveDataCrew.postValue(crew);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //translator.close();
            }
        });
    }

    public static void setMovie(MovieObject.Movie a) {
        movie = a;
    }

    public static Cast getCast() {
        return cast;
    }

    public static void setCast(Cast cast) {
        TranslateUtils.cast = cast;
    }

    public static Crew getCrew() {
        return crew;
    }

    public static void setCrew(Crew crew) {
        TranslateUtils.crew = crew;
    }
}
