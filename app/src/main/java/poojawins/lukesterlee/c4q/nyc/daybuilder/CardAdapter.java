package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Luke on 6/28/2015.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private static final int VIEW_TYPE_TITLE = 0;
    private static final int VIEW_TYPE_WEATHER = 1;
    private static final int VIEW_TYPE_TODO = 2;
    private static final int VIEW_TYPE_STOCK = 3;

    private LayoutInflater inflater;
    Context mContext;



    public CardAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card = null;
        switch (viewType) {
            case VIEW_TYPE_TITLE:
                card = inflater.inflate(R.layout.app_title_logo, parent, false);
                break;
            case VIEW_TYPE_WEATHER:
                card = inflater.inflate(R.layout.card_weather, parent, false);
                break;
            case VIEW_TYPE_TODO:
                card = inflater.inflate(R.layout.card_todo, parent, false);
                break;
            case VIEW_TYPE_STOCK:
                card = inflater.inflate(R.layout.card_stock, parent, false);
                break;
        }
        return new CardViewHolder(card, viewType);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        switch (position) {
            case VIEW_TYPE_TITLE:
                Typeface titleFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/kgsummersunshine.ttf");
                holder.mTextViewTitle.setTypeface(titleFont);
                holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.blue));
                break;
            case VIEW_TYPE_STOCK:
                holder.mButtonAddStock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "hey", Toast.LENGTH_LONG);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        // title
        private TextView mTextViewTitle;

        // weather

        // to do

        // stock
        private TextView mTextViewStockUpdate;
        private Button mButtonAddStock;
        private RecyclerView mRecyclerViewStock;


        public CardViewHolder(View itemView, int viewType) {
            super(itemView);

            switch (viewType) {
                case VIEW_TYPE_TITLE:
                    mTextViewTitle = (TextView) itemView.findViewById(R.id.textView_app_title);
                    break;
                case VIEW_TYPE_WEATHER:
                    break;
                case VIEW_TYPE_TODO:
                    //mButtonAdd = (Button) itemView.findViewById(R.id.button_todo_footer);
                    //mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView_todo);
                    break;
                case VIEW_TYPE_STOCK:
                    mTextViewStockUpdate = (TextView) itemView.findViewById(R.id.stock_update);
                    mButtonAddStock = (Button) itemView.findViewById(R.id.button_stock_footer);
                    mRecyclerViewStock = (RecyclerView) itemView.findViewById(R.id.recyclerView_stock);
                    break;
            }

        }

        public TextView getmTextViewTitle() {
            return mTextViewTitle;
        }

        public TextView getmTextViewStockUpdate() {
            return mTextViewStockUpdate;
        }

        public Button getmButtonAddStock() {
            return mButtonAddStock;
        }

        public RecyclerView getmRecyclerViewStock() {
            return mRecyclerViewStock;
        }
    }
}
