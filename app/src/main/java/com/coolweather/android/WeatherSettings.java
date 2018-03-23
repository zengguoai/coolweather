package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.coolweather.android.service.AutoUpdateService;

/**
 * Created by weiguanghua on 18-3-21.
 */

public class WeatherSettings extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private CheckBox bingCheckBox;
    private CheckBox updateCheckBox;
    private  SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bingCheckBox = findViewById(R.id.bing_checkbox);
        updateCheckBox =findViewById(R.id.update_checkbox);
        bingCheckBox.setOnCheckedChangeListener(this);
        updateCheckBox.setOnCheckedChangeListener(this);
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.bing_checkbox:
                if(isChecked){
                    editor.putString("checkbox_bing","on");
                    editor.apply();
                    Intent intent = new Intent(this, AutoUpdateService.class);
                    startService(intent);
                }else{
                    editor.putString("checkbox_bing","off");
                    editor.apply();
                    Intent intent = new Intent(this, AutoUpdateService.class);
                    stopService(intent);
                }
                editor.clear();
                break;
            case R.id.update_checkbox:
                if(isChecked){
                    editor.putString("checkbox_update","on");
                    editor.apply();
                    Intent intent = new Intent(this, AutoUpdateService.class);
                    startService(intent);
                }else {
                    editor.putString("checkbox_update","off");
                    editor.apply();
                    Intent intent = new Intent(this, AutoUpdateService.class);
                    stopService(intent);
                }
                editor.clear();
                break;
        }
    }
}
