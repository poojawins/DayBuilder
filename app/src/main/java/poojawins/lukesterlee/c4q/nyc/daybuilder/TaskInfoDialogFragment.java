package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;


/**
 * Created by Luke on 7/1/2015.
 */
public class TaskInfoDialogFragment extends DialogFragment {

    private View mDialogView;
    private TextView mTextViewCompleted;
    private TextView mTextViewDeleted;
    private TextView mTextViewPercentage;
    private int completed;
    private int deleted;
    private double total;
    private String percentage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle argument = getArguments();
        completed = argument.getInt("completed");
        deleted = argument.getInt("deleted");

        total = completed + deleted + 0.0;

        if (total == 0) {
            percentage = "0%";
        } else {
            DecimalFormat df = new DecimalFormat("#.#");
            percentage = df.format((completed / total) * 100) + "%";
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mDialogView = inflater.inflate(R.layout.dialog_task_info, null);

        mTextViewCompleted = (TextView) mDialogView.findViewById(R.id.textView_todo_completed);
        mTextViewDeleted = (TextView) mDialogView.findViewById(R.id.textVIew_todo_deleted);
        mTextViewPercentage = (TextView) mDialogView.findViewById(R.id.percentage);

        mTextViewCompleted.setText(completed + "");
        mTextViewDeleted.setText(deleted + "");
        mTextViewPercentage.setText(percentage);


        builder.setView(mDialogView);
        return builder.create();


    }

    @Override
    public void onResume() {
        super.onResume();


    }
}
