package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Luke on 6/29/2015.
 */
public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<Stock> mList;
    private int size;

    public StockAdapter(Context mContext, List<Stock> mList) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mList = mList;
        size = mList.size();
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View stock = inflater.inflate(R.layout.list_item_stock, parent, false);

        return new StockViewHolder(stock);
    }

    @Override
    public void onBindViewHolder(StockViewHolder stockViewHolder, int position) {

        Stock stock = mList.get(position);


        stockViewHolder.company.setText(stock.getCompany());
        stockViewHolder.category.setText(stock.getTicker() + " (" + stock.getExchange() + ")");
        stockViewHolder.price.setText(stock.getPrice());
        String flux = stock.getGrowth();
        stockViewHolder.growth.setText(flux + " (" + stock.getPercentage() + "%)");
        if (flux.startsWith("+")) {
            stockViewHolder.growth.append(" ↑");
            stockViewHolder.growth.setTextColor(mContext.getResources().getColor(R.color.green));
        } else {
            stockViewHolder.growth.append(" ↓");
            stockViewHolder.growth.setTextColor(mContext.getResources().getColor(R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void setItemSize(int size) {
        this.size = size;
    }

    public class StockViewHolder extends RecyclerView.ViewHolder {

        TextView company;
        TextView category;
        TextView price;
        TextView growth;

        public StockViewHolder(View itemView) {
            super(itemView);
            company = (TextView) itemView.findViewById(R.id.stock_company_name);
            category = (TextView) itemView.findViewById(R.id.stock_category);
            price = (TextView) itemView.findViewById(R.id.stock_price);
            growth = (TextView) itemView.findViewById(R.id.stock_growth);

        }
    }
}
