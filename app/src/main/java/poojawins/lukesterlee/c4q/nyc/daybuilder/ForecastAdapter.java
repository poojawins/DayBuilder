package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by pooja on 6/30/15.
 */
public class ForecastAdapter extends ArrayAdapter<Forecast> {
    private ArrayList<Forecast> data;

    public ForecastAdapter(Context context, int textViewResourceId, ArrayList<Forecast> data) {
        super(context, textViewResourceId, data);
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_forecast, null);
        }

        Forecast i = data.get(position);

        if (i != null) {
            TextView day = (TextView) v.findViewById(R.id.day);
            TextView high = (TextView) v.findViewById(R.id.high_temp);
            TextView low = (TextView) v.findViewById(R.id.low_temp);
            ImageView image = (ImageView) v.findViewById(R.id.image);

            if (day != null) {
                day.setText(i.getDay());
            }

            if (high != null) {
                high.setText(i.getHighTemp());
            }

            if (low != null) {
                low.setText(i.getLowTemp());
            }

            if (image != null) {
                Picasso.with(this.getContext()).load("http://openweathermap.org/img/w/" + i.getIcon() + ".png")
                        .resize(125, 125).centerCrop().into(image);
            }

        }

        return v;
    }

}
