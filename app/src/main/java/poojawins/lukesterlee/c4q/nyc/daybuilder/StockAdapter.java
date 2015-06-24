package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Luke on 6/22/2015.
 */
public class StockAdapter extends BaseAdapter {

    // ftp://ftp.nasdaqtrader.com/SymbolDirectory/nasdaqlisted.txt
    // ftp://ftp.nasdaqtrader.com/SymbolDirectory/otherlisted.txt

    Context mContext;
    List<Stock> mList;

    public StockAdapter(Context mContext, List<Stock> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void refreshList(List<Stock> newList) {
        mList.clear();
        mList.addAll(newList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Stock getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item_stock, parent, false);

        TextView company = (TextView) row.findViewById(R.id.stock_company_name);
        TextView category = (TextView) row.findViewById(R.id.stock_category);
        TextView price = (TextView) row.findViewById(R.id.stock_price);
        TextView growth = (TextView) row.findViewById(R.id.stock_growth);

        Stock stock = getItem(position);

        company.setText(stock.getCompany());
        category.setText(stock.getTicker() + " (" + stock.getExchange() + ")");
        price.setText(stock.getPrice());
        growth.setText(stock.getGrowth() + " (" + stock.getPercentage() + "%)");


        return row;
    }


}
