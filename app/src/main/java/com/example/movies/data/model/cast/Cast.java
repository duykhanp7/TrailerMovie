package com.example.movies.data.model.cast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.example.movies.data.model.anothers.ImageObject;
import com.example.movies.data.model.movie.MovieCredits;
import com.example.movies.data.model.socials.Social;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Cast extends BaseObservable implements Serializable {


    @SerializedName("adult") boolean adult;
    //    0 - Not specified
    //    1 - Female
    //    2 - Male
    @SerializedName("gender") String gender;
    @SerializedName("id") String id;
    @SerializedName("known_for_department") String known_for_department;
    @SerializedName("name") String name;
    @SerializedName("original_name") String original_name;
    @SerializedName("popularity") String popularity;
    @SerializedName("profile_path") String profile_path;
    @SerializedName("cast_id") String cast_id;
    @SerializedName("character") String character;
    @SerializedName("credit_id") String credit_id;
    @SerializedName("order") String order;

    @SerializedName("also_known_as")
    List<String> also_known_as;
    @SerializedName("biography") String biography;
    @SerializedName("birthday") String birthday;
    @SerializedName("deathday") String deathday;
    @SerializedName("place_of_birth") String place_of_birth;
    @SerializedName("movie_credits")
    MovieCredits movie_credits;
    //    @SerializedName("tv_credits") String tv_credits;
    @SerializedName("external_ids")
    Social external_ids;
    @SerializedName("images")
    ImageObject images;

    public Cast(){}

    @Bindable
    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
        this.notifyPropertyChanged(BR.adult);
    }

    @Bindable
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        this.notifyPropertyChanged(BR.gender);
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
    public String getKnown_for_department() {
        return known_for_department;
    }

    public void setKnown_for_department(String known_for_department) {
        this.known_for_department = known_for_department;
        this.notifyPropertyChanged(BR.known_for_department);
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
    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
        this.notifyPropertyChanged(BR.original_name);
    }

    @Bindable
    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
        this.notifyPropertyChanged(BR.popularity);
    }

    @Bindable
    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
        this.notifyPropertyChanged(BR.profile_path);
    }

    @Bindable
    public String getCast_id() {
        return cast_id;
    }

    public void setCast_id(String cast_id) {
        this.cast_id = cast_id;
        this.notifyPropertyChanged(BR.cast_id);
    }

    @Bindable
    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
        this.notifyPropertyChanged(BR.character);
    }

    @Bindable
    public String getCredit_id() {
        return credit_id;
    }

    public void setCredit_id(String credit_id) {
        this.credit_id = credit_id;
        this.notifyPropertyChanged(BR.credit_id);
    }

    @Bindable
    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
        this.notifyPropertyChanged(BR.order);
    }

    @Bindable
    public List<String> getAlso_known_as() {
        return also_known_as;
    }

    public void setAlso_known_as(List<String> also_known_as) {
        this.also_known_as = also_known_as;
        this.notifyPropertyChanged(BR.also_known_as);
    }

    @Bindable
    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
        this.notifyPropertyChanged(BR.biography);
    }

    @Bindable
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
        this.notifyPropertyChanged(BR.birthday);
    }

    @Bindable
    public String getDeathday() {
        return deathday;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
        this.notifyPropertyChanged(BR.deathday);
    }

    @Bindable
    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth = place_of_birth;
        this.notifyPropertyChanged(BR.place_of_birth);
    }

    @Bindable
    public MovieCredits getMovie_credits() {
        return movie_credits;
    }

    public void setMovie_credits(MovieCredits movie_credits) {
        this.movie_credits = movie_credits;
        this.notifyPropertyChanged(BR.movie_credits);
    }

    @Bindable
    public Social getExternal_ids() {
        return external_ids;
    }

    public void setExternal_ids(Social external_ids) {
        this.external_ids = external_ids;
        this.notifyPropertyChanged(BR.external_ids);
    }

    @Bindable
    public ImageObject getImages() {
        return images;
    }

    public void setImages(ImageObject images) {
        this.images = images;
        this.notifyPropertyChanged(BR.images);
    }
}
