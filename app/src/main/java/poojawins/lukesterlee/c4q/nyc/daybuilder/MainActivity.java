package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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

    double latitude;
    double longitude;
    private static final String WEATHER_ICON_URL = "http://openweathermap.org/img/w/";

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
    WeatherTask weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

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
                String weatherData = new WeatherGetter().getJSON();
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

                    JSONObject weather = jObject.getJSONArray("weather").getJSONObject(0);
                    String icon = weather.getString("icon");

                    mTextViewLocation.setText(location);
                    mTextViewTemperature.setText(tempFahrenheit + "°");
                    Picasso.with(MainActivity.this).load(WEATHER_ICON_URL + icon + ".png")
                            .resize(125,125).centerCrop().into(mImageViewWeatherIcon);
                } catch(Exception e) {
                    Log.println(Log.DEBUG, "pooja", "An Exception Happened");
                }
            }
        }
    }


}
