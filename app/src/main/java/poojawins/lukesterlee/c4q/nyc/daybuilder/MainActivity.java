package poojawins.lukesterlee.c4q.nyc.daybuilder;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // Location
    public static double latitude = 40.7005350;
    public static double longitude = -73.9396370;

    // Weather Data
    private static final String WEATHER_ICON_URL = "http://openweathermap.org/img/w/";
    private static final String JSON_WEATHER_BASE = "http://api.openweathermap.org/data/2.5/weather?lat=";
    private static final String JSON_WEATHER_END = "&lon=";

    // Forecast Data
    private static final String JSON_FORECAST_BASE = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=";
    private static final String JSON_FORECAST_LON = "&lon=";
    private static final String JSON_FORECAST_END = "&units=imperial&cnt=6";
    public List<Forecast> forecastData;

    // Dark Sky Notifications
    private static final String DARK_SKY_API_KEY = "d1dfd9033517c3d793c2b2744cdda637";
    private static final String DARK_SKY_BASE = "https://api.forecast.io/forecast/";
    Handler handler;


    // weather stuff
    private TextView mTextViewTemperature;
    private TextView mTextViewLocation;
    private ImageView mImageViewWeatherIcon;
    private TextView mTextViewCondition;
    private TextView mTextViewHumidity;
    private TextView mTextViewWindSpeed;
    private LinearLayout mParentLayoutForecast;
    NoScrollAdapter<Forecast> forecastAdapter;

    WeatherTask weather;
    ForecastTask forecast;

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
    private Map<String, Article> mArticleList;
    private Calendar lastUpdate = null;

    private ImageView mImageViewWorld;
    private TextView mTextViewTitleWorld;
    private TextView mTextViewDescriptionWorld;
    private TextView mTextViewPublishedDateWorld;

    private ImageView mImageViewUs;
    private TextView mTextViewTitleUs;
    private TextView mTextViewDescriptionUs;
    private TextView mTextViewPublishedDateUs;

    private ImageView mImageViewOpinion;
    private TextView mTextViewTitleOpinion;
    private TextView mTextViewDescriptionOpinion;
    private TextView mTextViewPublishedDateOpinion;

    private ImageView mImageViewTech;
    private TextView mTextViewTitleTech;
    private TextView mTextViewDescriptionTech;
    private TextView mTextViewPublishedDateTech;


    private boolean isAlreadyUpdated() {
        Calendar rightNow = Calendar.getInstance();
        if (lastUpdate == null) {
            return false;
        } else if (rightNow.get(Calendar.YEAR) != lastUpdate.get(Calendar.YEAR)) {
            return false;
        } else if (rightNow.get(Calendar.DAY_OF_YEAR) == lastUpdate.get(Calendar.DAY_OF_YEAR)) {
            return true;
        }
        return false;
    }

    private void initializeViews() {
        mImageViewTItle = (ImageView) findViewById(R.id.imageView_app_title);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);


        Picasso.with(MainActivity.this).load(R.drawable.c4qnow).resize(550, 550).into(mImageViewTItle);
    }



    private void initializeWeatherViews() {
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

    private void initializeArticleViews() {
        mImageViewWorld = (ImageView) findViewById(R.id.imageView_world);
        mTextViewTitleWorld = (TextView) findViewById(R.id.headline_world);
        mTextViewDescriptionWorld = (TextView) findViewById(R.id.description_world);
        mTextViewPublishedDateWorld = (TextView) findViewById(R.id.published_date_world);

        mImageViewUs = (ImageView) findViewById(R.id.imageView_us);
        mTextViewTitleUs = (TextView) findViewById(R.id.headline_us);
        mTextViewDescriptionUs = (TextView) findViewById(R.id.description_us);
        mTextViewPublishedDateUs = (TextView) findViewById(R.id.published_date_us);

        mImageViewOpinion = (ImageView) findViewById(R.id.imageView_opinion);
        mTextViewTitleOpinion = (TextView) findViewById(R.id.headline_opinion);
        mTextViewDescriptionOpinion = (TextView) findViewById(R.id.description_opinion);
        mTextViewPublishedDateOpinion = (TextView) findViewById(R.id.published_date_opinion);

        mImageViewTech = (ImageView) findViewById(R.id.imageView_tech);
        mTextViewTitleTech = (TextView) findViewById(R.id.headline_tech);
        mTextViewDescriptionTech = (TextView) findViewById(R.id.description_tech);
        mTextViewPublishedDateTech = (TextView) findViewById(R.id.published_date_tech);
    }


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
        initializeArticleViews();


        fetchDataFromSharedPreferences();
        fetchTask();
        doNetworkJob();

        callDarkSkyTask();
    }

    private void doNetworkJob() {
        if (hasNetwork()) {
            mParentLayoutStock.removeAllViews();
            new WeatherTask().execute();
            new ForecastTask().execute();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new StockTask().execute(stockNameSet);
                }
            }, 1000);
            new ArticleTask().execute();

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

    private class ArticleTask extends AsyncTask<Void, Void, Map<String, Article>> {

        @Override
        protected Map<String, Article> doInBackground(Void... voids) {
            try {
                return new ArticleGetter().getArticleList();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map<String, Article> articles) {
            lastUpdate = Calendar.getInstance();
            mArticleList = articles;
            fetchArticleData();

        }
    }

    private void fetchArticleData() {
        Article world = mArticleList.get("World");
        Picasso.with(MainActivity.this).load(world.getThumbnailUrl()).resize(200, 200).into(mImageViewWorld);
        Article us = mArticleList.get("Us");
        Picasso.with(MainActivity.this).load(us.getThumbnailUrl()).resize(200, 200).into(mImageViewUs);
        Article opinion = mArticleList.get("Opinion");
        Picasso.with(MainActivity.this).load(opinion.getThumbnailUrl()).resize(200, 200).into(mImageViewOpinion);
        Article tech = mArticleList.get("Tech");
        Picasso.with(MainActivity.this).load(tech.getThumbnailUrl()).resize(200, 200).into(mImageViewTech);

        mTextViewTitleWorld.setText(world.getTitle());
        mTextViewDescriptionWorld.setText(world.getDescription());
        mTextViewPublishedDateWorld.setText(world.getPublished_date());

        mTextViewTitleUs.setText(us.getTitle());
        mTextViewDescriptionUs.setText(us.getDescription());
        mTextViewPublishedDateUs.setText(us.getPublished_date());

        mTextViewTitleOpinion.setText(opinion.getTitle());
        mTextViewDescriptionOpinion.setText(opinion.getDescription());
        mTextViewPublishedDateOpinion.setText(opinion.getPublished_date());

        mTextViewTitleTech.setText(tech.getTitle());
        mTextViewDescriptionTech.setText(tech.getDescription());
        mTextViewPublishedDateTech.setText(tech.getPublished_date());


    }

    private class WeatherTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void...voids) {
            try {
                String weatherData = new WeatherGetter().getJSON(JSON_WEATHER_BASE + currentLocation().getLatitude() + JSON_WEATHER_END + currentLocation().getLongitude());
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
                String weatherData = new WeatherGetter().getJSON(JSON_FORECAST_BASE + currentLocation().getLatitude() + JSON_FORECAST_LON + currentLocation().getLongitude() + JSON_FORECAST_END);
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

                    List<Forecast> result = setForecastDataArray(data);

                    forecastAdapter = new NoScrollAdapter<>(MainActivity.this, mParentLayoutForecast, R.layout.list_item_forecast);
                    forecastAdapter.addForecastViews(result);

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
                String weatherData = new WeatherGetter().getJSON(DARK_SKY_BASE + DARK_SKY_API_KEY + "/" + currentLocation().getLatitude() + "," + currentLocation().getLongitude());
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

    public List<Forecast> setForecastDataArray(JSONArray data) {
        forecastData = new ArrayList<Forecast>();

        for (int i = 1; i < data.length(); i++) {
            try {

                JSONObject item = data.getJSONObject(i);

                long unixDatetime = item.getLong("dt");
                String day = dayFormatter(unixDatetime);

                JSONObject temp = item.getJSONObject("temp");

                double highTemp = temp.getInt("max");
                String tempH = tempFormatter(highTemp);

                double lowTemp = temp.getInt("min");
                String tempL = tempFormatter(lowTemp);

                String icon = item.getJSONArray("weather").getJSONObject(0).getString("icon");

                Forecast forecast = new Forecast();
                forecast.setDay(day);
                forecast.setHighTemp(tempH);
                forecast.setLowTemp(tempL);
                forecast.setIcon(icon);
                forecastData.add(forecast);
            } catch (Exception e){
                Log.println(Log.DEBUG, "pooja", "An Exception Happened");
            }

        }
        return forecastData;

    }

    public String dayFormatter(long unixDateTime) {
        Date date = new Date(unixDateTime * 1000); // needs to be in milliseconds
        String day = new SimpleDateFormat("EE").format(date);
        return day;
    }

    public String tempFormatter(double temp) {
        int fahRounded = (int) Math.round(temp);
        return Integer.toString(fahRounded);
    }

    public Location currentLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        Location location = lm.getLastKnownLocation(lm.getBestProvider(criteria, true));
        return location;
    }

}
