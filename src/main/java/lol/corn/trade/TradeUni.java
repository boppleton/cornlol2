package lol.corn.trade;


import java.text.DecimalFormat;

public class TradeUni {

    public enum Direction {
        BUY, SELL;
    }

    private String exchangeName;
    private String instrument;
    private double size;
    private String side;
    private double price;
    private String timestamp;
    private String id;
    private boolean update = false;



    private double firstPrice;
    private double lastPrice;



    private String sizeFormatted;

    public TradeUni() {

    }


    public TradeUni(String exchangeName, String instrument, double size, String side, double price, String timestamp, String id) {
        this.exchangeName = exchangeName;
        this.instrument = instrument;
        this.size = size;
        this.side = side;
        this.price = price;
        this.timestamp = timestamp;
        this.id = id;

    }

    public String getPriceGap() {

        double gap = lastPrice-firstPrice;

        String gapFormatted;

        if (gap == 0) {
            gapFormatted = "";
        } else if (gap > 0) {
            gapFormatted = String.format("^ $%d", gap);
        } else {
            gapFormatted = String.format("v $%d", gap);
        }
        return gapFormatted;
    }


    // exchangName
    public String getExchangeName() { return exchangeName; }
    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }

    // instrument
    public String getInstrument() {
        return instrument;
    }
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    // size
    public double getSize() {
        return size;
    }
    public void setSize(double size) {
        this.size = size;
    }

    // side
    public String getSide() {
        return side;
    }
    public void setSide(String side) {
        this.side = side;
    }

    // price
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    // timestamp
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // id
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // update
    public boolean isUpdate() { return update; }
    public void setUpdate(boolean update) { this.update = update; }

    public String getSizeFormatted() { return sizeFormatted; }
    public void setSizeFormatted(String sizeFormatted) { this.sizeFormatted = sizeFormatted; }

    public double getFirstPrice() { return firstPrice; }
    public void setFirstPrice(double firstPrice) { this.firstPrice = firstPrice; }

    public double getLastPrice() { return lastPrice; }
    public void setLastPrice(double lastPrice) { this.lastPrice = lastPrice; }



}
