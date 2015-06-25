package poojawins.lukesterlee.c4q.nyc.daybuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Luke on 6/22/2015.
 */
public class StocksGetter {

    private static final String TAG = "StocksGetter";

    private String jsonUrl = "";

    private HashMap<String, String> stockNames;

    private static final String JSON_STOCK_ENDPOINT = "http://finance.google.com/finance/info?client=ig&q=";

    public StocksGetter(String jsonUrl, HashMap<String, String> stockNames) {
        this.jsonUrl = jsonUrl;
        this.stockNames = stockNames;
        stockNames.put("GOOGL", "Google Inc.");
    }

    public String getJsonString() throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(jsonUrl);
            connection = (HttpURLConnection) url.openConnection();
            inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }

            return builder.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public List<Stock> getStocksList() throws IOException, JSONException {
        List<Stock> stocks = new ArrayList<>();

        String jsonString = getJsonString().substring(3);
        JSONArray array = new JSONArray(jsonString);

        for (int i = 0; i < array.length(); i++) {
            Stock stock = new Stock();
            JSONObject item = array.getJSONObject(i);
            stock.setTicker(item.getString("t"));
            stock.setExchange(item.getString("e"));
            stock.setPrice(item.getString("l"));
            stock.setGrowth(item.getString("c"));
            stock.setPercentage(item.getString("cp"));
            stock.setCompany(stockNames.get(stock.getTicker()));
            stocks.add(stock);
        }


        return stocks;
    }


}
