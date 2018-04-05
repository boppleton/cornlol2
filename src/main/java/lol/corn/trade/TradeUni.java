package lol.corn.trade;



public class TradeUni {

    public enum Direction {
        BUY, SELL;
    }

    private String pair;

    private double size;

    private String side;

    public double getBtcAmt() {
        return btcAmt;
    }

    public void setBtcAmt(double btcAmt) {
        this.btcAmt = btcAmt;
    }

    private double btcAmt;

    private String timestamp;

    private double price;

    public TradeUni(String exchangeName, String side, double size, double btcAmt, double price) {
        this.pair = exchangeName;
        this.size = size;
        this.side = side;
        this.btcAmt = btcAmt;
        this.timestamp = timestamp;
        this.price = price;
    }



    // exchangeName
    public String getPair() {
        return pair;
    }
    public void setPair(String pair) {
        this.pair = pair;
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

}
