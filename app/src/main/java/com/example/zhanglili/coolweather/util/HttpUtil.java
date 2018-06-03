package com.example.zhanglili.coolweather.util;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by zhanglili on 2018/5/31.
 */

public class HttpUtil {


    public static void sendOKHttpRequest(String address, okhttp3.Callback callBack){
        Log.d("ZLL", "sendOKHttpRequest: "+address);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callBack);


    }
}
