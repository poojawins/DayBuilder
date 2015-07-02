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
import java.util.Map;

/**
 * Created by Luke on 7/1/2015.
 */
public class ArticleGetter {

    private static final String TAG = "ArticleGetter";
    private static final String JSON_NYT_WORLD_ENDPOINT = "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/world/7.json?api-key=3f91a526526725b63f921c029e209d73:2:71503619";
    private static final String JSON_NYT_US_ENDPOINT = "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/u.s./7.json?api-key=3f91a526526725b63f921c029e209d73:2:71503619";
    private static final String JSON_NYT_OPINION_ENDPOINT = "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/opinion/7.json?api-key=3f91a526526725b63f921c029e209d73:2:71503619";
    private static final String JSON_NYT_TECH_ENDPOINT = "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/technology/7.json?api-key=3f91a526526725b63f921c029e209d73:2:71503619";

    private String jsonWorld = "";
    private String jsonUs = "";
    private String jsonOpinion = "";
    private String jsonTech = "";

    public ArticleGetter() {
        try {
            jsonWorld = getJsonString(JSON_NYT_WORLD_ENDPOINT);
            jsonUs = getJsonString(JSON_NYT_US_ENDPOINT);
            jsonOpinion = getJsonString(JSON_NYT_OPINION_ENDPOINT);
            jsonTech = getJsonString(JSON_NYT_TECH_ENDPOINT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getJsonString(String jsonUrl) throws IOException {
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

    public Map<String, Article> getArticleList() throws IOException, JSONException {
        Map<String, Article> list = new HashMap<>();

        list.put("World", getArticle(jsonWorld));
        list.put("Us", getArticle(jsonUs));
        list.put("Opinion", getArticle(jsonOpinion));
        list.put("Tech", getArticle(jsonTech));

        return list;
    }

    private Article getArticle(String jsonString) throws JSONException {
        JSONObject object = new JSONObject(jsonString);

        JSONArray results = object.getJSONArray("results");
        JSONObject first = results.getJSONObject(0);
        String title = first.getString("title");
        String description = first.getString("abstract");
        String published_date = first.getString("published_date");
        String articleUrl = first.getString("url");

        JSONArray media = first.getJSONArray("media");
        JSONObject firstMedia = media.getJSONObject(0);
        JSONArray metadata = firstMedia.getJSONArray("media-metadata");
        JSONObject firstItem = metadata.getJSONObject(1);
        String thumbnailUrl = firstItem.getString("url");

        return new Article(title, description, published_date, articleUrl, thumbnailUrl);

    }




}
