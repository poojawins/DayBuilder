package poojawins.lukesterlee.c4q.nyc.daybuilder;

/**
 * Created by pooja on 6/29/15.
 */
public class Forecast {
    private String day;
    private String icon;
    private String highTemp;
    private String lowTemp;

    public Forecast() {

    }

    public Forecast(String day, String icon, String highTemp, String lowTemp) {
        this.day = day;
        this.icon = icon;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(String highTemp) {
        this.highTemp = highTemp;
    }

    public String getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(String lowTemp) {
        this.lowTemp = lowTemp;
    }
}
