package com.creditmantri.weatherapptask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creditmantri.adapters.ViewPagerAdapter;
import com.creditmantri.adapters.WeatherRecyclerAdapter;
import com.creditmantri.models.UnitConvertor;
import com.creditmantri.utills.AppConstants;
import com.creditmantri.models.Weather;
import com.creditmantri.volleyservicecalls.AppService;
import com.creditmantri.volleyservicecalls.ServiceCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{

    TextView stv_bengaluru,stv_bg_weather,stv_chennai,stv_ch_weather,stv_mumbai,stv_mi_weather,
    stv_newdelhi,stv_nd_weather,stv_ch_icon,stv_mi_icon,stv_bg_icon,stv_nd_icon;
    TextView todayTemperature,todayDescription,todayWind,todayPressure,todayHumidity,todaySunrise,todaySunset, stv_b_location, todayIcon;
    ViewPager viewPager;
    TabLayout tabLayout;
    CardView scv_ch_view,scv_mi_view,scv_bg_view,scv_nd_view;
    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    ImageButton sibtn_share;

    AppService appservice=new AppService();
    ArrayList<HashMap<String,String>> weatherList;
    HashMap<String,String> weathermap;
    private List<Weather> longTermWeather = new ArrayList<>();
    private List<Weather> longTermTodayWeather = new ArrayList<>();
    private List<Weather> longTermTomorrowWeather = new ArrayList<>();
    String strCity="",strCityName="",resCode="",resWeather="";
    String[] cityArr=new String[]{"Chennai","Mumbai","Bengaluru","New Delhi"};
    Weather todayWeather = new Weather();
    Typeface weatherFont;
    private static Map<String, Integer> speedUnits = new HashMap<>(3);
    private static Map<String, Integer> pressUnits = new HashMap<>(3);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        commonIds();
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        weatherList=new ArrayList<>();
        for(int i=0;i<cityArr.length;i++)
        {
            getWeather(i);
        }

        getForecast(0);

        scv_ch_view.setOnClickListener(this);
        scv_mi_view.setOnClickListener(this);
        scv_bg_view.setOnClickListener(this);
        scv_nd_view.setOnClickListener(this);
        sibtn_share.setOnClickListener(this);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    public void commonIds()
    {
        stv_bengaluru=(TextView)findViewById(R.id.xtv_bengaluru);
        stv_bg_weather=(TextView)findViewById(R.id.xtv_bg_weather);
        stv_chennai=(TextView)findViewById(R.id.xtv_chennai);
        stv_ch_weather=(TextView)findViewById(R.id.xtv_ch_weather);
        stv_mumbai=(TextView)findViewById(R.id.xtv_mumbai);
        stv_mi_weather=(TextView)findViewById(R.id.xtv_mi_weather);
        stv_newdelhi=(TextView)findViewById(R.id.xtv_newdelhi);
        stv_nd_weather=(TextView)findViewById(R.id.xtv_nd_weather);
        stv_ch_icon=(TextView)findViewById(R.id.xtv_ch_icon);
        stv_mi_icon=(TextView)findViewById(R.id.xtv_mi_icon);
        stv_bg_icon=(TextView)findViewById(R.id.xtv_bg_icon);
        stv_nd_icon=(TextView)findViewById(R.id.xtv_nd_icon);
        todayTemperature = (TextView) findViewById(R.id.todayTemperature);
        todayDescription = (TextView) findViewById(R.id.todayDescription);
        todayWind = (TextView) findViewById(R.id.todayWind);
        todayPressure = (TextView) findViewById(R.id.todayPressure);
        todayHumidity = (TextView) findViewById(R.id.todayHumidity);
        todaySunrise = (TextView) findViewById(R.id.todaySunrise);
        todaySunset = (TextView) findViewById(R.id.todaySunset);
        todayIcon = (TextView) findViewById(R.id.todayIcon);
        scv_ch_view=(CardView)findViewById(R.id.xcv_ch_view);
        scv_mi_view=(CardView)findViewById(R.id.xcv_mi_view);
        scv_bg_view=(CardView)findViewById(R.id.xcv_bg_view);
        scv_nd_view=(CardView)findViewById(R.id.xcv_nd_view);
        sibtn_share=(ImageButton)findViewById(R.id.xibtn_share);
        stv_b_location=(TextView)findViewById(R.id.xtv_b_location);
        weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/weather.ttf");
        todayIcon.setTypeface(weatherFont);
        stv_ch_icon.setTypeface(weatherFont);
        stv_mi_icon.setTypeface(weatherFont);
        stv_bg_icon.setTypeface(weatherFont);
        stv_nd_icon.setTypeface(weatherFont);
        layoutBottomSheet=(LinearLayout)findViewById(R.id.bottom_sheet);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

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
                            weathermap=new HashMap<>();
                            weathermap.put("city",strCityName);
                            String resChWeather=cityObj.getString("weather");
                            JSONArray arrWea=new JSONArray(resChWeather);
                            for(int i=0;i<arrWea.length();i++)
                            {
                                JSONObject obj=arrWea.getJSONObject(i);
                                String main=obj.getString("main");
                                String description=obj.getString("description");
                                String iconid=obj.getString("id");
                                todayWeather.setIcon(setWeatherIcon(Integer.parseInt(iconid), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                                stv_ch_icon.setText(todayWeather.getIcon());

                                weathermap.put("main",main);
                                weathermap.put("description",description);
                                weathermap.put("icon",iconid);
                            }

                            JSONObject mainobj=new JSONObject(cityObj.getString("main"));
                            String temp=mainobj.getString("temp");
                            String pressure=mainobj.getString("pressure");
                            String humidity=mainobj.getString("humidity");
                            String temp_min=mainobj.getString("temp_min");
                            String temp_max=mainobj.getString("temp_max");

                            JSONObject sysobj=new JSONObject(cityObj.getString("sys"));
                            String sunrise=sysobj.getString("sunrise");
                            String sunset=sysobj.getString("sunset");

                            weathermap.put("sunrise",sunrise);
                            weathermap.put("sunset",sunset);
                            weathermap.put("temp",temp);
                            weathermap.put("pressure",pressure);
                            weathermap.put("humidity",humidity);
                            weathermap.put("temp_min",temp_min);
                            weathermap.put("temp_max",temp_max);


                            weatherList.add(weathermap);

                            stv_chennai.setText(strCityName);
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            float temperature = UnitConvertor.convertTemperature(Float.parseFloat(temp), sp);
                            temperature = Math.round(temperature);
                            stv_ch_weather.setText(new DecimalFormat("0.#").format(temperature) + " " + sp.getString("unit", "°C"));



                        }
                        else if(strCityName.equals("Mumbai"))
                        {
                            weathermap=new HashMap<>();
                            weathermap.put("city",strCityName);
                            String resMiWeather=cityObj.getString("weather");
                            JSONArray arrWea=new JSONArray(resMiWeather);
                            for(int i=0;i<arrWea.length();i++)
                            {
                                JSONObject obj=arrWea.getJSONObject(i);
                                String main=obj.getString("main");
                                String description=obj.getString("description");
                                String iconid=obj.getString("id");

                                todayWeather.setIcon(setWeatherIcon(Integer.parseInt(iconid), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                                stv_mi_icon.setText(todayWeather.getIcon());

                                weathermap.put("main",main);
                                weathermap.put("description",description);
                                weathermap.put("icon",iconid);

                            }
                            JSONObject mainobj=new JSONObject(cityObj.getString("main"));
                            String temp=mainobj.getString("temp");
                            String pressure=mainobj.getString("pressure");
                            String humidity=mainobj.getString("humidity");
                            String temp_min=mainobj.getString("temp_min");
                            String temp_max=mainobj.getString("temp_max");
                            JSONObject sysobj=new JSONObject(cityObj.getString("sys"));
                            String sunrise=sysobj.getString("sunrise");
                            String sunset=sysobj.getString("sunset");

                            weathermap.put("sunrise",sunrise);
                            weathermap.put("sunset",sunset);

                            weathermap.put("temp",temp);
                            weathermap.put("pressure",pressure);
                            weathermap.put("humidity",humidity);
                            weathermap.put("temp_min",temp_min);
                            weathermap.put("temp_max",temp_max);

                            weatherList.add(weathermap);

                            stv_mumbai.setText(strCityName);
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            float temperature = UnitConvertor.convertTemperature(Float.parseFloat(temp), sp);
                            temperature = Math.round(temperature);
                            stv_mi_weather.setText(new DecimalFormat("0.#").format(temperature) + " " + sp.getString("unit", "°C"));
                        }
                        else if(strCityName.equals("Bengaluru"))
                        {
                            weathermap=new HashMap<>();
                            weathermap.put("city",strCityName);
                            String resBgWeather=cityObj.getString("weather");
                            JSONArray arrWea=new JSONArray(resBgWeather);
                            for(int i=0;i<arrWea.length();i++)
                            {
                                JSONObject obj=arrWea.getJSONObject(i);
                                String main=obj.getString("main");
                                String description=obj.getString("description");
                                String iconid=obj.getString("id");
                                todayWeather.setIcon(setWeatherIcon(Integer.parseInt(iconid), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                                stv_bg_icon.setText(todayWeather.getIcon());

                                weathermap.put("main",main);
                                weathermap.put("description",description);
                                weathermap.put("icon",iconid);

                            }
                            JSONObject mainobj=new JSONObject(cityObj.getString("main"));
                            String temp=mainobj.getString("temp");
                            String pressure=mainobj.getString("pressure");
                            String humidity=mainobj.getString("humidity");
                            String temp_min=mainobj.getString("temp_min");
                            String temp_max=mainobj.getString("temp_max");

                            JSONObject sysobj=new JSONObject(cityObj.getString("sys"));
                            String sunrise=sysobj.getString("sunrise");
                            String sunset=sysobj.getString("sunset");

                            weathermap.put("sunrise",sunrise);
                            weathermap.put("sunset",sunset);

                            weathermap.put("temp",temp);
                            weathermap.put("pressure",pressure);
                            weathermap.put("humidity",humidity);
                            weathermap.put("temp_min",temp_min);
                            weathermap.put("temp_max",temp_max);

                            weatherList.add(weathermap);

                            stv_bengaluru.setText(strCityName);
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            float temperature = UnitConvertor.convertTemperature(Float.parseFloat(temp), sp);
                            temperature = Math.round(temperature);
                            stv_bg_weather.setText(new DecimalFormat("0.#").format(temperature) + " " + sp.getString("unit", "°C"));
                        }
                        else if(strCityName.equals("New Delhi"))
                        {
                            weathermap=new HashMap<>();
                            weathermap.put("city",strCityName);
                            String resNdWeather=cityObj.getString("weather");
                            JSONArray arrWea=new JSONArray(resNdWeather);
                            for(int i=0;i<arrWea.length();i++)
                            {
                                JSONObject obj=arrWea.getJSONObject(i);
                                String main=obj.getString("main");
                                String description=obj.getString("description");
                                String iconid=obj.getString("id");

                                todayWeather.setIcon(setWeatherIcon(Integer.parseInt(iconid), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                                stv_nd_icon.setText(todayWeather.getIcon());

                                weathermap.put("main",main);
                                weathermap.put("description",description);
                                weathermap.put("icon",iconid);

                            }
                            JSONObject mainobj=new JSONObject(cityObj.getString("main"));
                            String temp=mainobj.getString("temp");
                            String pressure=mainobj.getString("pressure");
                            String humidity=mainobj.getString("humidity");
                            String temp_min=mainobj.getString("temp_min");
                            String temp_max=mainobj.getString("temp_max");

                            JSONObject sysobj=new JSONObject(cityObj.getString("sys"));
                            String sunrise=sysobj.getString("sunrise");
                            String sunset=sysobj.getString("sunset");

                            weathermap.put("sunrise",sunrise);
                            weathermap.put("sunset",sunset);

                            weathermap.put("temp",temp);
                            weathermap.put("pressure",pressure);
                            weathermap.put("humidity",humidity);
                            weathermap.put("temp_min",temp_min);
                            weathermap.put("temp_max",temp_max);

                            weatherList.add(weathermap);

                            stv_newdelhi.setText(strCityName);
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            float temperature = UnitConvertor.convertTemperature(Float.parseFloat(temp), sp);
                            temperature = Math.round(temperature);
                            stv_nd_weather.setText(new DecimalFormat("0.#").format(temperature) + " " + sp.getString("unit", "°C"));
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
                            JSONObject reader = new JSONObject(response);
                            JSONArray list = reader.getJSONArray("list");
                            for (int i = 0; i < list.length(); i++) {
                                Weather weather = new Weather();

                                JSONObject listItem = list.getJSONObject(i);
                                JSONObject main = listItem.getJSONObject("main");

                                weather.setDate(listItem.getString("dt"));
                                weather.setTemperature(main.getString("temp"));
                                weather.setDescription(listItem.optJSONArray("weather").getJSONObject(0).getString("description"));
                                JSONObject windObj = listItem.optJSONObject("wind");
                                if (windObj != null) {
                                    weather.setWind(windObj.getString("speed"));
                                    weather.setWindDirectionDegree(windObj.getDouble("deg"));
                                }
                                weather.setPressure(main.getString("pressure"));
                                weather.setHumidity(main.getString("humidity"));

                                JSONObject rainObj = listItem.optJSONObject("rain");
                                String rain = "";
                                if (rainObj != null) {
                                    rain = getRainString(rainObj);
                                } else {
                                    JSONObject snowObj = listItem.optJSONObject("snow");
                                    if (snowObj != null) {
                                        rain = getRainString(snowObj);
                                    } else {
                                        rain = "0";
                                    }
                                }
                                weather.setRain(rain);

                                final String idString = listItem.optJSONArray("weather").getJSONObject(0).getString("id");
                                weather.setId(idString);

                                final String dateMsString = listItem.getString("dt") + "000";
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(Long.parseLong(dateMsString));
                                weather.setIcon(setWeatherIcon(Integer.parseInt(idString), cal.get(Calendar.HOUR_OF_DAY)));

                                Calendar today = Calendar.getInstance();
                                if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                                    longTermTodayWeather.add(weather);
                                } else if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) + 1) {
                                    longTermTomorrowWeather.add(weather);
                                } else {
                                    longTermWeather.add(weather);
                                }

                                updateLongTermWeatherUI();
                            }
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

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
           *//* Intent intent = new Intent(WeatherActivity.this, SettingsActivity.class);
            startActivity(intent);*//*
           return true;
        }
        return super.onOptionsItemSelected(item);
    }*/


    private String setWeatherIcon(int actualId, int hourOfDay) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            if (hourOfDay >= 7 && hourOfDay < 20) {
                icon = this.getString(R.string.weather_sunny);
            } else {
                icon = this.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = this.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = this.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = this.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = this.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = this.getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = this.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }

    public static String getRainString(JSONObject rainObj) {
        String rain = "0";
        if (rainObj != null) {
            rain = rainObj.optString("3h", "fail");
            if ("fail".equals(rain)) {
                rain = rainObj.optString("1h", "0");
            }
        }
        return rain;
    }

    public static String getWindDirectionString(SharedPreferences sp, Context context, Weather weather) {
        try {
            if (Double.parseDouble(weather.getWind()) != 0) {
                String pref = sp.getString("windDirectionFormat", null);
                if ("arrow".equals(pref)) {
                    return weather.getWindDirection(8).getArrow(context);
                } else if ("abbr".equals(pref)) {
                    return weather.getWindDirection().getLocalizedString(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private String localize(SharedPreferences sp, String preferenceKey, String defaultValueKey) {
        return localize(sp, this, preferenceKey, defaultValueKey);
    }

    public static String localize(SharedPreferences sp, Context context, String preferenceKey, String defaultValueKey) {
        String preferenceValue = sp.getString(preferenceKey, defaultValueKey);
        String result = preferenceValue;
        if ("speedUnit".equals(preferenceKey)) {
            if (speedUnits.containsKey(preferenceValue)) {
                result = context.getString(speedUnits.get(preferenceValue));
            }
        } else if ("pressureUnit".equals(preferenceKey)) {
            if (pressUnits.containsKey(preferenceValue)) {
                result = context.getString(pressUnits.get(preferenceValue));
            }
        }
        return result;
    }

    public WeatherRecyclerAdapter getAdapter(int id) {
        WeatherRecyclerAdapter weatherRecyclerAdapter;
        if (id == 0) {
            weatherRecyclerAdapter = new WeatherRecyclerAdapter(this, longTermTodayWeather);
        } else if (id == 1) {
            weatherRecyclerAdapter = new WeatherRecyclerAdapter(this, longTermTomorrowWeather);
        } else {
            weatherRecyclerAdapter = new WeatherRecyclerAdapter(this, longTermWeather);
        }
        return weatherRecyclerAdapter;
    }

    private void updateLongTermWeatherUI() {


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundleToday = new Bundle();
        bundleToday.putInt("day", 0);
        RecyclerViewFragment recyclerViewFragmentToday = new RecyclerViewFragment();
        recyclerViewFragmentToday.setArguments(bundleToday);
        viewPagerAdapter.addFragment(recyclerViewFragmentToday, getString(R.string.today));

        Bundle bundleTomorrow = new Bundle();
        bundleTomorrow.putInt("day", 1);
        RecyclerViewFragment recyclerViewFragmentTomorrow = new RecyclerViewFragment();
        recyclerViewFragmentTomorrow.setArguments(bundleTomorrow);
        viewPagerAdapter.addFragment(recyclerViewFragmentTomorrow, getString(R.string.tomorrow));

        Bundle bundle = new Bundle();
        bundle.putInt("day", 2);
        RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();
        recyclerViewFragment.setArguments(bundle);
        viewPagerAdapter.addFragment(recyclerViewFragment, getString(R.string.later));

        int currentPage = viewPager.getCurrentItem();

        viewPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (currentPage == 0 && longTermTodayWeather.isEmpty()) {
            currentPage = 1;
        }
        viewPager.setCurrentItem(currentPage, false);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.xcv_ch_view)
        {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            for(int i=0;i<weatherList.size();i++)
            {
                Log.e("P",weatherList.get(i).get("city"));
                if(weatherList.get(i).get("city").equals("Chennai"))
                {
                    String city = weatherList.get(i).get("city");
                    stv_b_location.setText(city);
                    String country = "IN";
                    DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
                    getSupportActionBar().setTitle(city + (country.isEmpty() ? "" : ", " + country));

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);

                    float temperature = UnitConvertor.convertTemperature(Float.parseFloat(weatherList.get(i).get("temp")), sp);
                    if (sp.getBoolean("temperatureInteger", false)) {
                        temperature = Math.round(temperature);
                    }

                    double pressure = UnitConvertor.convertPressure((float) Double.parseDouble(weatherList.get(i).get("pressure")), sp);

                    todayTemperature.setText(new DecimalFormat("0.#").format(temperature) + " " + sp.getString("unit", "°C"));
                    todayDescription.setText(weatherList.get(i).get("description").toUpperCase());
                    todayPressure.setText(getString(R.string.pressure) + ": " + new DecimalFormat("0.0").format(pressure) + " " +
                            localize(sp, "pressureUnit", "hPa"));
                    todayHumidity.setText(getString(R.string.humidity) + ": " + weatherList.get(i).get("pressure") + " %");
                    todaySunrise.setText(getString(R.string.sunrise) + ": " + timeFormat.format(new Date(Long.parseLong(weatherList.get(i).get("sunrise")) * 1000)));
                    todaySunset.setText(getString(R.string.sunset) + ": " + timeFormat.format(new Date(Long.parseLong(weatherList.get(i).get("sunset")) * 1000)));
                    todayWeather.setIcon(setWeatherIcon(Integer.parseInt(weatherList.get(i).get("icon")), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                    todayIcon.setText(todayWeather.getIcon());

                    break;
                }
            }

        }
        else if(view.getId()==R.id.xcv_mi_view)
        {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            for(int i=0;i<weatherList.size();i++)
            {
                Log.e("P",weatherList.get(i).get("city"));

                if(weatherList.get(i).get("city").equals("Mumbai"))
                {
                    String city = weatherList.get(i).get("city");
                    stv_b_location.setText(city);
                    String country = "IN";
                    DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
                    getSupportActionBar().setTitle(city + (country.isEmpty() ? "" : ", " + country));

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);

                    float temperature = UnitConvertor.convertTemperature(Float.parseFloat(weatherList.get(i).get("temp")), sp);
                    if (sp.getBoolean("temperatureInteger", false)) {
                        temperature = Math.round(temperature);
                    }

                    double pressure = UnitConvertor.convertPressure((float) Double.parseDouble(weatherList.get(i).get("pressure")), sp);

                    todayTemperature.setText(new DecimalFormat("0.#").format(temperature) + " " + sp.getString("unit", "°C"));
                    todayDescription.setText(weatherList.get(i).get("description").toUpperCase());
                    todayPressure.setText(getString(R.string.pressure) + ": " + new DecimalFormat("0.0").format(pressure) + " " +
                            localize(sp, "pressureUnit", "hPa"));
                    todayHumidity.setText(getString(R.string.humidity) + ": " + weatherList.get(i).get("pressure") + " %");
                    todaySunrise.setText(getString(R.string.sunrise) + ": " + timeFormat.format(new Date(Long.parseLong(weatherList.get(i).get("sunrise")) * 1000)));
                    todaySunset.setText(getString(R.string.sunset) + ": " + timeFormat.format(new Date(Long.parseLong(weatherList.get(i).get("sunset")) * 1000)));
                    todayWeather.setIcon(setWeatherIcon(Integer.parseInt(weatherList.get(i).get("icon")), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                    todayIcon.setText(todayWeather.getIcon());

                    break;
                }
            }
        }
        else if(view.getId()==R.id.xcv_bg_view)
        {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            for(int i=0;i<weatherList.size();i++)
            {
                Log.e("P",weatherList.get(i).get("city"));
                if(weatherList.get(i).get("city").equals("Bengaluru"))
                {
                    String city = weatherList.get(i).get("city");
                    stv_b_location.setText(city);
                    String country = "IN";
                    DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
                    getSupportActionBar().setTitle(city + (country.isEmpty() ? "" : ", " + country));

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);

                    float temperature = UnitConvertor.convertTemperature(Float.parseFloat(weatherList.get(i).get("temp")), sp);
                    if (sp.getBoolean("temperatureInteger", false)) {
                        temperature = Math.round(temperature);
                    }

                    double pressure = UnitConvertor.convertPressure((float) Double.parseDouble(weatherList.get(i).get("pressure")), sp);

                    todayTemperature.setText(new DecimalFormat("0.#").format(temperature) + " " + sp.getString("unit", "°C"));
                    todayDescription.setText(weatherList.get(i).get("description").toUpperCase());
                    todayPressure.setText(getString(R.string.pressure) + ": " + new DecimalFormat("0.0").format(pressure) + " " +
                            localize(sp, "pressureUnit", "hPa"));
                    todayHumidity.setText(getString(R.string.humidity) + ": " + weatherList.get(i).get("pressure") + " %");
                    todaySunrise.setText(getString(R.string.sunrise) + ": " + timeFormat.format(new Date(Long.parseLong(weatherList.get(i).get("sunrise")) * 1000)));
                    todaySunset.setText(getString(R.string.sunset) + ": " + timeFormat.format(new Date(Long.parseLong(weatherList.get(i).get("sunset")) * 1000)));
                    todayWeather.setIcon(setWeatherIcon(Integer.parseInt(weatherList.get(i).get("icon")), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                    todayIcon.setText(todayWeather.getIcon());

                    break;
                }
            }
        }
        else if(view.getId()==R.id.xcv_nd_view)
        {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            for(int i=0;i<weatherList.size();i++)
            {
                Log.e("P",weatherList.get(i).get("city"));
                if(weatherList.get(i).get("city").equals("New Delhi"))
                {
                    String city = weatherList.get(i).get("city");
                    stv_b_location.setText(city);
                    String country = "IN";
                    DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
                    getSupportActionBar().setTitle(city + (country.isEmpty() ? "" : ", " + country));

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);

                    float temperature = UnitConvertor.convertTemperature(Float.parseFloat(weatherList.get(i).get("temp")), sp);
                    if (sp.getBoolean("temperatureInteger", false)) {
                        temperature = Math.round(temperature);
                    }

                    double pressure = UnitConvertor.convertPressure((float) Double.parseDouble(weatherList.get(i).get("pressure")), sp);

                    todayTemperature.setText(new DecimalFormat("0.#").format(temperature) + " " + sp.getString("unit", "°C"));
                    todayDescription.setText(weatherList.get(i).get("description").toUpperCase());
                    todayPressure.setText(getString(R.string.pressure) + ": " + new DecimalFormat("0.0").format(pressure) + " " +
                            localize(sp, "pressureUnit", "hPa"));
                    todayHumidity.setText(getString(R.string.humidity) + ": " + weatherList.get(i).get("pressure") + " %");
                    todaySunrise.setText(getString(R.string.sunrise) + ": " + timeFormat.format(new Date(Long.parseLong(weatherList.get(i).get("sunrise")) * 1000)));
                    todaySunset.setText(getString(R.string.sunset) + ": " + timeFormat.format(new Date(Long.parseLong(weatherList.get(i).get("sunset")) * 1000)));
                    todayWeather.setIcon(setWeatherIcon(Integer.parseInt(weatherList.get(i).get("icon")), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                    todayIcon.setText(todayWeather.getIcon());

                    break;
                }
            }
        }
        else if(view.getId()==R.id.xibtn_share)
        {
            if(stv_b_location.getText().toString().equals("Chennai"))
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Today's Weather in Chennai");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Today's Weather in Chennai"+"\n\n"+"Temp :"+todayTemperature.getText().toString()+"\n"
                +"Wearther :"+todayDescription.getText().toString()+"\n"
                +"Pressure :"+todayPressure.getText().toString()+"\n"
                +"Humidity :"+todayHumidity.getText().toString()+"\n"
                +"Sunrise :"+todaySunrise.getText().toString()+"\n"
                +"Sunset :"+todaySunset.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
            else if(stv_b_location.getText().toString().equals("Bengaluru"))
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Today's Weather in Bengaluru");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Today's Weather in Bengaluru"+"\n\n"+"Temp :"+todayTemperature.getText().toString()+"\n"
                        +"Wearther :"+todayDescription.getText().toString()+"\n"
                        +"Pressure :"+todayPressure.getText().toString()+"\n"
                        +"Humidity :"+todayHumidity.getText().toString()+"\n"
                        +"Sunrise :"+todaySunrise.getText().toString()+"\n"
                        +"Sunset :"+todaySunset.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
            else if(stv_b_location.getText().toString().equals("Mumbai"))
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Today's Weather in Mumbai");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Today's Weather in Mumbai"+"\n\n"+"Temp :"+todayTemperature.getText().toString()+"\n"
                        +"Wearther :"+todayDescription.getText().toString()+"\n"
                        +"Pressure :"+todayPressure.getText().toString()+"\n"
                        +"Humidity :"+todayHumidity.getText().toString()+"\n"
                        +"Sunrise :"+todaySunrise.getText().toString()+"\n"
                        +"Sunset :"+todaySunset.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
            else if(stv_b_location.getText().toString().equals("New Delhi"))
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Today's Weather in New Delhi");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Today's Weather in New Delhi"+"\n\n"+"Temp :"+todayTemperature.getText().toString()+"\n"
                        +"Wearther :"+todayDescription.getText().toString()+"\n"
                        +"Pressure :"+todayPressure.getText().toString()+"\n"
                        +"Humidity :"+todayHumidity.getText().toString()+"\n"
                        +"Sunrise :"+todaySunrise.getText().toString()+"\n"
                        +"Sunset :"+todaySunset.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }
    }
}
