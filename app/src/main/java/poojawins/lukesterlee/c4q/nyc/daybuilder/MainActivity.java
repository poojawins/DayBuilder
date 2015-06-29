package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
 * C4Q Access Code 2.1 Unit 2 Final Project
 */


// https://github.com/krossovochkin/Android-SwipeToDismiss-RecyclerView

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, AddDialogListener {

    private CardAdapter.CardViewHolder mCardViewStock;

    private SharedPreferences mSharedPreferences;

    private static final String JSON_STOCK_ENDPOINT = "http://finance.google.com/finance/info?client=ig&q=GOOGL";
    private static final String SHARED_PREFERENCES_STOCK_KEY = "stock";
    private static final String SHARED_PREFERENCES_TODO_KEY = "todo";
    private static final int REQUEST_CODE_TODO = 1;
    private static final int REQUEST_CODE_STOCK = 2;
    private static final int VIEW_TYPE_TITLE = 0;
    private static final int VIEW_TYPE_WEATHER = 1;
    private static final int VIEW_TYPE_TODO = 2;
    private static final int VIEW_TYPE_STOCK = 3;

    LayoutInflater inflater;
    CardAdapter cardAdapter;

    ConnectivityManager connectivityManager;
    NetworkInfo activeNetwork;
    boolean isConnected;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewCard;

    // to do view stuffs
    private RecyclerView mRecyclerViewTodo;
    private Button mButtonTodoFooter;
    private Set<String> todoSet;
    private NoScrollAdapter<String> todoAdapter;
    private boolean isShowMoreTodo;
    private boolean isFromDialogTodo;


    // Stock view stuffs
    private final static int INTERVAL = 1000 * 60;

    private TextView mTextViewStockUpdate;
    private Button mButtonStockAdd;
    private RecyclerView mRecyclerViewStock;

    private Set<String> stockNameSet;
    private List<Stock> mRestOfStocks = null;

    private StockAdapter stockAdapter;
    private boolean isShowMoreStock;
    private boolean isFromDialogStock;
    Date lastUpdated;
    final Handler mHandler = new Handler();
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

    }

    private void initializeTodoViews() {
    }

    private void initializeStockViews() {
        mCardViewStock = cardAdapter.onCreateViewHolder(mRecyclerViewCard, VIEW_TYPE_STOCK);
        mTextViewStockUpdate = mCardViewStock.getmTextViewStockUpdate();
        //mButtonStockAdd = mCardViewStock.getmButtonAddStock();
        mRecyclerViewStock = mCardViewStock.getmRecyclerViewStock();

    }

    private void initializeViews() {


        cardAdapter = new CardAdapter(MainActivity.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerViewCard = (RecyclerView) findViewById(R.id.recyclerView_card);
        mRecyclerViewCard.setHasFixedSize(true);

        mRecyclerViewCard.setLayoutManager(linearLayoutManager);



        mRecyclerViewCard.setAdapter(cardAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);



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
        inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        initializeViews();
        initializeWeatherViews();
        initializeStockViews();
        initializeTodoViews();

        connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = connectivityManager.getActiveNetworkInfo();
        isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());

        fetchDataFromSharedPreferences();
        //new TodoTask().execute(todoSet);
        new StockTask().execute(stockNameSet);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);




    }

    private void setUpListeners(boolean isResumed) {
        if (!isResumed) {
            //mButtonStockAdd.setOnClickListener(null);
            mSwipeRefreshLayout.setOnRefreshListener(null);
        } else {

//            mButtonStockAdd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    new AddStockDialogFragment().show(getFragmentManager(), "AddStockDialogFragment");
//                    if (isShowMoreStock) {
////                        stockAdapter.addStockViews(mRestOfStocks, true);
////                        mButtonStockAdd.setText("Add a stock");
////                        mButtonStockAdd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_white_18dp, 0, 0, 0);
////                        isShowMoreStock = false;
//                    } else {
//                        new AddStockDialogFragment().show(getFragmentManager(), "AddStockDialogFragment");
//                    }
//
//                }
//            });
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.green);

//            mButtonTodoFooter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (isShowMoreTodo) {
//
//                    } else {
//                        new AddTaskDialogFragment().show(getFragmentManager(), "AddTaskDialogFragment");
//                    }
//
//                }
//            });


        }
    }

    private void fetchDataFromSharedPreferences() {
        stockNameSet = mSharedPreferences.getStringSet(SHARED_PREFERENCES_STOCK_KEY, new TreeSet<String>());
        //todoSet = mSharedPreferences.getStringSet(SHARED_PREFERENCES_TODO_KEY, new TreeSet<String>());
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
        //new TodoTask().execute(todoSet);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new StockTask().execute(stockNameSet);
            }
        }, 1000);
    }



    @Override
    public void addDialogClicked(DialogFragment dialog, int requestCode, String data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

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
                new TodoTask().execute(newTodoList);
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



            stockAdapter = new StockAdapter(MainActivity.this, stocks);
            mRecyclerViewStock.setAdapter(stockAdapter);
            mRecyclerViewStock.setLayoutManager(new LinearLayoutManager(MainActivity.this));


//            if (isFromDialogStock) {
//                isShowMoreStock = false;
//                stockAdapter.addStockViews(stocks, false);
//            } else {
//                if (stocks.size() > 4) {
//                    isShowMoreStock = true;
//                    mButtonStockFooter.setText("Show more");
//                    mButtonStockFooter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_down_white_18dp,0,0,0);
//                    List<Stock> firstFour = stocks.subList(0,4);
//                    mRestOfStocks = stocks.subList(4, stocks.size());
//                    stockAdapter.addStockViews(firstFour,false);
//
//                } else {
//                    stockAdapter.addStockViews(stocks, false);
//                }
//            }
//
//            isFromDialogStock = false;
            mSwipeRefreshLayout.setRefreshing(false);


        }
    }

    private class TodoTask extends AsyncTask<Set<String>, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Set<String>... params) {
            Set<String> mList = params[0];
            return null;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
        }
    }
}
