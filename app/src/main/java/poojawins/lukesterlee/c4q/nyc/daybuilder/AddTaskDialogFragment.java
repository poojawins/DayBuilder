package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Created by Luke on 6/27/2015.
 */
public class AddTaskDialogFragment extends DialogFragment {

    private static final String[] PRIORITY = new String[]{"High", "Medium", "Low"};

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

        builder.setView(mDialogView).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        }).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }


}
