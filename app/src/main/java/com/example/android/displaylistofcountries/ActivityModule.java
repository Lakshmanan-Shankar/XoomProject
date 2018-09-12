package com.example.android.displaylistofcountries;

import com.example.android.displaylistofcountries.Repo.ServiceRepo;
import com.example.android.displaylistofcountries.data.DataManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Lakshmanan on 9/8/18.
 */

@Module
public class ActivityModule {

    public ActivityModule() {}

    @Provides
    DisplayCountriesPresenter.PresenterFactory provideDisplayCountriesPresenterFactory(ServiceRepo serviceRepo, DataManager dataManager) {
        return new DisplayCountriesPresenter.PresenterFactory(serviceRepo, dataManager);
    }
}