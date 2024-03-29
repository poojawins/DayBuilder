package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luke on 6/25/2015.
 */
public class NoScrollAdapter<T> {

    private Context mContext;
    private LayoutInflater inflater;
    private LinearLayout mParentLayout;
    private List<T> mList;
    private int mLayoutResource;
    private int numOfHighPriorities;
    private List<View> mChildViews;


    public NoScrollAdapter(Context mContext, LinearLayout mParentLayout, int mLayoutResource) {
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mParentLayout = mParentLayout;
        this.mLayoutResource = mLayoutResource;
        mChildViews = new ArrayList<>();
        numOfHighPriorities = 0;
    }

    public List<View> getChildViews() {
        return mChildViews;
    }

    public int getCount() {
        return mList.size();
    }

    public T getItem(int position) {
        return mList.get(position);
    }




    public void addStockViews(List<T> mList, boolean isRest) {
        this.mList = mList;

        if (!isRest) {
            mChildViews.clear();
            mParentLayout.removeAllViews();
        }


        for (int i = 0; i < mList.size(); i++) {
            final View row = inflater.inflate(mLayoutResource, null);


            TextView company = (TextView) row.findViewById(R.id.stock_company_name);
            TextView category = (TextView) row.findViewById(R.id.stock_category);
            TextView price = (TextView) row.findViewById(R.id.stock_price);
            TextView growth = (TextView) row.findViewById(R.id.stock_growth);

            Stock stock = (Stock) mList.get(i);

            company.setText(stock.getCompany());
            category.setText(stock.getTicker() + " (" + stock.getExchange() + ")");
            price.setText(stock.getPrice());
            String flux = stock.getGrowth();
            growth.setText(flux + " (" + stock.getPercentage() + "%)");
            if (flux.startsWith("+")) {
                growth.append(" ↑");
                growth.setTextColor(mContext.getResources().getColor(R.color.green));
            } else {
                growth.append(" ↓");
                growth.setTextColor(mContext.getResources().getColor(R.color.red));
            }


            mChildViews.add(row);
            mParentLayout.addView(row,i);

        }

    }




    public void addTaskViews(List<T> mList, boolean isRest) {
        this.mList = mList;
        if (!isRest) {
            mChildViews.clear();
            mParentLayout.removeAllViews();
            numOfHighPriorities = 0;
        }


        for (int i = 0; i < mList.size(); i++) {
            final View row = inflater.inflate(mLayoutResource, null);


            Button priority = (Button) row.findViewById(R.id.button_todo);
            TextView title = (TextView) row.findViewById(R.id.textView_todo);


            String task = (String) mList.get(i);

            String firstLetter = task.substring(0, 1);
            String sentence = task.substring(1);

            if (firstLetter.equals("H")) {
                priority.setBackgroundResource(R.drawable.button_high);
            } else if (firstLetter.equals("M")) {
                priority.setBackgroundResource(R.drawable.button_medium);
            } else {
                priority.setBackgroundResource(R.drawable.button_low);
            }

            priority.setText(firstLetter);
            title.setText(sentence);


            mChildViews.add(row);
            mParentLayout.addView(row,getTaskPosition(firstLetter));

        }
    }

    public int getTaskPosition(String firstLetter) {
        if (firstLetter.equals("H")) {
            numOfHighPriorities++;
            return 0;
        } else if (firstLetter.equals("L")) {
            if (mChildViews.size() == 0) {
                return 0;
            } else {
                return mChildViews.size() - 1;
            }
        } else {
            return numOfHighPriorities;
        }
    }

    public void addForecastViews(List<T> mList) {
        this.mList = mList;

        mChildViews.clear();
        mParentLayout.removeAllViews();

        for (int i = 0; i < mList.size(); i++) {
            final View column = inflater.inflate(mLayoutResource, null);

            TextView day = (TextView) column.findViewById(R.id.day);
            TextView high = (TextView) column.findViewById(R.id.high_temp);
            TextView low = (TextView) column.findViewById(R.id.low_temp);
            ImageView image = (ImageView) column.findViewById(R.id.image);

            Forecast forecast = (Forecast) getItem(i);

            day.setText(forecast.getDay().toUpperCase());
            high.setText(forecast.getHighTemp() + "°");
            low.setText(forecast.getLowTemp() + "°");
            Picasso.with(this.mContext).load("http://openweathermap.org/img/w/" + forecast.getIcon() + ".png")
                    .resize(100, 100).centerCrop().into(image);

            mParentLayout.addView(column,i);
        }

    }

    public void addEmptyMessageView() {
        mParentLayout.removeAllViews();
        View empty = inflater.inflate(R.layout.message_empty, null);
        mParentLayout.addView(empty, 0);
    }


    public void addNetworkWarningMessageView() {



        mParentLayout.removeAllViews(); // TODO: find a better way
        View network = inflater.inflate(R.layout.message_network, null);
        mParentLayout.addView(network, 0);

    }




}
