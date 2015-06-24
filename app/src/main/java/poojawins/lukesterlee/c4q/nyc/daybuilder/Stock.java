package poojawins.lukesterlee.c4q.nyc.daybuilder;

/**
 * Created by Luke on 6/22/2015.
 */
public class Stock {
    private String company;
    private String ticker;
    private String exchange;
    private String price;
    private String growth;
    private String percentage;

    public Stock() {

    }

    public Stock(String company, String ticker, String exchange, String price, String growth, String percentage) {
        this.company = company;
        this.ticker = ticker;
        this.exchange = exchange;
        this.price = price;
        this.growth = growth;
        this.percentage = percentage;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGrowth() {
        return growth;
    }

    public void setGrowth(String growth) {
        this.growth = growth;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
