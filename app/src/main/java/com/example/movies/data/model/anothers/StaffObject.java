package com.example.movies.data.model.anothers;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.example.movies.data.model.cast.Cast;
import com.example.movies.data.model.crew.Crew;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StaffObject extends BaseObservable implements Serializable {

    @SerializedName("cast") List<Cast> casts;
    @SerializedName("crew") List<Crew> crews;

    public StaffObject(){}

    @Bindable
    public List<Cast> getCasts() {
        return casts;
    }

    public void setCasts(List<Cast> cast) {
        this.casts = cast;
        this.notifyPropertyChanged(BR.casts);
    }

    @Bindable
    public List<Crew> getCrews() {
        return crews;
    }

    public void setCrews(List<Crew> crews) {
        this.crews = crews;
        this.notifyPropertyChanged(BR.crews);
    }
}
