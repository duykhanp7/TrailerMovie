package com.example.movies.data.model.movie;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MovieCredits extends BaseObservable implements Serializable {

    @SerializedName("cast") List<MovieObject.Movie> casts;
    @SerializedName("crew") List<MovieObject.Movie> crews;


    public MovieCredits(){
        casts = new ArrayList<>();
        crews = new ArrayList<>();
    }

    @Bindable
    public List<MovieObject.Movie> getCasts() {
        return casts;
    }

    public void setCasts(List<MovieObject.Movie> casts) {
        this.casts = casts;
        this.notifyPropertyChanged(BR.casts);
    }

    @Bindable
    public List<MovieObject.Movie> getCrews() {
        return crews;
    }

    public void setCrews(List<MovieObject.Movie> crews) {
        this.crews = crews;
        this.notifyPropertyChanged(BR.crews);
    }
}
