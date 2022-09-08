package com.example.movies.data.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.movies.data.model.setting.Setting;

@Dao
public interface DatabaseDao {

    @Insert
    void addSetting(Setting setting);

    @Query("UPDATE setting_table SET language =:newLanguage , theme =:newTheme WHERE userID=:id")
    void updateSetting(String id, String newLanguage, int newTheme);

    @Query("DELETE FROM setting_table WHERE userID=:id")
    void deleteSetting(String id);

    @Query("SELECT userID FROM setting_table WHERE userID =:uid ")
    String getUserID(String uid);

    @Query("SELECT * FROM setting_table WHERE userID =:uid")
    Setting getUserSetting(String uid);

}
