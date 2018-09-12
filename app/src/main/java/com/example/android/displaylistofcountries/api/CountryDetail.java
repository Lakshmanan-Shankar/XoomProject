package com.example.android.displaylistofcountries.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Lakshmanan on 9/8/18.
 */

public class CountryDetail {

    @SerializedName("code")
    public String code;

    @SerializedName("name")
    public String name;

    @SerializedName("disbursement_options")
    public List<DisbursementOption> disbursementOptions;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DisbursementOption> getDisbursementOptions() {
        return disbursementOptions;
    }

    public void setDisbursementOptions(List<DisbursementOption> disbursementOptions) {
        this.disbursementOptions = disbursementOptions;
    }
}
