package com.example.movies.data.model.users;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;

import java.io.Serializable;
import java.util.Map;

public class UserProfile extends BaseObservable implements Serializable {

    private String uid, firstName, lastName, email, password, phoneNumber, pathImage, birthdate;
//    private List<MovieObject.Movie> favoriteMovies;
//    private AccountUser account;

    public UserProfile() {
        this.birthdate = "";
    }

    public UserProfile(String uid, String firstName, String lastName, String email, String password, String phoneNumber, String pathImage) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.pathImage = pathImage;
        this.birthdate = "";
    }

    @Bindable
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
        this.notifyPropertyChanged(BR.uid);
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

    public static UserProfile fromMap(Map<String,String> map){
        UserProfile userProfile = new UserProfile();
        userProfile.setUid(map.get("uid"));
        userProfile.setFirstName(map.get("firstName"));
        userProfile.setLastName(map.get("lastName"));
        userProfile.setEmail(map.get("email"));
        userProfile.setPassword(map.get("password"));
        userProfile.setPhoneNumber(map.get("phoneNumber"));
        userProfile.setPathImage(map.get("pathImage"));
        userProfile.setBirthdate(map.get("birthdate"));
        return userProfile;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "uid='" + uid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", pathImage='" + pathImage + '\'' +
                ", birthdate='" + birthdate + '\'' +
                '}';
    }
}
