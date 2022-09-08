package com.example.movies.data.shared;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String fileName = "data";
    private static final Integer mode = Context.MODE_PRIVATE;
    private Context context;

    private static SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        this.context = context;
    }

    public static SharedPreferencesHelper with(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(fileName, mode);
        }
        return new SharedPreferencesHelper(context);
    }

    @SuppressLint("ApplySharedPref")
    public synchronized void saveRemember(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public synchronized Boolean getRemember(String key){
        return sharedPreferences.getBoolean(key,false);
    }


    @SuppressLint("ApplySharedPref")
    public synchronized void saveTheme(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }

    public synchronized int getTheme(String key){
        return sharedPreferences.getInt(key,1);
    }

    @SuppressLint("ApplySharedPref")
    public void saveLanguage(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public String getLanguage(String key){
        return sharedPreferences.getString(key,"us");
    }

}
