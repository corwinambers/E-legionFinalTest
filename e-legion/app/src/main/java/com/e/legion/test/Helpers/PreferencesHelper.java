package com.e.legion.test.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private Context mContext;
    private String APP_SP;

    public PreferencesHelper(Context _context) {
        mContext = _context;
        APP_SP = mContext.getPackageName();
    }



    public void setString(String key, String value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();

        sharedPrefEdit.putString(key, value);
        sharedPrefEdit.apply();
    }

    public void setInt(String key, int value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();

        sharedPrefEdit.putInt(key, value);
        sharedPrefEdit.apply();
    }

    public void setLong(String key, long value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();

        sharedPrefEdit.putLong(key, value);
        sharedPrefEdit.apply();
    }

    public void setFloat(String key, float value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();

        sharedPrefEdit.putFloat(key, value);
        sharedPrefEdit.apply();
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();

        sharedPrefEdit.putBoolean(key, value);
        sharedPrefEdit.apply();
    }

    public String getString(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        return sharedPref.getString(key, "");
    }

    public String getString(String key, String defValue) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        String value = sharedPref.getString(key, defValue);

        return value;
    }

    public int getInt(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        int value = sharedPref.getInt(key, 0);

        return value;
    }

    public int getInt(String key, int defValue) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        int value = sharedPref.getInt(key, defValue);

        return value;
    }

    public long getLong(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        long value = sharedPref.getLong(key, 0);

        return value;
    }

    public long getLong(String key, long defValue) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        long value = sharedPref.getLong(key, defValue);

        return value;
    }

    public float getFloat(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        float value = sharedPref.getFloat(key, 0);

        return value;
    }

    public float getFloat(String key, float defValue) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        float value = sharedPref.getFloat(key, defValue);

        return value;
    }

    public boolean getBoolean(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        boolean value = sharedPref.getBoolean(key, false);

        return value;
    }

    public boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_SP, 0);
        boolean value = sharedPref.getBoolean(key, defValue);

        return value;
    }
}