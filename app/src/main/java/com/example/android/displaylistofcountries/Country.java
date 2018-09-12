package com.example.android.displaylistofcountries;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Lakshmanan on 9/8/18.
 */

public class Country implements Comparable<Country> {

    private String mCountryCode;
    private String mCountryName;
    private boolean mIsFavorite;

    Country() {}

    public Country(String countryCode, String countryName, boolean isFavorite) {
        this.mCountryCode = countryCode;
        this.mCountryName = countryName;
        this.mIsFavorite = isFavorite;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        this.mCountryCode = countryCode;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public void setCountryName(String countryName) {
        this.mCountryName = countryName;
    }

    public boolean getIsFavorite() {
        return mIsFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.mIsFavorite = isFavorite;
    }

    @Override
    public int compareTo(@NonNull Country country) {
        if(this.getIsFavorite() == country.getIsFavorite()) {
            return 0;
        } else if(this.getIsFavorite()) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Country)) {
            return false;
        }
        Country that = (Country) obj;
        if(StringUtils.equalsAnyIgnoreCase(this.getCountryName(), that.getCountryName())) {
            if(StringUtils.equalsAnyIgnoreCase(this.getCountryCode(), that.getCountryCode())) {
                return this.getIsFavorite() == that.getIsFavorite();
            }
        }
        return false;
    }
}
