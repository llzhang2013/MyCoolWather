package com.example.zhanglili.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhanglili.coolweather.db.City;
import com.example.zhanglili.coolweather.db.Country;
import com.example.zhanglili.coolweather.db.Province;
import com.example.zhanglili.coolweather.util.HttpUtil;
import com.example.zhanglili.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zhanglili on 2018/5/31.
 */

public class ChoseAreaFragment extends Fragment {
    private static final String TAG = "ZLL_ChoseAreaFragment";
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;
    private Province selectedProvince;
    private City selectedCity;
    private Country selectedCountry;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.chose_area,container,false);
       titleText = (TextView)view.findViewById(R.id.title_text);
       backButton = (Button)view.findViewById(R.id.back_button);
       listView = (ListView)view.findViewById(R.id.list_view);
       dataList.add("1");
       dataList.add("2");
       adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
       listView.setAdapter(adapter);
       return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    queryCity();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCountry();
                }else if(currentLevel==LEVEL_COUNTRY){
                    String weatherId = countryList.get(i).getWeatherID();
                    if(getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(),WeahterActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeahterActivity){
                        WeahterActivity activity = (WeahterActivity)getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeahter(weatherId);

                    }

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_COUNTRY){
                    queryCity();
                }else if(currentLevel==LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        queryProvince();
    }

    private void queryProvince(){

        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            Log.d(TAG, "queryProvince:本地获取 ");
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            Log.d(TAG, "queryProvince: 网络请求");
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");

        }
//        Log.d(TAG, "queryProvince: 网络请求");
//        String address = "http://guolin.tech/api/china";
//        queryFromServer(address,"province");
    }
    private void queryCity(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        Log.d(TAG, "queryCity: "+cityList);
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }

    }
    private void queryCountry(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityid=?",String.valueOf(selectedCity.
                getId())).find(Country.class);
        Log.d(TAG, "queryCountry: "+countryList);
        if(countryList.size()>0){
            dataList.clear();
            for(Country country:countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"country");
        }


    }

    private void queryFromServer(String address,final String type){
        Log.d(TAG, "queryFromServer: ");
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean result = false;

                String responceText = response.body().string();
                Log.d(TAG, "onResponse: response"+responceText);
                if("province".equals(type)){
                    result = Utility.handleProvinceResponce(responceText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponce(responceText,selectedProvince.getId());
                }else{
                    result = Utility.handleCountryResponce(responceText,selectedCity.getId());
                }

                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                               queryCity();
                            }else{
                               queryCountry();
                            }
                        }
                    });
                }

            }
        });
    }


}
