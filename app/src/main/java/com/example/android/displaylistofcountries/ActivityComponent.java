package com.example.android.displaylistofcountries;

import com.example.android.displaylistofcountries.dagger.ActivityScope;
import com.example.android.displaylistofcountries.dagger.ApplicationComponent;

import dagger.Component;

/**
 * Created by Lakshmanan on 9/8/18.
 */

@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class})
public interface ActivityComponent {

    void inject(DisplayCountriesActivity mainActivity);
}