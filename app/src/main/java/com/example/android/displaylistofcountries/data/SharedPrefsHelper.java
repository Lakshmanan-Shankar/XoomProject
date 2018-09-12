package com.example.android.displaylistofcountries.data;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Lakshmanan on 9/8/18.
 */

@Singleton
public class SharedPrefsHelper {

    public static String COUNTRIES_KEY = "countries_key";

    private SharedPreferences mSharedPreferences;

    @Inject
    public SharedPrefsHelper(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public String get(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void put(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }
}