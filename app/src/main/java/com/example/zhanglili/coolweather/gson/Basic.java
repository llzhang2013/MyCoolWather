package com.example.zhanglili.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhanglili on 2018/6/2.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }


}
/*
* basic:{
* city:苏州
* id:CN101190401
* update:{
* loc:2016-08-08 21:58
*
*
*
*
*
*
* }
* }
*
* */
