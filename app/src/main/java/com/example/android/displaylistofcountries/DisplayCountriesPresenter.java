package com.example.android.displaylistofcountries;

import android.text.TextUtils;

import com.example.android.displaylistofcountries.Repo.ServiceRepo;
import com.example.android.displaylistofcountries.api.CountriesResponse;
import com.example.android.displaylistofcountries.api.CountryDetail;
import com.example.android.displaylistofcountries.api.DisbursementOption;
import com.example.android.displaylistofcountries.data.DataManager;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lakshmanan on 9/10/18.
 */

public class DisplayCountriesPresenter implements DisplayCountriesContract.Presenter {

    private static final String ACTIVE = "active";
    private static final int PAGE_SIZE = 100;
    private DataManager mDataManager;
    private ServiceRepo mServiceRepo;
    private DisplayCountriesContract.View mView;

    public DisplayCountriesPresenter(ServiceRepo serviceRepo, DataManager dataManager) {
        mDataManager = dataManager;
        mServiceRepo = serviceRepo;
    }

    @Override
    public void attachView(DisplayCountriesContract.View view) {
        mView = view;
        getCountriesList();
    }

    @Override
    public void countryFavoriteButtonClicked(Country clickedCountry) {
        if (clickedCountry.getIsFavorite()) {
            clickedCountry.setIsFavorite(false);
            removeFromFavorites(clickedCountry.getCountryCode());
        } else {
            clickedCountry.setIsFavorite(true);
            addToFavorites(clickedCountry.getCountryCode());
        }
    }

    private void getCountriesList() {
        mView.showLoading();

        mServiceRepo.getCountriesList(PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CountriesResponse>() {
                    @Override
                    public void onNext(CountriesResponse countriesResponse) {
                        if (countriesResponse != null) {
                            if (countriesResponse.getItems() != null && countriesResponse.getItems().size() > 0) {
                                Country country;
                                HashMap<String, Country> countriesMap = new HashMap<>();
                                for (CountryDetail countryDetail : countriesResponse.getItems()) {
                                    if (countryDetail.getDisbursementOptions() != null && countryDetail.getDisbursementOptions().size() > 0) {
                                        if (isAtLeastOneDisbursementModeActive(countryDetail.getDisbursementOptions())) {
                                            country = new Country(countryDetail.getCode(), countryDetail.getName(), false);
                                            countriesMap.put(country.getCountryCode(), country);
                                        }
                                    }
                                }

                                //setting favorites
                                String countriesCommaSeparatedFromPref = mDataManager.getCountries();
                                String[] countriesArrayFromPref = StringUtils.split(countriesCommaSeparatedFromPref, ",");

                                if (countriesArrayFromPref != null && countriesArrayFromPref.length > 0) {
                                    for (String countryFromPref : countriesArrayFromPref) {
                                        if (countriesMap.containsKey(countryFromPref)) {
                                            countriesMap.get(countryFromPref).setIsFavorite(true);
                                        }
                                    }
                                }

                                Collection<Country> countriesCollection = countriesMap.values();
                                List<Country> countries = new ArrayList<>(countriesCollection);
                                Collections.sort(countries);

                                mView.populateList(countries);
                                mView.registerToFavoriteClickEvents();
                            }
                        } else {
                            mView.showErrorMessage();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showErrorMessage();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        mView.hideLoading();
                    }
                });
    }

    private void addToFavorites(String countryCode) {
        String countriesCommaSeparatedFromPref = mDataManager.getCountries();
        String[] countriesArrayFromPref = StringUtils.split(countriesCommaSeparatedFromPref, ",");
        ArrayList<String> countriesList;
        if (countriesArrayFromPref != null && countriesArrayFromPref.length > 0) {
            countriesList = new ArrayList<>(Arrays.asList(countriesArrayFromPref));
        } else {
            countriesList = new ArrayList<>();
        }
        countriesList.add(countryCode);
        String countriesCommaSeparatedToSaveInPref = TextUtils.join(",", countriesList.toArray());
        mDataManager.saveCountries(countriesCommaSeparatedToSaveInPref);
    }

    private void removeFromFavorites(String countryCode) {
        String countriesCommaSeparatedFromPref = mDataManager.getCountries();
        String[] countriesArrayFromPref = StringUtils.split(countriesCommaSeparatedFromPref, ",");
        ArrayList<String> countriesList;
        if (countriesArrayFromPref != null && countriesArrayFromPref.length > 0) {
            countriesList = new ArrayList<>(Arrays.asList(countriesArrayFromPref));
            countriesList.remove(countryCode);
            String countriesCommaSeparatedToSaveInPref = TextUtils.join(",", countriesList.toArray());
            mDataManager.saveCountries(countriesCommaSeparatedToSaveInPref);
        }
    }

    private boolean isAtLeastOneDisbursementModeActive(List<DisbursementOption> disbursementOptionList) {
        if (disbursementOptionList == null || disbursementOptionList.size() == 0) {
            return false;
        } else {
            for (DisbursementOption disbursementOption : disbursementOptionList) {
                if (StringUtils.equalsAnyIgnoreCase(ACTIVE, disbursementOption.getMode())) {
                    return true;
                }
            }
        }
        return false;
    }

    static class PresenterFactory {

        private ServiceRepo serviceRepo;
        private DataManager dataManager;

        public PresenterFactory(ServiceRepo serviceRepo, DataManager dataManager) {
            this.serviceRepo = serviceRepo;
            this.dataManager = dataManager;
        }

        public DisplayCountriesPresenter newInstance() {
            return new DisplayCountriesPresenter(serviceRepo, dataManager);
        }
    }
}
