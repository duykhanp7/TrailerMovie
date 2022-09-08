package com.example.movies.data.model.setting;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.movies.BR;
import com.example.movies.utils.Utils;

import java.io.Serializable;

@Entity(tableName = Utils.SETTING_TABLE_NAME)
public class Setting extends BaseObservable implements Serializable {

    @NonNull
    @PrimaryKey
    String userID;
    String language;
    //1 mode light, 2 mode night
    int theme;

    public Setting() {
        language = "en";
        theme = 1;
    }

    @Bindable
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
        this.notifyPropertyChanged(BR.userID);
    }

    @Bindable
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        this.notifyPropertyChanged(BR.language);
    }

    @Bindable
    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
        this.notifyPropertyChanged(BR.theme);
    }
}
