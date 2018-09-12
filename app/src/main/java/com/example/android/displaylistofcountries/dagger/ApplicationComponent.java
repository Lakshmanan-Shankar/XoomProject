package com.example.android.displaylistofcountries.dagger;

import android.app.Application;
import android.content.Context;

import com.example.android.displaylistofcountries.Repo.ServiceRepo;
import com.example.android.displaylistofcountries.api.XoomApi;
import com.example.android.displaylistofcountries.data.DataManager;
import com.example.android.displaylistofcountries.data.SharedPrefsHelper;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Lakshmanan on 9/8/18.
 */

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {

    void inject(DisplayListOfCountriesApplication displayListOfCountriesApplication);

    @ApplicationContext
    Context getContext();

    Application getApplication();

    DataManager getDataManager();

    SharedPrefsHelper getPreferenceHelper();

    Retrofit provideRetrofit();

    XoomApi providesNetworkService();

    ServiceRepo providesService();
}