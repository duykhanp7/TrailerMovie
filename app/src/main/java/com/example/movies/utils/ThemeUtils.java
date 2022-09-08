package com.example.movies.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.movies.R;

import es.dmoral.toasty.Toasty;


public class ThemeUtils {

    public static String theme = "theme";

    /**
     * Kiểm tra xem đang ở chế độ sáng hay tối
     * Trả về 2 nếu đang là tối, 1 nếu là sáng*/
    public static int getCurrentTheme(){
        return AppCompatDelegate.getDefaultNightMode();
    }

    /**
     * Set chế độ sáng tối
     * */
    public static void setTheme(int mode, Context context){
        try {
            AppCompatDelegate.setDefaultNightMode(mode);
        }
        catch (Exception e){
            Toasty.error(context, R.string.change_theme_info,Toasty.LENGTH_SHORT,true).show();
            e.printStackTrace();
        }
    }

}
