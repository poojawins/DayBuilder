package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Luke on 6/22/2015.
 */
public class StockDialogActivity extends Activity {

    EditText mEditText;
    ListView mListView;
    ArrayAdapter<String> adapter;

    String userSearchInput = "";

    private static final String SHARED_PREFERENCES_STOCK_KEY = "stock";


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            userSearchInput = mEditText.getText().toString();
            new StockSearchTask().execute(userSearchInput);
        }
    };

    private static final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_stock);

        mEditText = (EditText) findViewById(R.id.editText_stock);
        mListView = (ListView) findViewById(R.id.listView_dialog_stock);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 400);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mEditText.addTextChangedListener(textWatcher);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stock = adapter.getItem(position);
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sp.edit();
                Set<String> list = sp.getStringSet(SHARED_PREFERENCES_STOCK_KEY, new TreeSet<String>());
                list.add(stock);
                editor.putStringSet(SHARED_PREFERENCES_STOCK_KEY, list);
                editor.apply();

                Intent intent = new Intent(StockDialogActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private class StockSearchTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {

            return new CSVGetter(StockDialogActivity.this, params[0]).getLines();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            adapter = new ArrayAdapter<String>(StockDialogActivity.this, android.R.layout.simple_list_item_1, strings);
            mListView.setAdapter(adapter);
        }
    }

    public interface AddStockListener {
        public void addStockClicked(String stock);
    }
}
