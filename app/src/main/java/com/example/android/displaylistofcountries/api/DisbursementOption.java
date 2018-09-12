package com.example.android.displaylistofcountries.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lakshmanan on 9/8/18.
 */

public class DisbursementOption {

    @SerializedName("mode")
    public String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
