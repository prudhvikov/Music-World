package com.prudhvi.musicworld.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefs {

       public SharedPreferences sharedPreferences;

       public SharedPrefs(Context context) {
              sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
       }

       public void writeSharedPrefs(String key, int value) {
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putInt(key, value);
              editor.apply();
       }

       public void writeSharedPrefs(String key, String value) {
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putString(key, value);
              editor.apply();
       }

       public int readSharedPrefsInt( String key, int defaultValue) {
              return sharedPreferences.getInt(key, defaultValue);
       }

       public String  readSharedPrefsString( String key, String defaultValue) {
              return sharedPreferences.getString(key, defaultValue);
       }

       public boolean readSharedPrefBoolean( String key, boolean defaultValue) {
              return sharedPreferences.getBoolean(key, defaultValue);
       }

       public void  writeSharedPrefBoolean( String key, boolean defaultValue) {
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putBoolean(key, defaultValue);
              editor.apply();
       }

} // shared prefs ending
