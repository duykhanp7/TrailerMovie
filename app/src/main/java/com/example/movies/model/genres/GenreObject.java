package com.example.movies.model.genres;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.List;

public class GenreObject extends BaseObservable implements Serializable{

    @SerializedName("genres")
    List<Genre> genres;

    public GenreObject(){}

    @Bindable
    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
        this.notifyPropertyChanged(BR.genres);
    }

    public static class Genre extends BaseObservable implements Serializable{
        @SerializedName("id")
        private String idGenre;
        @SerializedName("name")
        private String nameGenre;

        public Genre() {}

        @Bindable
        public String getIdGenre() {
            return idGenre;
        }

        public void setIdGenre(String idGenre) {
            this.idGenre = idGenre;
            this.notifyPropertyChanged(BR.idGenre);
        }

        @Bindable
        public String getNameGenre() {
            return nameGenre;
        }

        public void setNameGenre(String nameGenre) {
            this.nameGenre = nameGenre;
            this.notifyPropertyChanged(BR.nameGenre);
        }
    }
}
