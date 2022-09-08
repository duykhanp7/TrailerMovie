package com.example.movies.data.room;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.example.movies.data.model.setting.Setting;
import com.example.movies.utils.Utils;

public class DatabaseRepository {

    private static DatabaseHelper databaseHelper;

    public DatabaseRepository(Context context) {
        if (databaseHelper == null) {
            databaseHelper = Room.databaseBuilder(context, DatabaseHelper.class, Utils.SETTING_DATABASE_NAME).allowMainThreadQueries().build();
        }
    }

    public void addSetting(Setting setting) {
        databaseHelper.getDao().addSetting(setting);
    }

    public void updateSetting(Setting setting) {
        databaseHelper.getDao().updateSetting(setting.getUserID(), setting.getLanguage(), setting.getTheme());
    }

    public void deleteSetting(Setting setting) {
        databaseHelper.getDao().deleteSetting(setting.getUserID());
    }

    public String isUserExists(String uid) {
        return databaseHelper.getDao().getUserID(uid);
    }

    public void saveDefaultSetting(String userId) {
        String id = isUserExists(userId);
        Log.i("AAA", "ID USER : " + id);
        if (id == null) {
            Log.i("AAA", "USER NOT EXISTS");
            Setting setting = new Setting();
            setting.setUserID(userId);
            addSetting(setting);
        } else {
            Log.i("AAA", "USER EXISTS");
        }
    }

    public Setting getUserSetting(String uid) {
        return databaseHelper.getDao().getUserSetting(uid);
    }

}
