package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Created by Luke on 6/27/2015.
 */
public class AddTaskDialogFragment extends DialogFragment {

    private static final String[] PRIORITY = new String[]{"High", "Medium", "Low"};
    private static final int REQUEST_CODE_TODO = 1;

    View mDialogView;
    EditText mEditText;
    ListView mListView;
    ArrayAdapter<String> mAdapter;
    AddDialogListener mListener;
    String sentence;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mDialogView = inflater.inflate(R.layout.dialog_add_task, null);

        mEditText = (EditText) mDialogView.findViewById(R.id.editText_dialog_task);
        mListView = (ListView) mDialogView.findViewById(R.id.listView_dialog_task);

        builder.setView(mDialogView);
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, PRIORITY);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sentence = mEditText.getText().toString();
                if (sentence.length() > 0) {
                    String task = mAdapter.getItem(position).substring(0,1) + sentence;
                    mListener.addDialogClicked(AddTaskDialogFragment.this, REQUEST_CODE_TODO, task);
                }

            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (AddDialogListener) activity;
    }
}
