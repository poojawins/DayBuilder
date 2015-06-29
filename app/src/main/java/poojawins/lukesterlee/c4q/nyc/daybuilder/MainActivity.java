package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
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

import com.example.android.swipedismiss.SwipeDismissTouchListener;
import com.squareup.picasso.Picasso;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
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
 */


public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, AddDialogListener {

    private CardView mCardViewWeather;
    private CardView mCardViewTodo;
    private CardView mCardViewStock;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String JSON_STOCK_ENDPOINT = "http://finance.google.com/finance/info?client=ig&q=GOOGL";
    private static final String SHARED_PREFERENCES_STOCK_KEY = "stock";
    private static final String SHARED_PREFERENCES_TODO_KEY = "todo";
    private static final int REQUEST_CODE_TODO = 1;
    private static final int REQUEST_CODE_STOCK = 2;

    LayoutInflater inflater;

    ConnectivityManager connectivityManager;
    NetworkInfo activeNetwork;
    boolean isConnected;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextViewTitle;

    // weather stuffs
    double latitude;
    double longitude;
    private static final String WEATHER_ICON_URL = "http://openweathermap.org/img/w/";

    // to do view stuffs
    private LinearLayout mParentLayoutTodo;
    private Button mButtonTodoFooter;
    private Set<String> todoSet;
    private NoScrollAdapter<String> todoAdapter;
    private List<String> mRestOfTodos = null;
    private boolean isShowMoreTodo;
    private boolean isFromDialogTodo;


    // Stock view stuffs
    private LinearLayout mParentLayoutStock;
    private TextView mTextViewStockUpdate;
    private Button mButtonStockFooter;
    private Set<String> stockNameSet;
    private List<Stock> mRestOfStocks = null;
    private NoScrollAdapter<Stock> stockAdapter;
    private boolean isShowMoreStock;
    private boolean isFromDialogStock;
    Date lastUpdated;
    final Handler mHandler = new Handler();
    private final static int INTERVAL = 1000 * 60;
    Runnable postTimeRunnable = new Runnable() {
        @Override
        public void run() {
            Date now = new Date();
            int difference = (int) ((now.getTime() - lastUpdated.getTime())/INTERVAL);
            if (difference == 1) {
                mTextViewStockUpdate.setText("Updated " + difference + " minute ago");
            } else if (difference < 60) {
                mTextViewStockUpdate.setText("Updated " + difference + " minutes ago");
            } else {
                difference = difference / 60;
                if (difference == 1) {
                    mTextViewStockUpdate.setText("Updated " + difference + " hour ago");
                } else {
                    mTextViewStockUpdate.setText("Updated " + difference + " hours ago");
                }

            }
            mHandler.postDelayed(postTimeRunnable, INTERVAL);
        }
    };




    private void initializeWeatherViews() {
        mTextViewTemperature = (TextView) findViewById(R.id.temperature);
        mTextViewLocation = (TextView) findViewById(R.id.location);
        mImageViewWeatherIcon = (ImageView) findViewById(R.id.weatherIcon);
    }

    private void initializeTodoViews() {
        mParentLayoutTodo = (LinearLayout) findViewById(R.id.todo_list_parent);
        mButtonTodoFooter = (Button) findViewById(R.id.button_todo_footer);
    }

    private void initializeStockViews() {
        mButtonStockFooter = (Button) findViewById(R.id.button_stock_footer);
        mParentLayoutStock = (LinearLayout) findViewById(R.id.stock_list_parent);
        mTextViewStockUpdate = (TextView) findViewById(R.id.stock_update);

    }

    private void initializeViews() {
        mTextViewTitle = (TextView) findViewById(R.id.textView_app_title);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mCardViewWeather = (CardView) findViewById(R.id.card_view_weather);
        mCardViewTodo = (CardView) findViewById(R.id.card_view_todo);
        mCardViewStock = (CardView) findViewById(R.id.card_view_stock);

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/kgsummersunshine.ttf");
        mTextViewTitle.setTypeface(titleFont);
        mTextViewTitle.setTextColor(getResources().getColor(R.color.blue));
    }

    // Weather view
    TextView mTextViewTemperature;
    TextView mTextViewLocation;
    ImageView mImageViewWeatherIcon;
    WeatherTask weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isShowMoreStock = false;
        isFromDialogStock = false;

        isShowMoreTodo = false;
        isFromDialogTodo = false;

        mSharedPreferences = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        initializeViews();
        initializeWeatherViews();
        initializeStockViews();
        initializeTodoViews();

        connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = connectivityManager.getActiveNetworkInfo();
        isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());

        fetchDataFromSharedPreferences();
        fetchTask();
        new StockTask().execute(stockNameSet);

    }

    private void setUpListeners(boolean isResumed) {
        if (!isResumed) {
            mButtonTodoFooter.setOnClickListener(null);
            mButtonStockFooter.setOnClickListener(null);
            mSwipeRefreshLayout.setOnRefreshListener(null);
        } else {
            mButtonStockFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShowMoreStock) {
                        stockAdapter.addStockViews(mRestOfStocks, true);
                        addStockTouchListener();
                        mButtonStockFooter.setText("Add a stock");
                        mButtonStockFooter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_white_18dp, 0, 0, 0);
                        isShowMoreStock = false;
                    } else {
                        new AddStockDialogFragment().show(getFragmentManager(), "AddStockDialogFragment");
                    }

                }
            });
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.green);

            mButtonTodoFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShowMoreTodo) {
                        todoAdapter.addTaskViews(mRestOfTodos, true);
                        addTaskTouchListener();
                        mButtonTodoFooter.setText("Add a task");
                        mButtonTodoFooter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_white_18dp, 0, 0, 0);
                        isShowMoreTodo = false;
                    } else {
                        new AddTaskDialogFragment().show(getFragmentManager(), "AddTaskDialogFragment");
                    }

                }
            });


        }
    }

    private void fetchDataFromSharedPreferences() {
        stockNameSet = mSharedPreferences.getStringSet(SHARED_PREFERENCES_STOCK_KEY, new TreeSet<String>());
        todoSet = mSharedPreferences.getStringSet(SHARED_PREFERENCES_TODO_KEY, new TreeSet<String>());

    }


    @Override
    protected void onPause() {
        super.onPause();
        setUpListeners(false);
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        weather = new WeatherTask();
        weather.execute();
        setUpListeners(true);
    }

    @Override
    // when the user pull to refresh, following will be executed to refresh cards.
    public void onRefresh() {
        fetchDataFromSharedPreferences();
        fetchTask();
        new StockTask().execute(stockNameSet);

    }



    @Override
    public void addDialogClicked(DialogFragment dialog, int requestCode, String data) {


        switch (requestCode) {
            case REQUEST_CODE_TODO:
                Set<String> todoList = mSharedPreferences.getStringSet(SHARED_PREFERENCES_TODO_KEY, new TreeSet<String>());
                Set<String> newTodoList = new TreeSet<>();
                newTodoList.addAll(todoList);
                newTodoList.add(data);
                editor.putStringSet(SHARED_PREFERENCES_TODO_KEY, newTodoList);
                editor.apply();

                dialog.dismiss();

                isFromDialogTodo = true;
                fetchDataFromSharedPreferences();
                fetchTask();
                break;
            case REQUEST_CODE_STOCK:
                Set<String> list = mSharedPreferences.getStringSet(SHARED_PREFERENCES_STOCK_KEY, new TreeSet<String>());
                Set<String> newStockList = new TreeSet<>();
                newStockList.addAll(list);
                newStockList.add(data);
                editor.putStringSet(SHARED_PREFERENCES_STOCK_KEY, newStockList);
                editor.apply();

                dialog.dismiss();


                isFromDialogStock = true;
                new StockTask().execute(newStockList);
                break;
        }

    }



//    public void deleteStock() {
//        Set<String> list = mSharedPreferences.getStringSet(SHARED_PREFERENCES_STOCK_KEY, new TreeSet<String>());
//        Set<String> newStockList = new TreeSet<>();
//        newStockList.addAll(list);
//        newStockList.add(data);
//        editor.putStringSet(SHARED_PREFERENCES_STOCK_KEY, newStockList);
//        editor.apply();
//
//    }

    public void deleteTodo() {

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
            lastUpdated = new Date();
            mHandler.removeCallbacks(postTimeRunnable);
            postTimeRunnable.run();
            mTextViewStockUpdate.setText("Just updated");
            stockAdapter = new NoScrollAdapter<>(MainActivity.this, mParentLayoutStock, R.layout.list_item_stock);

            if (isFromDialogStock) {
                isShowMoreStock = false;
                stockAdapter.addStockViews(stocks, false);
            } else {
                if (stocks.size() > 4) {
                    isShowMoreStock = true;
                    mButtonStockFooter.setText("Show more");
                    mButtonStockFooter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_down_white_18dp,0,0,0);
                    List<Stock> firstFour = stocks.subList(0,4);
                    mRestOfStocks = stocks.subList(4, stocks.size());
                    stockAdapter.addStockViews(firstFour,false);
                } else {
                    stockAdapter.addStockViews(stocks, false);
                }
            }
            isFromDialogStock = false;
            mSwipeRefreshLayout.setRefreshing(false);
            addStockTouchListener();
        }
    }

    private void fetchTask() {
        todoAdapter = new NoScrollAdapter<>(MainActivity.this, mParentLayoutTodo, R.layout.list_item_todo);
        List<String> list = new ArrayList<>();
        for (String sentence : todoSet) {
            list.add(sentence);
        }
        if (isFromDialogTodo) {
            isShowMoreTodo = false;
            todoAdapter.addTaskViews(list, false);
        } else {
            if (list.size() > 4) {
                isShowMoreTodo = true;
                mButtonTodoFooter.setText("Show more");
                mButtonTodoFooter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_down_white_18dp, 0, 0, 0);
                List<String> firstFour = list.subList(0,4);
                mRestOfTodos = list.subList(4, list.size());
                todoAdapter.addTaskViews(firstFour, false);
            } else {
                todoAdapter.addTaskViews(list, false);
            }
        }

        isFromDialogTodo = false;
        mSwipeRefreshLayout.setRefreshing(false);
        addTaskTouchListener();
    }

    private void addStockTouchListener() {
        for (View row : stockAdapter.getChildViews()) {

            row.setOnTouchListener(new SwipeDismissTouchListener(row, null, new SwipeDismissTouchListener.DismissCallbacks() {
                @Override
                public boolean canDismiss(Object token) {
                    return true;
                }


                @Override
                public void onDismiss(View view, Object token, boolean isLeft) {
                    mParentLayoutStock.removeView(view);
                    if (isLeft) {
                        //deleteStock();
                    }
                }
            }));
        }
    }

    private void addTaskTouchListener() {
        for (View row : todoAdapter.getChildViews()) {

            row.setOnTouchListener(new SwipeDismissTouchListener(row, null, new SwipeDismissTouchListener.DismissCallbacks() {
                @Override
                public boolean canDismiss(Object token) {
                    return true;
                }


                @Override
                public void onDismiss(View view, Object token, boolean isLeft) {
                    mParentLayoutTodo.removeView(view);
                    if (isLeft) {
                        deleteTodo();
                    } else {
                        // TODO: add to the completed list.
                    }

                }
            }));
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
