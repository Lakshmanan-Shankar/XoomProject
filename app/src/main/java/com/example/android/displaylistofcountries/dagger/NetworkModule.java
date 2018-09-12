package com.example.android.displaylistofcountries.dagger;

import com.example.android.displaylistofcountries.Repo.ServiceRepo;
import com.example.android.displaylistofcountries.api.XoomApi;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Lakshmanan on 9/8/18.
 */

@Module
public class NetworkModule {

    public NetworkModule() {
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
        return client;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://mobile.xoom.com/catalog/v2/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public XoomApi providesNetworkService(
            Retrofit retrofit) {
        return retrofit.create(XoomApi.class);
    }

    @Provides
    @Singleton
    public ServiceRepo providesService(
            XoomApi xoomApi) {
        return new ServiceRepo(xoomApi);
    }
}
