package com.creditmantri.weatherapptask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.creditmantri.utills.AppConstants;
import com.creditmantri.volleyservicecalls.AppService;
import com.creditmantri.volleyservicecalls.ServiceCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WeatherActivity extends AppCompatActivity {

    AppService appservice=new AppService();
    ArrayList<HashMap<String,String>> locList;
    HashMap<String,String> locmap;
    String strCity="",strCityName="",resCode="";
    String[] cityArr=new String[]{"Chennai","Mumbai","Bengaluru","New Delhi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        for(int i=0;i<cityArr.length;i++)
        {
            getWeather(i);
        }
    }

    public void getWeather(int i)
    {
        appservice.getWeatherForecast(AppConstants.wStr, cityArr[i], new ServiceCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject cityObj=new JSONObject(response);
                    resCode=cityObj.getString("cod");
                    Log.e("City",resCode);
                    if(resCode.equals("200"))
                    {
                        strCityName=cityObj.getString("name");
                        if(strCityName.equals("Chennai"))
                        {
                            Log.e("City",strCityName);
                        }
                        else if(strCityName.equals("Mumbai"))
                        {
                            Log.e("City",strCityName);
                        }
                        else if(strCityName.equals("Bengaluru"))
                        {
                            Log.e("City",strCityName);
                        }
                        else if(strCityName.equals("New Delhi"))
                        {
                            Log.e("City",strCityName);
                        }
                        else
                        {
                            Log.e("City",strCityName);
                        }
                    }
                    else
                    {
                        Log.e("resCode",resCode);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void getForecast(int i)
    {
        appservice.getWeatherForecast(AppConstants.fStr,cityArr[i], new ServiceCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject resObj = new JSONObject(response);
                    resCode = resObj.getString("cod");
                    if (resCode.equals("200"))
                    {
                        strCity = resObj.getString("city");
                        JSONObject cityObj = new JSONObject(strCity);
                        strCityName = cityObj.getString("name");
                        if (strCityName.equals("Chennai"))
                        {

                        } else if (strCityName.equals("Mumbai"))
                        {

                        } else if (strCityName.equals("Bengaluru"))
                        {

                        } else if (strCityName.equals("New Delhi"))
                        {

                        } else {
                            Log.e("City", strCityName);
                        }
                    }
                    else
                    {
                        Log.e("resCode",resCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
