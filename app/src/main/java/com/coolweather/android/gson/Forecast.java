package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by weiguanghua on 18-3-20.
 */

public class Forecast {

    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperatrue temperatrue;
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
    public class Temperatrue{
        public String max;
        public String min;
    }
}
