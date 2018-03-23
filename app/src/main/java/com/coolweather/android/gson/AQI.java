package com.coolweather.android.gson;

/**
 * Created by weiguanghua on 18-3-20.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String qlty;
        public String aqi;
        public String pm25;
    }
}
