package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import poojawins.lukesterlee.c4q.nyc.daybuilder.stock.StockDialogActivity;
import poojawins.lukesterlee.c4q.nyc.daybuilder.stock.Stock;
import poojawins.lukesterlee.c4q.nyc.daybuilder.stock.StockAdapter;
import poojawins.lukesterlee.c4q.nyc.daybuilder.stock.StocksGetter;

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
 * TODO : choose our product name : C4Q Now
 * TODO : complete readme documentation
 * TODO : prepare Demo Day
 */

public class MainActivity extends ActionBarActivity {

    CardView mCardViewWeather;
    CardView mCardViewTodo;
    CardView mCardViewStock;

    private static final String JSON_STOCK_ENDPOINT = "http://finance.google.com/finance/info?client=ig&q=GOOG";
    private static final String TICKERS_KEY = "tickers";

    ListView mListViewStock;
    Button mButtonStockRefresh;
    Button mButtonStockAdd;
    private static Set<String> tickersList = null;
    StockAdapter adapter;

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

    private void refreshStockData() {


    }

    private void initializeViews() {
        mCardViewWeather = (CardView) findViewById(R.id.card_view_weather);
        mCardViewTodo = (CardView) findViewById(R.id.card_view_todo);
        mCardViewStock = (CardView) findViewById(R.id.card_view_stock);
        mListViewStock = (ListView) findViewById(R.id.listView_stock);
        mButtonStockAdd = (Button) findViewById(R.id.button_stock_add);
        mButtonStockRefresh = (Button) findViewById(R.id.button_stock_refresh);
    }

    private void initializeData() {

//        SharedPreferences sharedPreferences = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
//        tickersList = sharedPreferences.getStringSet(TICKERS_KEY, null);

    }

    // TODO : every time the app starts, update all the card information.
    private void updateData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        setUpListeners(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeData();
        updateData();
        new StockTask().execute();
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

    private class StockTask extends AsyncTask<Void, Void, List<Stock>> {

        @Override
        protected List<Stock> doInBackground(Void... params) {
            String jsonUrl = JSON_STOCK_ENDPOINT;
            if (tickersList != null) {

            }
            jsonUrl += ",AAPL,HMC,GE,VZ";
            try {
                return new StocksGetter(jsonUrl).getStocksList();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Stock> stocks) {
            adapter = new StockAdapter(MainActivity.this, stocks);
            mListViewStock.setAdapter(adapter);
        }
    }
}
