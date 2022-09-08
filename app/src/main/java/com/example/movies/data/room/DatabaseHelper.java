package com.example.movies.data.room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.movies.data.model.setting.Setting;

@Database(entities = {Setting.class}, version = 1)
public abstract class DatabaseHelper extends RoomDatabase {
    public abstract DatabaseDao getDao();
}
