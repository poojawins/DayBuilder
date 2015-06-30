package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

/**
 * Created by Luke on 6/30/2015.
 */
public class SplashDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View splash = inflater.inflate(R.layout.activity_splash, null);
        ImageView app = (ImageView) splash.findViewById(R.id.imageView_splash);
        Picasso.with(getActivity()).load(R.drawable.c4qrainbow).resize(500, 500).into(app);
        builder.setView(splash);
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 3000);

    }
}
