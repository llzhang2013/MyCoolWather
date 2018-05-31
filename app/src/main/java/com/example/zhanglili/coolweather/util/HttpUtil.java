package com.example.zhanglili.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by zhanglili on 2018/5/31.
 */

public class HttpUtil {


    public static void sendOKHttpRequest(String address, okhttp3.Callback callBack){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callBack);


    }
}
