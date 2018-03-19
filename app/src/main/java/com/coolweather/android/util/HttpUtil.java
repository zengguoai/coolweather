package com.coolweather.android.util;

import javax.security.auth.callback.Callback;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by weiguanghua on 18-3-19.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String adress, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(adress).build();
        client.newCall(request).enqueue(callback);
    }
}
