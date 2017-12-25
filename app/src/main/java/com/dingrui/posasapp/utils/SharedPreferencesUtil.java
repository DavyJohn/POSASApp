package com.dingrui.posasapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;


/**
 * Created by come on 2015/12/3.
 */
public class SharedPreferencesUtil {

    private static SharedPreferencesUtil sharedPreferences;
    private SharedPreferences sp;
    private final String SHARED = "dingrui";

    private SharedPreferencesUtil(Context context){
        sp = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
    }
    public static SharedPreferencesUtil getInstance(Context context){
        if(sharedPreferences == null){
            synchronized (SharedPreferencesUtil.class){
                if(sharedPreferences == null){
                    sharedPreferences = new SharedPreferencesUtil(context);
                }
            }
        }
        return sharedPreferences;
    }

    public String getString(String key){
        return sp.getString(key, "");
    }

    public static void init(Context context){
        if (null == sharedPreferences){
            sharedPreferences = (SharedPreferencesUtil) PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public void putString(String key, String value){
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public void putBoolean(String key, boolean value){
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public boolean getBoolean(String key){
       return sp.getBoolean(key,false);
    }

    public Boolean getBoolean(Context context, String key){
        SharedPreferences s = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        Boolean result = s.getBoolean(key,false);
        return result;
    }

    public void putBoolean(Context context, String key, Boolean strData){
        SharedPreferences activityPreferences = context.getSharedPreferences(
                SHARED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(key, strData);
        editor.commit();
    }


}
