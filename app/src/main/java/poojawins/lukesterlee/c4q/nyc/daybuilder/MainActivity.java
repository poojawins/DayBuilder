package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
 * TODO : add Google search bar with Ok Google
 *
 * ETC
 * TODO : choose our team name :
 * TODO : choose our product name : C4Q Now, DayBuilder
 * TODO : complete readme documentation
 * TODO : prepare Demo Day
 */

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private CardView mCardViewWeather;
    private CardView mCardViewTodo;
    private CardView mCardViewStock;

    private static final String JSON_STOCK_ENDPOINT = "http://finance.google.com/finance/info?client=ig&q=GOOGL";
    private static final String SHARED_PREFERENCES_STOCK_KEY = "stock";
    private static final String SHARED_PREFERENCES_TODO_KEY = "todo";

    private SwipeRefreshLayout mSwipeRefreshLayout;

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

    LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isShowMore = false;
        inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initializeViews();
        initializeWeatherViews();
        initializeStockViews();
        initializeTodoViews();
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
                        Intent intent = new Intent(MainActivity.this, StockDialogActivity.class);
                        startActivity(intent);
                    }

                }
            });
            mSwipeRefreshLayout.setOnRefreshListener(this);

        }

    }

    private void fetchDataFromSharedPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mCardViewWeather = (CardView) findViewById(R.id.card_view_weather);
        mCardViewTodo = (CardView) findViewById(R.id.card_view_todo);
        mCardViewStock = (CardView) findViewById(R.id.card_view_stock);
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


    @Override
    // when the user pull to refresh, following will be executed to refresh cards.
    public void onRefresh() {
        fetchDataFromSharedPreferences();
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
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
