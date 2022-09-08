package com.example.movies.data.model.socials;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Social extends BaseObservable implements Serializable {

    //"imdb_id",
    //"facebook_id",
    //"instagram_id",
    //"twitter_id"
    @SerializedName("imdb_id") String imdb_id;
    @SerializedName("facebook_id") String facebook_id;
    @SerializedName("instagram_id") String instagram_id;
    @SerializedName("twitter_id") String twitter_id;

    public Social(){}

    @Bindable
    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
        this.notifyPropertyChanged(BR.imdb_id);
    }

    @Bindable
    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
        this.notifyPropertyChanged(BR.facebook_id);
    }

    @Bindable
    public String getInstagram_id() {
        return instagram_id;
    }

    public void setInstagram_id(String instagram_id) {
        this.instagram_id = instagram_id;
        this.notifyPropertyChanged(BR.instagram_id);
    }

    @Bindable
    public String getTwitter_id() {
        return twitter_id;
    }

    public void setTwitter_id(String twitter_id) {
        this.twitter_id = twitter_id;
        this.notifyPropertyChanged(BR.twitter_id);
    }
}
