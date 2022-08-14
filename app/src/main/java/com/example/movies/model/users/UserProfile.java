package com.example.movies.model.users;

import android.accounts.Account;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.movies.BR;
import com.example.movies.model.movie.MovieObject;

import java.io.Serializable;
import java.util.Base64;
import java.util.List;

public class UserProfile extends BaseObservable implements Serializable {

    private String firstName, lastName, email, password, phoneNumber, pathImage, birthdate;
//    private List<MovieObject.Movie> favoriteMovies;
//    private AccountUser account;

    public UserProfile(){
        this.birthdate = "";
    }

    public UserProfile(String firstName, String lastName, String email, String password, String phoneNumber, String pathImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.pathImage = pathImage;
        this.birthdate = "";
    }

    @Bindable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.notifyPropertyChanged(BR.firstName);
    }

    @Bindable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.notifyPropertyChanged(BR.lastName);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.notifyPropertyChanged(BR.phoneNumber);
    }

    @Bindable
    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
        this.notifyPropertyChanged(BR.pathImage);
    }

    @Bindable
    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
        this.notifyPropertyChanged(BR.birthdate);
    }
}
