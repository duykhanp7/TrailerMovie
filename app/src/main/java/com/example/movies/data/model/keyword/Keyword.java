package com.example.movies.data.model.keyword;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;

import com.example.movies.BR;
import com.example.movies.utils.Utils;

import java.io.Serializable;
import java.security.Key;
import java.util.Map;

public class Keyword extends BaseObservable implements Serializable {

    String keyword;
    String type;

    public Keyword() {
        this.keyword = "";
        this.type = Utils.TYPE_MOVIE;
    }

    public Keyword(String key, String type) {
        this.keyword = key;
        this.type = type;
    }

    public Keyword(String key) {
        this.keyword = key;
    }

    @Bindable
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
        this.notifyPropertyChanged(BR.keyword);
    }

    @Bindable
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.notifyPropertyChanged(BR.type);
    }

    public static Keyword fromMap(Map<String, String> map) {
        String key = map.get("keyword");
        String type = map.get("type");
        return new Keyword(key, type);
    }

    public String getKeywordString() {
        return this.keyword.concat(this.type);
    }

}
