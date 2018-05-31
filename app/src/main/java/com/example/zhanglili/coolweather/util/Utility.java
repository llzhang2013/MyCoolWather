package com.example.zhanglili.coolweather.util;

import android.text.TextUtils;

import com.example.zhanglili.coolweather.db.City;
import com.example.zhanglili.coolweather.db.Country;
import com.example.zhanglili.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhanglili on 2018/5/31.
 */

public class Utility {
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
                    country.setWeatherID(object.getString("weather+id"));
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
