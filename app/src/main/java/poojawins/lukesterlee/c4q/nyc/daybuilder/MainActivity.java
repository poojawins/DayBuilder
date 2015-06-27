package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
 * C4Q Access Code 2.1 Unit 2 Final Project
 */

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, AddStockDialogFragment.AddStockListener {

    private CardView mCardViewWeather;
    private CardView mCardViewTodo;
    private CardView mCardViewStock;

    private static final String JSON_STOCK_ENDPOINT = "http://finance.google.com/finance/info?client=ig&q=GOOGL";
    private static final String SHARED_PREFERENCES_STOCK_KEY = "stock";
    private static final String SHARED_PREFERENCES_TODO_KEY = "todo";

    LayoutInflater inflater;

    ConnectivityManager connectivityManager;
    NetworkInfo activeNetwork;
    boolean isConnected;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextViewTitle;

    // to do view stuffs
    private LinearLayout mParentLayoutTodo;
    private Button mButtonTodoAdd;
    private Set<String> todoList;
    private NoScrollAdapter<String> todoAdapter;


    // Stock view stuffs
    private final static int INTERVAL = 1000 * 60;
    private LinearLayout mParentLayoutStock;
    private TextView mTextViewStockUpdate;
    private Button mButtonStockFooter;
    private Set<String> stockNameSet;
    private List<Stock> mRestOfStocks = null;
    private NoScrollAdapter<Stock> stockAdapter;
    private boolean isShowMore;
    private boolean isFromDialog;
    Date lastUpdated;
    final Handler mHandler = new Handler();
    Runnable postTimeRunnable = new Runnable() {
        @Override
        public void run() {
            Date now = new Date();
            if ((now.getTime() - lastUpdated.getTime())/INTERVAL == 1) {
                mTextViewStockUpdate.setText("Updated " + (now.getTime() - lastUpdated.getTime())/INTERVAL + " minute ago");
            } else {
                mTextViewStockUpdate.setText("Updated " + (now.getTime() - lastUpdated.getTime())/INTERVAL + " minutes ago");
            }
            mHandler.postDelayed(postTimeRunnable, INTERVAL);
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isShowMore = false;
        isFromDialog = false;
        inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        initializeViews();
        initializeWeatherViews();
        initializeStockViews();
        initializeTodoViews();

        connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = connectivityManager.getActiveNetworkInfo();
        isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());

        fetchDataFromSharedPreferences();
        new StockTask().execute(stockNameSet);


    }

    private void setUpListeners(boolean isResumed) {
        if (!isResumed) {
            mButtonStockFooter.setOnClickListener(null);
            mSwipeRefreshLayout.setOnRefreshListener(null);
        } else {
            mButtonStockFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShowMore) {
                        stockAdapter.addStockViews(mRestOfStocks, true);
                        mButtonStockFooter.setText("Add a stock");
                        mButtonStockFooter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_white_18dp, 0, 0, 0);
                        isShowMore = false;
                    } else {
                        new AddStockDialogFragment().show(getFragmentManager(), "AddStockDialogFragment");
                    }

                }
            });
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.green);

            mButtonTodoAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AddTaskDialogFragment().show(getFragmentManager(), "AddTaskDialogFragment");
                }
            });


        }
    }

    private void fetchDataFromSharedPreferences() {
        SharedPreferences sp = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
//        if (stockNameSet != null) {
//            stockNameSet.clear();
//        }
        stockNameSet = sp.getStringSet(SHARED_PREFERENCES_STOCK_KEY, new TreeSet<String>());
    }

    private void initializeWeatherViews() {

    }

    private void initializeTodoViews() {
        mParentLayoutTodo = (LinearLayout) findViewById(R.id.todo_list_parent);
        mButtonTodoAdd = (Button) findViewById(R.id.button_todo_add);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    // when the user pull to refresh, following will be executed to refresh cards.
    public void onRefresh() {

        fetchDataFromSharedPreferences();
        new StockTask().execute(stockNameSet);
    }

    @Override
    public void addStockClicked(AddStockDialogFragment dialog, String stock) {
        SharedPreferences sp = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> list = sp.getStringSet(SHARED_PREFERENCES_STOCK_KEY, new TreeSet<String>());
        Set<String> newList = new TreeSet<>();
        newList.addAll(list);
        newList.add(stock);
        editor.putStringSet(SHARED_PREFERENCES_STOCK_KEY, newList);
        editor.apply();

        dialog.dismiss();



        fetchDataFromSharedPreferences();
        isFromDialog = true;
        new StockTask().execute(stockNameSet);
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
            //mTextViewStockUpdate.setText("Last Update : " + new SimpleDateFormat("HH:mm").format(lastUpdated));
            stockAdapter = new NoScrollAdapter<>(MainActivity.this, mParentLayoutStock, R.layout.list_item_stock);

            if (isFromDialog) {
                isShowMore = false;
                stockAdapter.addStockViews(stocks, false);
            } else {
                if (stocks.size() > 4) {
                    isShowMore = true;
                    mButtonStockFooter.setText("Show more");
                    mButtonStockFooter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_down_white_18dp,0,0,0);
                    List<Stock> firstFour = stocks.subList(0,4);
                    mRestOfStocks = stocks.subList(4, stocks.size());
                    stockAdapter.addStockViews(firstFour,false);

                } else {
                    stockAdapter.addStockViews(stocks, false);
                }
            }

            isFromDialog = false;
            mSwipeRefreshLayout.setRefreshing(false);


        }
    }
}
