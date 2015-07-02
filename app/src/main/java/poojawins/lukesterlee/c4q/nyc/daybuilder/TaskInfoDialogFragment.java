package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.philjay.circledisplay.CircleDisplay;

import java.text.DecimalFormat;


/**
 * Created by Luke on 7/1/2015.
 */
public class TaskInfoDialogFragment extends DialogFragment implements CircleDisplay.SelectionListener {

    private View mDialogView;
    private TextView mTextViewCompleted;
    private TextView mTextViewDeleted;
    private CircleDisplay mCircleDisplay;
    private int completed;
    private int deleted;
    private int total;
    private float percentage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle argument = getArguments();
        completed = argument.getInt("completed");
        deleted = argument.getInt("deleted");

        total = completed + deleted;

        if (total == 0) {
            percentage = 0;
        } else {
            percentage = (completed*100f)/total;
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mDialogView = inflater.inflate(R.layout.dialog_task_info, null);

        mTextViewCompleted = (TextView) mDialogView.findViewById(R.id.textView_todo_completed);
        mTextViewDeleted = (TextView) mDialogView.findViewById(R.id.textVIew_todo_deleted);
        mCircleDisplay = (CircleDisplay) mDialogView.findViewById(R.id.circleDisplay);

        mTextViewCompleted.setText(completed + "");
        mTextViewDeleted.setText(deleted + "");


        builder.setView(mDialogView);
        return builder.create();


    }

    @Override
    public void onResume() {
        super.onResume();


        mCircleDisplay.setAnimDuration(1500);
        mCircleDisplay.setTextSize(44f);
        mCircleDisplay.setColor(getResources().getColor(R.color.blue));
        mCircleDisplay.setDrawText(true);
        mCircleDisplay.setValueWidthPercent(10f);
        mCircleDisplay.setDrawInnerCircle(true);
        mCircleDisplay.setFormatDigits(1);
        mCircleDisplay.setTouchEnabled(false);
        mCircleDisplay.setSelectionListener(this);
        mCircleDisplay.setUnit("%");
        mCircleDisplay.setStepSize(0.5f);
        mCircleDisplay.setDimAlpha(0);
        mCircleDisplay.setFormatDigits(1);
        mCircleDisplay.showValue(percentage, 100f, true);

    }

    @Override
    public void onSelectionUpdate(float val, float maxval) {

    }

    @Override
    public void onValueSelected(float val, float maxval) {

    }
}
