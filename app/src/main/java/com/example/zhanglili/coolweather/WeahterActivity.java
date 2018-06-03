package com.example.zhanglili.coolweather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhanglili.coolweather.gson.Forecast;
import com.example.zhanglili.coolweather.gson.Weather;
import com.example.zhanglili.coolweather.util.HttpUtil;
import com.example.zhanglili.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeahterActivity extends AppCompatActivity {
    private static final String TAG = "ZLL";

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    private ImageView bingPicImage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weahter);



        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString!=null){
            Weather weather = Utility.handleWeahterResponce(weatherString);
            showWeatherInfo(weather);
        }else{
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeahter(weatherId);
        }

        bingPicImage = (ImageView)findViewById(R.id.bing_pic_img);
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImage);
        }else{
            loadBindPic();
        }

    }

    private void loadBindPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOKHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeahterActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeahterActivity.this).load(bingPic).into(bingPicImage);
                    }
                });


            }
        });
    }

    public void requestWeahter(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=c13e57054ac2467b84b5ff87bafdf8d6";
        HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeahterActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responceText = response.body().string();
                final Weather weather = Utility.handleWeahterResponce(responceText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeahterActivity.this).edit();
                            editor.putString("weather", responceText);
                            editor.apply();

//
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeahterActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        loadBindPic();

    }

    private void showWeatherInfo(Weather weather){
        Log.d(TAG, "showWeatherInfo: "+weather);
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split("")[1];
        String degree = weather.now.temperature+"C";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);


        }
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度"+weather.suggestion.comfort.info;
        String carWash = "洗车指数"+weather.suggestion.carWash.info;
        String sport = "运动指数"+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

}
