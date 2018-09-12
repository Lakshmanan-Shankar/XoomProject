package com.example.android.displaylistofcountries.data;

import android.content.Context;

import com.example.android.displaylistofcountries.dagger.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Lakshmanan on 9/8/18.
 */

@Singleton
public class DataManager {

    private Context mContext;
    private SharedPrefsHelper mSharedPrefsHelper;

    @Inject
    public DataManager(@ApplicationContext Context context,
                       SharedPrefsHelper sharedPrefsHelper) {
        mContext = context;
        mSharedPrefsHelper = sharedPrefsHelper;
    }

    public void saveCountries(String countries) {
        mSharedPrefsHelper.put(SharedPrefsHelper.COUNTRIES_KEY, countries);
    }

    public String getCountries() {
        return mSharedPrefsHelper.get(SharedPrefsHelper.COUNTRIES_KEY, null);
    }
}
