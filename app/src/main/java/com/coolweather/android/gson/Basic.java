package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weiguanghua on 18-3-20.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
