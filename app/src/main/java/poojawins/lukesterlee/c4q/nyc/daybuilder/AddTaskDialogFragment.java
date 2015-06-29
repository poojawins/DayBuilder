package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Created by Luke on 6/27/2015.
 */
public class AddTaskDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener{

    private static final String[] PRIORITY = new String[]{"High", "Medium", "Low"};
    private static final int REQUEST_CODE_TODO = 1;

    View mDialogView;
    EditText mEditText;
    Spinner mSpinner;
    ArrayAdapter<CharSequence> mAdapter;
    AddDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mDialogView = inflater.inflate(R.layout.dialog_add_task, null);

        mEditText = (EditText) mDialogView.findViewById(R.id.editText_dialog_task);
        mSpinner = (Spinner) mDialogView.findViewById(R.id.spinner_dialog_task);

        mAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.priority_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(this);

        builder.setView(mDialogView).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        }).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String priority = (String) mSpinner.getSelectedItem();
                String todo = priority.substring(0,1) + mEditText.getText().toString();
                mListener.addDialogClicked(AddTaskDialogFragment.this, REQUEST_CODE_TODO, todo);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (AddDialogListener) activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        parent.getItemAtPosition(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        parent.getItemAtPosition(0);
    }
}
