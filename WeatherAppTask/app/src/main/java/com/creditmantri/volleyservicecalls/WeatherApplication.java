package com.creditmantri.volleyservicecalls;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Anitha on 14/5/18.
 */

public class WeatherApplication extends Application
{
    public static final String TAG = WeatherApplication.class.getSimpleName();
    private static WeatherApplication ourInstance;
    private RequestQueue mRequestQueue;


    @Override
    public void onCreate()
    {
        super.onCreate();
        ourInstance=this;
    }

    public static synchronized WeatherApplication getInstance()
    {
        return ourInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag)
    {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag)
    {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
