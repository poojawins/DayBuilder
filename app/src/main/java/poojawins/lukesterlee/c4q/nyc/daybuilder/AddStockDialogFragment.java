package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Luke on 6/26/2015.
 */
public class AddStockDialogFragment extends DialogFragment {

    View dialog;
    EditText mEditText;
    ListView mListView;
    ArrayAdapter<String> adapter;

    String userSearchInput = "";

    AddStockListener mListener;
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialog = inflater.inflate(R.layout.dialog_add_stock, null);
        mEditText = (EditText) dialog.findViewById(R.id.editText_stock);
        mListView = (ListView) dialog.findViewById(R.id.listView_dialog_stock);
        builder.setView(dialog);

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

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
               mListener.addStockClicked(AddStockDialogFragment.this, stock);

           }
        });



    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (AddStockListener) activity;
    }

    private class StockSearchTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            return new CSVGetter(getActivity(), params[0]).getLines();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, strings);
            mListView.setAdapter(adapter);
        }
    }

    public interface AddStockListener {
        public void addStockClicked(AddStockDialogFragment dialog, String stock);
    }
}
