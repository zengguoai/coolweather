package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
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

public class WeatherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ScrollView weatherLayout;
    private TextView titleCity,titleUpdateTime,degreeText,
            weatherInfoText,qltyText,aqiText,pm25Text,comfortText,carWashText,sportText;
    private TextView flText,humText,presText,visText,dirText,spdText;
    private LinearLayout forecastLayout;
    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefreshLayout;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private NavigationView navigationView;
    private View headerLayout;
    private ImageView bingImage;
    private ImageView weatherImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherLayout = findViewById(R.id.weather_layout);
        forecastLayout = findViewById(R.id.forecast_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        drawerLayout =findViewById(R.id.drawer_layout);
        navigationView =findViewById(R.id.nav_view);
        headerLayout = navigationView.getHeaderView(0);
        bingImage = headerLayout.findViewById(R.id.icon_image);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        weatherImage=findViewById(R.id.weather_icon);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        flText = findViewById(R.id.fl_text);
        humText = findViewById(R.id.hum_text);
        presText = findViewById(R.id.pres_text);
        visText=findViewById(R.id.vis_text);
        dirText=findViewById(R.id.dir_text);
        spdText=findViewById(R.id.spd_text);
        qltyText= findViewById(R.id.qlty_text);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        navButton =findViewById(R.id.nav_button);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
        String weatherString = prefs.getString("weather",null);
        final String weatherId;
        if(weatherString!=null){
            //有缓存直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        String bingPic =prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
            Glide.with(this).load(bingPic).into(bingImage);
        }else{
            loadBingPic();
        }

    }
    /**
     * 根据天气id请求天气信息
     * */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=9d8e4294f3204b778151404fa165e839";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null && "ok".equals(weather.status) ){
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                            Glide.with(WeatherActivity.this).load(bingPic).into(bingImage);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+" 'C";
        titleCity.setText(cityName);
        titleUpdateTime.setText("当地时间："+updateTime);
        degreeText.setText("  "+degree);
        weatherInfoText.setText(weather.now.more.info);
        showNowweatherImage(weather);
        flText.setText(weather.now.fl + " 'C");
        humText.setText(weather.now.hum + " %");
        presText.setText(weather.now.pres );
        visText.setText(weather.now.vis);
        dirText.setText(weather.now.wind_dir);
        spdText.setText(weather.now.wind_spd );
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView indoText = view.findViewById(R.id.info_text);
            ImageView weatherimage = view.findViewById(R.id.forecat_item_image);
            TextView minText = view.findViewById(R.id.maxmin_text);
            dateText.setText(forecast.date);
            indoText.setText(forecast.more.info);
            showForecastweatherImage(weatherimage,forecast);
            minText.setText(forecast.temperatrue.max+" ~ "+forecast.temperatrue.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            qltyText.setText(weather.aqi.city.qlty);
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度： "+weather.suggestion.comfort.info;
        String carWash = "洗车指数： "+weather.suggestion.carWash.info;
        String sport = "运动建议： "+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
    }

    private void showForecastweatherImage(ImageView imageView,Forecast forecast) {
        String forecastWeatherInfo = forecast.more.info;
        if(forecastWeatherInfo.contains("晴")){
            imageView.setImageResource(R.drawable.ic__weather_sunny);
        }else if(forecastWeatherInfo.contains("云")){
            imageView.setImageResource(R.drawable.ic__weather_cloundy);
        }else if(forecastWeatherInfo.contains("雨")){
            imageView.setImageResource(R.drawable.ic__weather_shower);
        }else if(forecastWeatherInfo.contains("雪")){
            imageView.setImageResource(R.drawable.ic__weather_snow4);
        }else if(forecastWeatherInfo.contains("雷")){
            imageView.setImageResource(R.drawable.ic__weather_tstorm3);
        }
    }

    private void showNowweatherImage(Weather weather) {
        String weatherinfo = weather.now.more.info;
        if(weatherinfo.contains("晴")){
            weatherImage.setImageResource(R.drawable.ic__weather_sunny);
        }else if(weatherinfo.contains("云")){
            weatherImage.setImageResource(R.drawable.ic__weather_cloundy);
        }else if(weatherinfo.contains("雨")){
            weatherImage.setImageResource(R.drawable.ic__weather_shower);
        }else if(weatherinfo.contains("雪")){
            weatherImage.setImageResource(R.drawable.ic__weather_snow4);
        }else if(weatherinfo.contains("雷")){
            weatherImage.setImageResource(R.drawable.ic__weather_tstorm3);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.city_item:
                editor.putString("weather",null);
                editor.apply();
                Intent intent1 = new Intent(WeatherActivity.this,MainActivity.class);
                startActivity(intent1);
                editor.clear();
                break;
            case R.id.setting_item:
                Intent intent2 = new Intent(WeatherActivity.this,WeatherSettings.class);
                startActivity(intent2);
                break;
            case R.id.about_item:
                Intent intent = new Intent(WeatherActivity.this,AboutAppInfoActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
