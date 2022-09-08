package com.example.movies.data.model.comment;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.example.movies.data.firebase.FirebaseObjectCommentManager;

import java.io.Serializable;

public class Comment extends BaseObservable implements Serializable {

    private String id = "";
    private String userId = "";
    private String textComment = "";
    private String timeComment = "";
    private String nameDisplay = "";
    private String pathImage = "";
    private String movieID = "";
    private String trailerID = "";

    public Comment() {
        this.userId = FirebaseObjectCommentManager.getUserUID();
        this.nameDisplay = FirebaseObjectCommentManager.getUserNameDisplay();
        this.pathImage = FirebaseObjectCommentManager.getUserPathImage();
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
    public String getTextComment() {
        return textComment;
    }

    public void setTextComment(String textComment) {
        this.textComment = textComment;
        this.notifyPropertyChanged(BR.textComment);
    }

    @Bindable
    public String getTimeComment() {
        return timeComment;
    }

    public void setTimeComment(String timeComment) {
        this.timeComment = timeComment;
        this.notifyPropertyChanged(BR.timeComment);
    }

    @Bindable
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.notifyPropertyChanged(BR.userId);
    }

    @Bindable
    public String getNameDisplay() {
        return nameDisplay;
    }

    public void setNameDisplay(String nameDisplay) {
        this.nameDisplay = nameDisplay;
        this.notifyPropertyChanged(BR.nameDisplay);
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
    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
        this.notifyPropertyChanged(BR.movieID);
    }

    @Bindable
    public String getTrailerID() {
        return trailerID;
    }

    public void setTrailerID(String trailerID) {
        this.trailerID = trailerID;
        this.notifyPropertyChanged(BR.trailerID);
    }
}
