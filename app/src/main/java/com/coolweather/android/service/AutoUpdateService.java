package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by weiguanghua on 18-3-20.
 */

public class AutoUpdateService extends Service {
    private SharedPreferences prefs;
    private AlarmManager manager;
    private  PendingIntent pi;
    private String weather_status,bing_status;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        weather_status = prefs.getString("checkbox_update","off");
        bing_status = prefs.getString("checkbox_bing","off");
        if(weather_status.equals("on")) {
            Log.d("wgh","您启动自动更新天气了");
            updateWeather();
            alarmWork();
        }else {
            Log.d("wgh","请启动自动更新天气");
        }
        if(bing_status.equals("on")){
            Log.d("wgh","您启动每日一图了");
            updateBingPic();
            alarmWork();
        }else{
            Log.d("wgh","请启动每日一图");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void alarmWork(){
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;
        long triggerAtTime = SystemClock.currentThreadTimeMillis()+anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
    }
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });

    }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=9d8e4294f3204b778151404fa165e839";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                   e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if(weather!=null && "ok".equals(weather.status) ){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(manager!=null && pi!=null){
            manager.cancel(pi);
            Log.d("wgh","您取消了定时任务");
        }
        if(weather_status.equals("off")&&bing_status.equals("off")){
            stopSelf();
            Log.d("wgh","您取消了所有服务");
        }

    }
}
