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
//    String JSON_WEATHER_LATLON = "http://api.openweathermap.org/data/2.5/weather?lat=";
//    String JSON_COORDINATE_END = "&lon=";
    private static final String JSON_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?zip=11206";

    public WeatherGetter() {

    }

    public String getJSON() throws IOException{
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(JSON_WEATHER_URL);
//            URL url = new URL(JSON_WEATHER_LATLON + latitude + JSON_COORDINATE_END + longitude);
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
