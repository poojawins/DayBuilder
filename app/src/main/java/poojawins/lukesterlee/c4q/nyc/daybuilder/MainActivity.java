package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.lang.Math;

/*
 * C4Q Access Code 2.1 Unit 2 Final Project
 *
 * Rules
 * 1. always "git pull origin master" first before starting to work
 * 2. push to master only when the code is working
 * 3. keep refactoring each other's code
 *
 * Basic Requirements
 * TODO : todo feature (Luke)
 * TODO : stock market card (Luke)
 * TODO : weather card (Pooja)
 * TODO : dark sky API with notifications (Pooja)
 * TODO : create our app icon
 *
 * Bonus
 * TODO : main screen takes up to status bar
 * TODO : animation - swipe to remove a card
 * TODO : animation - pull to refresh
 * TODO : animation - infinite scrolling
 * TODO : add Google search bar with Ok Google
 * TODO : songza API
 *
 * ETC
 * TODO : choose our team name :
 * TODO : choose our product name : C4Q Now, DayBuilder
 * TODO : complete readme documentation
 * TODO : prepare Demo Day
 */

public class MainActivity extends ActionBarActivity {

    CardView mCardViewWeather;
    CardView mCardViewTodo;
    CardView mCardViewStock;

    private static final String JSON_STOCK_ENDPOINT = "http://finance.google.com/finance/info?client=ig&q=GOOGL";
    private static final String SHARED_PREFERENCES_STOCK_KEY = "stock";
    private static final String SHARED_PREFERENCES_TODO_KEY = "todo";

    // Location
    double latitude;
    double longitude;

    // Weather Data
    private static final String WEATHER_ICON_URL = "http://openweathermap.org/img/w/";
    private static final String JSON_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?zip=11206";
//    private static final String JSON_WEATHER_BASE = "http://api.openweathermap.org/data/2.5/weather?lat=";
//    private static final String JSON_WEATHER_END = "&lon=";
//    private static final String JSON_WEATHER_URL = JSON_WEATHER_BASE + latitude + JSON_WEATHER_END + longitude;

    // Forecast Data
    private static final String JSON_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=brooklyn,us&cnt=5";
//    private static final String JSON_FORECAST_BASE = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=";
//    private static final String JSON_FORECAST_LON = "&lon=";
//    private static final String JSON_FORECAST_END = "&cnt=5";
//    private static final String JSON_FORECAST_URL = JSON_FORECAST_BASE + latitude + JSON_FORECAST_LON + longitude + JSON_FORECAST_END;
    ArrayList<Forecast> forecastData;

    // Dark Sky Notifications
    private static final String DARK_SKY_API_KEY = "d1dfd9033517c3d793c2b2744cdda637";
    private static final String lat = "40.7005350"; //bk
    private static final String lon = "-73.9396370"; //bk
    private static final String DARK_SKY_URL = "https://api.forecast.io/forecast/"
            + DARK_SKY_API_KEY + "/" + lat + "," + lon;
//    private static final String DARK_SKY_BASE = "https://api.darkskyapp.com/v1/forecast/";
//    private static final String DARK_SKY_URL = DARK_SKY_BASE + DARK_SKY_API_KEY + "/" + latitude + "," + longitude;
    Handler handler;

    // to do view stuffs


    // Stock view stuffs
    LinearLayout mParentLayoutStock;
    TextView mTextViewStockUpdate;
    Button mButtonStockRefresh;
    Button mButtonStockAdd;
    private static Set<String> stocksList;
    NoScrollAdapter<Stock> stockAdapter;

    // Weather view
    TextView mTextViewTemperature;
    TextView mTextViewLocation;
    ImageView mImageViewWeatherIcon;
    TextView mTextViewCondition;
    TextView mTextViewHumidity;
    TextView mTextViewWindSpeed;
    LinearLayout mParentLayoutForecast;
    ForecastAdapter forecastAdapter;

    WeatherTask weather;
    ForecastTask forecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

//        DarkSkyTask darkSkyTask = new DarkSkyTask();
//        Timer timer = new Timer();
//        timer.schedule(darkSkyTask, 0, 60000);

        callDarkSkyTask();
    }

    private void setUpListeners(boolean isResumed) {
        if (!isResumed) {
            mButtonStockAdd.setOnClickListener(null);
            mButtonStockRefresh.setOnClickListener(null);
        } else {
            mButtonStockRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchDataFromSharedPreferences();
                    new StockTask().execute(stocksList);

                }
            });
            mButtonStockAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, StockDialogActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    private void fetchDataFromSharedPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        stocksList = sp.getStringSet(SHARED_PREFERENCES_STOCK_KEY, new TreeSet<String>());

    }


    private void initializeViews() {
        mCardViewWeather = (CardView) findViewById(R.id.card_view_weather);
        mCardViewTodo = (CardView) findViewById(R.id.card_view_todo);
        mCardViewStock = (CardView) findViewById(R.id.card_view_stock);
        mButtonStockAdd = (Button) findViewById(R.id.button_stock_add);
        mButtonStockRefresh = (Button) findViewById(R.id.button_stock_refresh);
        mParentLayoutStock = (LinearLayout) findViewById(R.id.stock_list_parent);
        mTextViewStockUpdate = (TextView) findViewById(R.id.stock_update);
        mTextViewTemperature = (TextView) findViewById(R.id.temperature);
        mTextViewLocation = (TextView) findViewById(R.id.location);
        mImageViewWeatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        mTextViewCondition = (TextView) findViewById(R.id.condition);
        mTextViewHumidity = (TextView) findViewById(R.id.humidity);
        mTextViewWindSpeed = (TextView) findViewById(R.id.wind_speed);
        mParentLayoutForecast = (LinearLayout) findViewById(R.id.forecast_list_parent);
    }



    @Override
    protected void onPause() {
        super.onPause();
        setUpListeners(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDataFromSharedPreferences();
        new StockTask().execute(stocksList);
        weather = new WeatherTask();
        weather.execute();
        forecast = new ForecastTask();
        forecast.execute();
        setUpListeners(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class StockTask extends AsyncTask<Set<String>, Void, List<Stock>> {

        @Override
        protected List<Stock> doInBackground(Set<String>... params) {
            Set<String> mList = params[0];
            String jsonUrl = JSON_STOCK_ENDPOINT;
            HashMap<String, String> stockNames = new HashMap<>();
            if (mList.size() > 0) {
                for (String line : mList) {
                    int index = line.indexOf("|");
                    String ticker = line.substring(0, index);
                    String name = line.substring(index + 1);
                    stockNames.put(ticker, name);
                    jsonUrl += "," + ticker;
                }
            }
            try {
                return new StocksGetter(jsonUrl, stockNames).getStocksList();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Stock> stocks) {
            mTextViewStockUpdate.setText("Last Update : " + new SimpleDateFormat("HH:mm").format(new Date()));
            stockAdapter = new NoScrollAdapter<>(MainActivity.this, mParentLayoutStock, R.layout.list_item_stock);
            stockAdapter.addStockViews(stocks);

        }
    }

    private class WeatherTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void...voids) {
            try {
                String weatherData = new WeatherGetter().getJSON(JSON_WEATHER_URL);
                return weatherData;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
         protected void onPostExecute(String output) {
            if (output != null) {
                try {
                    JSONObject jObject = new JSONObject(output);

                    JSONObject main = jObject.getJSONObject("main");
                    String temp = main.getString("temp");
                    String location = jObject.getString("name");
                    double tempKelvin = Double.parseDouble(temp);
                    double fah = ((tempKelvin - 273.15) * 1.8) + 32.0;
                    int fahRounded = (int) Math.round(fah);
                    String tempFahrenheit = Integer.toString(fahRounded);

                    String condition = jObject.getJSONArray("weather").getJSONObject(0).getString("main");
                    String humidity = main.getString("humidity");
                    String windSpeed = jObject.getJSONObject("wind").getString("speed");

                    String icon = jObject.getJSONArray("weather").getJSONObject(0).getString("icon");

                    mTextViewLocation.setText(location);
                    mTextViewTemperature.setText(tempFahrenheit + "°");
                    mTextViewCondition.setText(condition);
                    mTextViewHumidity.setText(humidity + "% humidity");
                    mTextViewWindSpeed.setText("Wind " + windSpeed + "mph");
                    Picasso.with(MainActivity.this).load(WEATHER_ICON_URL + icon + ".png")
                            .resize(125, 125).centerCrop().into(mImageViewWeatherIcon);
                } catch(Exception e) {
                    Log.println(Log.DEBUG, "pooja", "An Exception Happened");
                }
            }
        }
    }

    private class ForecastTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void...voids) {
            try {
                String weatherData = new WeatherGetter().getJSON(JSON_FORECAST_URL);
                return weatherData;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String output) {
            if (output != null) {
                try {
                    JSONObject jObject = new JSONObject(output);
                    JSONArray data = jObject.getJSONArray("list");
                    setForecastDataArray(data);

//                    forecastAdapter = new ForecastAdapter<>(MainActivity.this, mParentLayoutForecast, R.layout.list_item_forecast);
//                    forecastAdapter.addForecastViews(forecastData);

                } catch(Exception e) {
                    Log.println(Log.DEBUG, "pooja", "An Exception Happened");
                }
            }
        }
    }

    private class DarkSkyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void...voids) {
            try {
                String weatherData = new WeatherGetter().getJSON(DARK_SKY_URL);
                return weatherData;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String output) {
            if (output != null) {
                try {
                    JSONObject jObject = new JSONObject(output);
                    int precipitating = jObject.getJSONObject("currently").getInt("precipIntensity");
                    if (precipitating == 0) { //CHANGE TO != WHEN DONE TESTING!!
                        showNotification();
                    }
                } catch(Exception e) {
                    Log.println(Log.DEBUG, "pooja", "An Exception Happened");
                }
            }
        }
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Rain Advisory");
        builder.setContentText("It is Raining!");
        builder.setSmallIcon(R.drawable.ic_stat_raincloud);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    public void callDarkSkyTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        int minutes = 15; // execute every 15 minutes
        int msec = minutes * 60 * 1000;

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            DarkSkyTask darkSkyTask = new DarkSkyTask();
                            darkSkyTask.execute();
                        } catch (Exception e) {
                            Log.println(Log.DEBUG, "pooja", "An Exception Happened");
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, msec); // execute every 15 minutes
    }

    public void setForecastDataArray(JSONArray data) {
        forecastData = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            try {

                JSONObject item = data.getJSONObject(i);
//                int day = item.getInt("dt"); // convert to day (String)
//                String temp = item.getString("temp");
//                int highTemp = temp.getInt("max"); // convert to fahrenheit + String
//                int lowTemp = temp.getInt("min"); // convert to fahrenheit + String
                String icon = item.getJSONArray("weather").getJSONObject(0).getString("icon");

                Forecast forecast = new Forecast();
//                forecast.setDay(day);
//                forecast.setHighTemp(highTemp); // temp must be string
//                forecast.setLowTemp(lowTemp); // temp must be string
                forecast.setIcon(icon);

                forecastData.add(forecast);
            } catch (Exception e){
                Log.println(Log.DEBUG, "pooja", "An Exception Happened");
            }

        }

    }
}
