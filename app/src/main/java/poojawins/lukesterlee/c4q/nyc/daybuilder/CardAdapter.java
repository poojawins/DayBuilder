package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Luke on 6/24/2015.
 */
public class CardAdapter extends BaseAdapter {

    Context mContext;

    private static final int CARD_WEATHER_FLAG = 0;
    private static final int CARD_TODO_FLAG = 1;
    private static final int CARD_STOCK_FLAG = 2;

    public CardAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = null;
        switch (position) {
            case CARD_WEATHER_FLAG:
                row = inflater.inflate(R.layout.card_weather, parent, false);
                break;
            case CARD_TODO_FLAG:
                row = inflater.inflate(R.layout.card_todo, parent, false);
                break;
            case CARD_STOCK_FLAG:
                row = inflater.inflate(R.layout.card_stock, parent, false);
                break;
        }
        return row;
    }


}
