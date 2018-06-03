package com.example.zhanglili.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhanglili on 2018/6/2.
 */

public class Now {

    @SerializedName("tmp")
    public  String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}

/*
now:{
    tmp:29,
    cond:{
        txt: 阵雨
        }
    }
* */
