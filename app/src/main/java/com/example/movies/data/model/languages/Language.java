package com.example.movies.data.model.languages;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Language extends BaseObservable implements Serializable {


    @SerializedName("iso_639_1") String iso_639_1;
    @SerializedName("english_name") String english_name;
    @SerializedName("name") String name;


    public Language(){}

    @Bindable
    public String getIso_639_1() {
        return iso_639_1;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
        this.notifyPropertyChanged(BR.iso_639_1);
    }

    @Bindable
    public String getEnglish_name() {
        return english_name;
    }

    public void setEnglish_name(String english_name) {
        this.english_name = english_name;
        this.notifyPropertyChanged(BR.english_name);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.notifyPropertyChanged(BR.name);
    }
}
