package com.example.movies.model.anothers;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateBy extends BaseObservable implements Serializable {

    @SerializedName("id") private String idCreateBy;
    @SerializedName("name") private String name = "";
    @SerializedName("profile_path") private String profile_path;

    @Bindable
    public String getIdCreateBy() {
        return idCreateBy;
    }

    public void setIdCreateBy(String idCreateBy) {
        this.idCreateBy = idCreateBy;
        this.notifyPropertyChanged(BR.idCreateBy);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
        this.notifyPropertyChanged(BR.profile_path);
    }
}
