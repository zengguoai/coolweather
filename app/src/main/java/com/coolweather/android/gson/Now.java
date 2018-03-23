package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weiguanghua on 18-3-20.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    public String fl;
    public String hum;
    public String pres;
    public String wind_dir;
    public String wind_spd;
    @SerializedName("cond")
    public More more;

    @SerializedName("vis")
    public String vis;

    public class More{

        @SerializedName("txt")
        public String info;
    }
}
