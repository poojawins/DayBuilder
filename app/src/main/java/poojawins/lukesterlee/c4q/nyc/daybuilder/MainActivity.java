package poojawins.lukesterlee.c4q.nyc.daybuilder;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.swipedismiss.SwipeDismissTouchListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
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
 */


public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, AddDialogListener {



    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String JSON_NYT_ENDPOINT = "http://api.nytimes.com/svc/search/v2/articlesearch.json?fq=pub_date:2005-07-02&page=3&api-key=6c976382a905719c0ed18ad05cf374c2:5:71503619";


    private static final String JSON_STOCK_ENDPOINT = "http://finance.google.com/finance/info?client=ig&q=GOOGL,GOOG";
    private static final String SHARED_PREFERENCES_STOCK_KEY = "stock";
    private static final String SHARED_PREFERENCES_TODO_KEY = "todo";
    private static final String SHARED_PREFERENCES_COMPLETED_KEY = "completed";
    private static final String SHARED_PREFERENCES_DELETED_KEY = "deleted";
    private static final int REQUEST_CODE_TODO = 1;
    private static final int REQUEST_CODE_STOCK = 2;

    LayoutInflater inflater;

    final Handler mHandler = new Handler();

    ConnectivityManager connectivityManager;
    NetworkInfo activeNetwork;

    private ImageView mImageViewTItle;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // weather stuffs
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
    private LinearLayout mParentLayoutTodo;
    private Button mButtonTodoFooter;
    private ImageButton mButtonTodoInfo;
    private Set<String> todoSet;
    private NoScrollAdapter<String> todoAdapter;
    private List<String> mRestOfTodos = null;
    private boolean isShowMoreTodo;
    private boolean isFromDialogTodo;
    private int totalDeleted;
    private int totalCompleted;

    // Stock view stuffs
    private LinearLayout mParentLayoutStock;
    private TextView mTextViewStockUpdate;
    private Button mButtonStockFooter;
    private Set<String> stockNameSet;
    private List<Stock> mRestOfStocks = null;
    private NoScrollAdapter<Stock> stockAdapter;
    private boolean isShowMoreStock;
    private boolean isFromDialogStock;
    private Date lastUpdatedStock;
    private final static int INTERVAL = 1000 * 60;
    Runnable postTimeRunnable = new Runnable() {
        @Override
        public void run() {
            Date now = new Date();
            int difference = (int) ((now.getTime() - lastUpdatedStock.getTime())/INTERVAL);
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

    // new york times stuff
    String mostViewed_key = "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/U.S./7.json?api-key=3f91a526526725b63f921c029e209d73:2:71503619";
    private static final String ENDPOINT_MOST_VIEWED = "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/U.S./7.json?api-key=";
    private Date lastUpdatedNews;


    private void initializeWeatherViews() {
        mTextViewTemperature = (TextView) findViewById(R.id.temperature);
        mTextViewLocation = (TextView) findViewById(R.id.location);
        mImageViewWeatherIcon = (ImageView) findViewById(R.id.weatherIcon);



        mTextViewTemperature = (TextView) findViewById(R.id.temperature);
        mTextViewLocation = (TextView) findViewById(R.id.location);
        mImageViewWeatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        mTextViewCondition = (TextView) findViewById(R.id.condition);
        mTextViewHumidity = (TextView) findViewById(R.id.humidity);
        mTextViewWindSpeed = (TextView) findViewById(R.id.wind_speed);
        mParentLayoutForecast = (LinearLayout) findViewById(R.id.forecast_list_parent);
    }

    private void initializeTodoViews() {
        mParentLayoutTodo = (LinearLayout) findViewById(R.id.todo_list_parent);
        mButtonTodoFooter = (Button) findViewById(R.id.button_todo_footer);
        mButtonTodoInfo = (ImageButton) findViewById(R.id.button_todo_info);
    }

    private void initializeStockViews() {
        mButtonStockFooter = (Button) findViewById(R.id.button_stock_footer);
        mParentLayoutStock = (LinearLayout) findViewById(R.id.stock_list_parent);
        mTextViewStockUpdate = (TextView) findViewById(R.id.stock_update);
        stockAdapter = new NoScrollAdapter<>(MainActivity.this, mParentLayoutStock, R.layout.list_item_stock);
    }

    private void initializeViews() {
        mImageViewTItle = (ImageView) findViewById(R.id.imageView_app_title);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        Picasso.with(MainActivity.this).load(R.drawable.c4qrainbow).resize(800, 800).into(mImageViewTItle);
    }

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



        isShowMoreStock = false;
        isFromDialogStock = false;

        isShowMoreTodo = false;
        isFromDialogTodo = false;

        mSharedPreferences = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);


        initializeViews();
        initializeWeatherViews();
        initializeStockViews();
        initializeTodoViews();



        fetchDataFromSharedPreferences();
        fetchTask();
        doNetworkJob();


//        DarkSkyTask darkSkyTask = new DarkSkyTask();
//        Timer timer = new Timer();
//        timer.schedule(darkSkyTask, 0, 60000);

        callDarkSkyTask();
    }

    private void doNetworkJob() {
        if (hasNetwork()) {
            mParentLayoutStock.removeAllViews();
            new StockTask().execute(stockNameSet);
            new WeatherTask().execute();
            new ForecastTask().execute();

        } else {
            stockAdapter.addNetworkWarningMessageView();
            mButtonStockFooter.setText("Add a stock");
            mButtonStockFooter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_white_18dp, 0, 0, 0);
            isShowMoreStock = false;
        }
    }



    private boolean hasNetwork() {
        activeNetwork = connectivityManager.getActiveNetworkInfo();
        return (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
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

            mButtonTodoInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TaskInfoDialogFragment taskInfo = new TaskInfoDialogFragment();
                    Bundle argument = new Bundle();
                    argument.putInt(SHARED_PREFERENCES_COMPLETED_KEY, totalCompleted);
                    argument.putInt(SHARED_PREFERENCES_DELETED_KEY, totalDeleted);
                    taskInfo.setArguments(argument);
                    taskInfo.show(getFragmentManager(), "TaskInfoDialogFragment");

                }
            });


        }
    }

    private void fetchDataFromSharedPreferences() {
        stockNameSet = mSharedPreferences.getStringSet(SHARED_PREFERENCES_STOCK_KEY, new TreeSet<String>());
        todoSet = mSharedPreferences.getStringSet(SHARED_PREFERENCES_TODO_KEY, new TreeSet<String>());
        totalCompleted = mSharedPreferences.getInt(SHARED_PREFERENCES_COMPLETED_KEY, 0);
        totalDeleted = mSharedPreferences.getInt(SHARED_PREFERENCES_DELETED_KEY, 0);
    }


    @Override
    protected void onPause() {
        super.onPause();
        setUpListeners(false);
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpListeners(true);
    }

    @Override
    // when the user pull to refresh, following will be executed to refresh cards.
    public void onRefresh() {
        fetchDataFromSharedPreferences();
        fetchTask();
        doNetworkJob();
    }



    @Override
    public void addDialogClicked(DialogFragment dialog, int requestCode, String data) {

        switch (requestCode) {
            case REQUEST_CODE_TODO:
                Set<String> todoList = mSharedPreferences.getStringSet(SHARED_PREFERENCES_TODO_KEY, new TreeSet<String>());
                if (todoList.size() == 0) {
                    mParentLayoutTodo.removeAllViews();
                }

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
                fetchDataFromSharedPreferences();
                doNetworkJob();
                break;
        }

    }



    public void deleteStock(String companyName) {
        Set<String> list = mSharedPreferences.getStringSet(SHARED_PREFERENCES_STOCK_KEY, new TreeSet<String>());
        Set<String> newStockList = new TreeSet<>();
        newStockList.addAll(list);

        for (String stock : newStockList) {
            if (stock.contains(companyName)) {
                newStockList.remove(stock);
                break;
            }
        }
        editor.putStringSet(SHARED_PREFERENCES_STOCK_KEY, newStockList);
        editor.apply();
        if (companyName.contains("Google")) {
            Toast.makeText(MainActivity.this, "You can't get rid of me :)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteTodo(String todo, boolean isLeft) {
        Set<String> list = mSharedPreferences.getStringSet(SHARED_PREFERENCES_TODO_KEY, new TreeSet<String>());
        Set<String> newTodoList = new TreeSet<>();
        newTodoList.addAll(list);

        for (String task : newTodoList) {
            if (task.contains(todo)) {
                newTodoList.remove(task);
                break;
            }
        }

        if (isLeft) {
            totalDeleted++;
            editor.putInt(SHARED_PREFERENCES_DELETED_KEY, totalDeleted);
            Toast.makeText(MainActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
        } else {
            totalCompleted++;
            editor.putInt(SHARED_PREFERENCES_COMPLETED_KEY, totalCompleted);
            Toast.makeText(MainActivity.this, "Completed!", Toast.LENGTH_SHORT).show();
        }
        editor.putStringSet(SHARED_PREFERENCES_TODO_KEY, newTodoList);
        editor.apply();

    }

    private void fetchTask() {
        todoAdapter = new NoScrollAdapter<>(MainActivity.this, mParentLayoutTodo, R.layout.list_item_todo);
        List<String> list = new ArrayList<>();
        for (String sentence : todoSet) {
            list.add(sentence);
        }


        if (list.size() == 0) {
            todoAdapter.addEmptyMessageView();
        } else {
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
            addTaskTouchListener();
        }
        mSwipeRefreshLayout.setRefreshing(false);
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
                        // this is not a good way but I can't find any other way.
                        TextView ticker = (TextView) view.findViewById(R.id.stock_company_name);
                        deleteStock(ticker.getText().toString());
                    } else {
                        Toast.makeText(MainActivity.this, "Dismissed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView ticker = (TextView) view.findViewById(R.id.stock_category);
                    WebViewDialogFragment webFragment = new WebViewDialogFragment();
                    Bundle argument = new Bundle();
                    argument.putString("ticker", ticker.getText().toString());
                    webFragment.setArguments(argument);
                    webFragment.show(getFragmentManager(), "WebViewFragment");


                }
            });
        }
    }

    private void addTaskTouchListener() {
        for (final View row : todoAdapter.getChildViews()) {


            row.setOnTouchListener(new SwipeDismissTouchListener(row, null, new SwipeDismissTouchListener.DismissCallbacks() {
                @Override
                public boolean canDismiss(Object token) {
                    return true;
                }

                @Override
                public void onDismiss(View view, Object token, boolean isLeft) {
                    mParentLayoutTodo.removeView(view);
                    TextView task = (TextView) view.findViewById(R.id.textView_todo);
                    deleteTodo(task.getText().toString(), isLeft);
                }
            }));
        }
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
            lastUpdatedStock = new Date();
            mHandler.removeCallbacks(postTimeRunnable);
            postTimeRunnable.run();
            mTextViewStockUpdate.setText("Just updated");

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
