package lol.corn.trade;



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

}
