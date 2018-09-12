package com.example.android.displaylistofcountries;

import java.util.List;

/**
 * Created by Lakshmanan on 9/10/18.
 */

public class DisplayCountriesContract {
    public interface View {
        void populateList(List<Country> countries);
        void registerToFavoriteClickEvents();
        void showErrorMessage();
        void showLoading();
        void hideLoading();
    }

    public interface Presenter {
        void attachView(DisplayCountriesContract.View view);
        void countryFavoriteButtonClicked(Country clickedCountry);
    }
}
