package com.example.android.displaylistofcountries.dagger;

import android.app.Application;
import android.content.Context;

import com.example.android.displaylistofcountries.BuildConfig;
import com.facebook.stetho.Stetho;

/**
 * Created by Lakshmanan on 9/8/18.
 */

public class DisplayListOfCountriesApplication extends Application {

    protected ApplicationComponent applicationComponent;

    public static DisplayListOfCountriesApplication get(Context context) {
        return (DisplayListOfCountriesApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();
        applicationComponent.inject(this);
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    public ApplicationComponent getComponent(){
        return applicationComponent;
    }
}