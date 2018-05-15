package com.creditmantri.volleyservicecalls;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creditmantri.utills.AppConstants;

import java.net.URLEncoder;

/**
 * Created by ant000160 on 14/5/18.
 */

public class AppService
{
    public static StringRequest weather_request=null;


    public static void getWeatherForecast(final String type,final String q,
                                          final ServiceCallback callback)
    {
        String reqURL=AppConstants.URL+type+"?q="+ URLEncoder.encode(q)+"&lang=en&mode=json&appid="+AppConstants.APP_ID;
        Log.e("URL",reqURL);
        weather_request = new StringRequest(Request.Method.GET,reqURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("Removed String", response);
                        try
                        {
                            callback.onSuccess(response);
                        }
                        catch (Exception e)
                        {

                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        );
        WeatherApplication.getInstance().addToRequestQueue(weather_request);
    }
}
