package com.example.movies.model.videos;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrailerObject extends BaseObservable implements Serializable{

    @SerializedName("results")
    private List<Trailer> trailers;


    public TrailerObject(){
        trailers = new ArrayList<>();
    }

    @Bindable
    public List<Trailer> getTrailersList() {
        return trailers;
    }

    public void setTrailersList(List<Trailer> trailers) {
        this.trailers = trailers;
        this.notifyPropertyChanged(BR.trailersList);
    }

    public static class Trailer extends BaseObservable implements Serializable {


        @SerializedName("iso_639_1") private String iso_639_1;
        @SerializedName("iso_3166_1") private String iso_3166_1;
        @SerializedName("name") private String name;
        @SerializedName("key") private String key;
        @SerializedName("site") private String site;
        @SerializedName("size") private String size;
        @SerializedName("type") private String type;
        @SerializedName("official") private String official;
        @SerializedName("published_at") private String published_at;
        @SerializedName("id") private String id;


        public Trailer(){}

        @Bindable
        public String getIso_639_1() {
            return iso_639_1;
        }


        public void setIso_639_1(String iso_639_1) {
            this.iso_639_1 = iso_639_1;
            this.notifyPropertyChanged(BR.iso_639_1);
        }

        @Bindable
        public String getIso_3166_1() {
            return iso_3166_1;
        }

        public void setIso_3166_1(String iso_3166_1) {
            this.iso_3166_1 = iso_3166_1;
            this.notifyPropertyChanged(BR.iso_3166_1);
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
        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
            this.notifyPropertyChanged(BR.site);
        }

        @Bindable
        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
            this.notifyPropertyChanged(BR.size);
        }

        @Bindable
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
            this.notifyPropertyChanged(BR.type);
        }

        @Bindable
        public String getOfficial() {
            return official;
        }

        public void setOfficial(String official) {
            this.official = official;
            this.notifyPropertyChanged(BR.official);
        }

        @Bindable
        public String getPublished_at() {
            return published_at;
        }

        public void setPublished_at(String published_at) {
            this.published_at = published_at;
            this.notifyPropertyChanged(BR.published_at);
        }

        @Bindable
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
            this.notifyPropertyChanged(BR.id);
        }

        @Bindable
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
            this.notifyPropertyChanged(BR.key);
        }


    }
}
