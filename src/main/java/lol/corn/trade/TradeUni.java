package lol.corn.trade;


import lol.corn.MainView;

import java.text.DecimalFormat;

public class TradeUni {

    public enum Direction {
        BUY, SELL;
    }

    private String exchangeName;
    private String instrument;
    private double size;
    private boolean side;
    private double price;
    private String timestamp;
    private String id;
    private boolean update = false;
    private String slip;


    private double firstPrice;
    private double lastPrice;


    private String sizeFormatted;

    public TradeUni() {

    }


    public TradeUni(String exchangeName, String instrument, double size, boolean side, double price, String timestamp, String id) {
        this.exchangeName = exchangeName;
        this.instrument = instrument;
        this.size = size;
        this.side = side;
        this.price = price;
        this.timestamp = timestamp;
        this.id = id;

    }

    public String getIcon() {

        String icon = "";

        switch (exchangeName) {
            case "bitmex":
                icon = "https://i.imgur.com/3LQBglR.png";
                break;
            case "bitfinex":
                icon = "https://i.imgur.com/7CNGpKm.png";
                break;
            case "okex":
                icon = "https://i.imgur.com/9jX2ikO.png";
                break;
            case "binance":
                icon = "https://i.imgur.com/nbRCbWo.png";
                break;
            default:
                icon = "";
                break;
        }

        return icon;
    }

    public String getSlipIcon() {
        //wow clean this lol

        double slipDub;
        String slip = "";
        String up = "https://i.imgur.com/n38F1Tw.png";
        String down = "https://i.imgur.com/uTbeZbt.png";

        slipDub = lastPrice-firstPrice;

        if (slipDub >= MainView.minSlipToShow) {
            slip = up;
            return slip;
        } else if (slipDub <= -MainView.minSlipToShow) {
            slip = down;
            return slip;
        }

        return slip;
//
//        System.out.println(lastPrice + " - " + firstPrice + "gap: " + gap);
//
//        String gapString;
//
//        System.out.println(gap);
////
//        if (slip == 0) {
//            gapString = "";
//        } else if (gap >= 1) {
//            gapString = String.format("+ %.1f", gap);
//        } else if (gap <= 1){
//            gapString = String.format("- %.1f", Math.abs(gap));
//        } else {
//            gapString = "";
//        }
//
//        return !gapString.equals("") ? String.format("%.1f (%s)", firstPrice, gapString) : String.format("%.1f", firstPrice);

    }



    public String getSlip() {

        double slipDub = lastPrice-firstPrice;

        String slip = "";

        if (slipDub >= MainView.minSlipToShow) {
            slip = String.format("%.0f", slipDub);
            return slip;
        } else if (slipDub <= -MainView.minSlipToShow) {
            slip = String.format("%.0f", -slipDub);
            return slip;
        } else {
            return slip;
        }
    }



    //remove thyslef?
    public String getPriceWithGap() {

        double gap = lastPrice-firstPrice;

        System.out.println(lastPrice + " - " + firstPrice + "gap: " + gap);

        String gapString;

        System.out.println(gap);

        if (gap == 0) {
            gapString = "";
        } else if (gap >= 1) {
            gapString = String.format("+ %.1f", gap);
        } else if (gap <= 1){
            gapString = String.format("- %.1f", Math.abs(gap));
        } else {
            gapString = "";
        }

        return !gapString.equals("") ? String.format("%.1f (%s)", firstPrice, gapString) : String.format("%.1f", firstPrice);

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
    public boolean getSide() {
        return side;
    }
    public void setSide(boolean side) {
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
