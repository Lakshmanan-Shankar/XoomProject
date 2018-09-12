package com.example.android.displaylistofcountries.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Lakshmanan on 9/8/18.
 */


public class CountriesResponse {

    @SerializedName("items")
    public List<CountryDetail> items;

    public List<CountryDetail> getItems() {
        return items;
    }

    public void setItems(List<CountryDetail> items) {
        this.items = items;
    }
}
