package com.example.zhanglili.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.zhanglili.coolweather.db.City;
import com.example.zhanglili.coolweather.db.Country;
import com.example.zhanglili.coolweather.db.Province;
import com.example.zhanglili.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhanglili on 2018/5/31.
 */

public class Utility {
    private static final String TAG = "ZLL-Utility";

    public static Weather handleWeahterResponce(String responce){
        Log.d(TAG, "handleWeahterResponce: "+responce);
        try {
            JSONObject jsonObject = new JSONObject(responce);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weahterContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weahterContent,Weather.class);


        }catch (Exception e){
            Log.d(TAG, "handleWeahterResponce: Exception"+e);
            e.printStackTrace();
        }
        return null;
    }



    public static boolean handleProvinceResponce(String responce){
        if(!TextUtils.isEmpty(responce)){
            try {
                JSONArray allProvince = new JSONArray(responce);
                for(int i=0;i<allProvince.length();i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();

                }
                return true;


            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Boolean handleCityResponce(String responce,int proviceId){
        if(!TextUtils.isEmpty(responce)){
            try {
                JSONArray allCitys = new JSONArray(responce);
                for(int i=0;i<allCitys.length();i++){
                    JSONObject object = allCitys.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(object.getInt("id"));
                    city.setCityName(object.getString("name"));
                    city.setProvinceId(proviceId);
                    city.save();

                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountryResponce(String responce,int cityId){
        if(!TextUtils.isEmpty(responce)){
            try {
                JSONArray allCountry = new JSONArray(responce);
                for(int i=0;i<allCountry.length();i++){
                    JSONObject object = allCountry.getJSONObject(i);
                    Country country = new Country();
                    country.setCityId(cityId);
                    country.setCountryName(object.getString("name"));
                    country.setWeatherID(object.getString("weather_id"));
                    country.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

}
