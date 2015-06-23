package poojawins.lukesterlee.c4q.nyc.daybuilder.stock;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import poojawins.lukesterlee.c4q.nyc.daybuilder.R;

/**
 * Created by Luke on 6/22/2015.
 */
public class CSVGetter {

    Context mContext;
    String userSearchInput;
    int size;

    public CSVGetter(Context mContext, String userSearchInput) {
        this.mContext = mContext;
        this.userSearchInput = userSearchInput;
        size = 0;
    }

    public List<String> getLines() {
        List<String> lines = new ArrayList<>();

        InputStream stream = mContext.getResources().openRawResource(R.raw.stock_list);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        userSearchInput = userSearchInput.toLowerCase();
        try {

            while ((line = reader.readLine()) != null && size <= 20) {
                String line2 = line.toLowerCase();
                if (line2.contains(userSearchInput)) {
                    lines.add(line);
                    size++;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return lines;
    }
}
