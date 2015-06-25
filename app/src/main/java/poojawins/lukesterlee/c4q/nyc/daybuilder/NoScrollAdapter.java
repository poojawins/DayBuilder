package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luke on 6/25/2015.
 */
public class NoScrollAdapter<T> {

    Context mContext;
    LayoutInflater inflater;
    LinearLayout mParentLayout;
    List<T> mList;
    int mLayoutResource;

    List<View> mChildViews;

    boolean isOverFour;

    public NoScrollAdapter(Context mContext, LinearLayout mParentLayout, int mLayoutResource) {
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mParentLayout = mParentLayout;
        this.mLayoutResource = mLayoutResource;
        mChildViews = new ArrayList<>();

    }

    public int getCount() {
        return mList.size();
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    public void addStockViews(List<T> mList) {
        this.mList = mList;

        mParentLayout.removeAllViews();

        for (int i = 0; i < mList.size(); i++) {
            View row = inflater.inflate(mLayoutResource, null);


            TextView company = (TextView) row.findViewById(R.id.stock_company_name);
            TextView category = (TextView) row.findViewById(R.id.stock_category);
            TextView price = (TextView) row.findViewById(R.id.stock_price);
            TextView growth = (TextView) row.findViewById(R.id.stock_growth);

            Stock stock = (Stock) mList.get(i);

            company.setText(stock.getCompany());
            category.setText(stock.getTicker() + " (" + stock.getExchange() + ")");
            price.setText(stock.getPrice());
            growth.setText(stock.getGrowth() + " (" + stock.getPercentage() + "%)");

            mParentLayout.addView(row, i);

        }

    }


}
