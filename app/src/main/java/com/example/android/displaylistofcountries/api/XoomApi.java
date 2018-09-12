package com.example.android.displaylistofcountries.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Lakshmanan on 9/8/18.
 */

public interface XoomApi {

    @GET("countries")
    Observable<CountriesResponse> getCountriesList(@Query("page_size") int pageSize);
}
