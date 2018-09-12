package com.example.android.displaylistofcountries.Repo;

/**
 * Created by Lakshmanan on 9/8/18.
 */

import com.example.android.displaylistofcountries.api.CountriesResponse;
import com.example.android.displaylistofcountries.api.XoomApi;

import io.reactivex.Observable;

public class ServiceRepo {
    private final XoomApi mXoomApi;
    private Observable mCountriesObservable;

    public ServiceRepo(XoomApi xoomApi) {
        mXoomApi = xoomApi;
    }

    public Observable<CountriesResponse> getCountriesList(int pageSize) {
        if(mCountriesObservable == null) {
            mCountriesObservable = mXoomApi.getCountriesList(pageSize).cache();
        }
        return mCountriesObservable;
    }
}