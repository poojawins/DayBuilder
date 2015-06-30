package poojawins.lukesterlee.c4q.nyc.daybuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pooja on 6/29/15.
 */
public class WeatherGetter {

    public WeatherGetter() {

    }

    public String getJSON(String api_url) throws IOException{
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(api_url);
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

}
