package com.example.movies.data.model.anothers;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageObject extends BaseObservable implements Serializable {


    @SerializedName("profiles") List<Image> images;

    public ImageObject(){images = new ArrayList<>();}

    @Bindable
    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
        this.notifyPropertyChanged(BR.images);
    }

    public static class Image extends BaseObservable implements Serializable {

        @SerializedName("aspect_ratio") String aspect_ratio;
        @SerializedName("height") String height;
        @SerializedName("iso_639_1") String iso_639_1;
        @SerializedName("file_path") String file_path;
        @SerializedName("vote_average") String vote_average;
        @SerializedName("vote_count") String vote_count;
        @SerializedName("width") String width;

        public Image(){}

        @Bindable
        public String getAspect_ratio() {
            return aspect_ratio;
        }

        public void setAspect_ratio(String aspect_ratio) {
            this.aspect_ratio = aspect_ratio;
            this.notifyPropertyChanged(BR.aspect_ratio);
        }

        @Bindable
        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
            this.notifyPropertyChanged(BR.height);
        }

        @Bindable
        public String getIso_639_1() {
            return iso_639_1;
        }

        public void setIso_639_1(String iso_639_1) {
            this.iso_639_1 = iso_639_1;
            this.notifyPropertyChanged(BR.iso_639_1);
        }

        @Bindable
        public String getFile_path() {
            return file_path;
        }

        public void setFile_path(String file_path) {
            this.file_path = file_path;
            this.notifyPropertyChanged(BR.file_path);
        }

        @Bindable
        public String getVote_average() {
            return vote_average;
        }

        public void setVote_average(String vote_average) {
            this.vote_average = vote_average;
            this.notifyPropertyChanged(BR.vote_average);
        }

        @Bindable
        public String getVote_count() {
            return vote_count;
        }

        public void setVote_count(String vote_count) {
            this.vote_count = vote_count;
            this.notifyPropertyChanged(BR.vote_count);
        }

        @Bindable
        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
            this.notifyPropertyChanged(BR.width);
        }
    }
}
