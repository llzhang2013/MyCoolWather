package com.example.zhanglili.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zhanglili on 2018/5/31.
 */

public class Country extends DataSupport {
    private int id;
    private int cityId;
    private String countryName;
    private String weatherID;

    public int getId() {
        return id;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getWeatherID() {
        return weatherID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setWeatherID(String weatherID) {
        this.weatherID = weatherID;
    }
}
