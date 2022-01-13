package com.example.madpropertypal.util;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {
    private static String PREF_NAME = "PropertyPreferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String KEY_USER_ID = "KEY_USER_ID";
    private String KEY_FIRST_TIME = "KEY_FIRST_TIME";

    public MyPreferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, 0);
        editor = preferences.edit();
    }

    /*-------------------- Save Current Logged in User Id ----------------------*/
    public void saveUserId(int value) {
        editor.putInt(KEY_USER_ID, value).commit();
    }

    public int getSavedUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }

    public void setFirstTime(boolean value) {
        editor.putBoolean(KEY_FIRST_TIME, value).commit();
    }

    public boolean getIsAppFirstTime() {
        return preferences.getBoolean(KEY_FIRST_TIME, true);
    }

    /*-------------------- Clear All Preferences ----------------------*/
    public void clearAll() {
        preferences.edit().clear().apply();
    }
}
