package lol.corn.exchange.bitfinex;

import lol.corn.exchange.Client;
import lol.corn.exchange.bitfinex.dto.TradeUpdate;
import lol.corn.trade.Buncher;
import lol.corn.trade.TradeUni;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class BitfinexClient extends Client {

    private static Buncher buncher = new Buncher();


    public BitfinexClient() throws URISyntaxException {
        super(new URI("wss://api.bitfinex.com/ws/"));
    }


    public void subscribe(boolean connect, String topic, String pair) {
        send("{\n" +
                "  \"event\": \"subscribe\",\n" +
                "  \"channel\": \"" + topic + "\",\n" +
                "  \"pair\": \"" + pair + "\"\n" +
                "}");
    }


    @Override
    public void onMessage(String message) {


//        System.out.println("msg: " + message);


        if (message.contains("tu")) {

            onMessageTrade(message);

        }

//        } else if (!message.contains("te") && !message.contains("hb")) {
//            onMessageOther(message);
////        } else if (message.contains("\"addChannel\",\"data\":{\"result\":true")) {
////            System.out.println("okex channel opened: " + message);
//////            System.out.println(message);
//////        } else if (message.contains("liquidation")) {
////////            onMessageLiq(message);
//        } else {
//            onMessageOther(message);
//        }


    }


    private void onMessageTrade(String message) {

        TradeUpdate tu = null;

        try {

            System.out.println("\n\n" + message);

            tu = mapper.readValue(message, TradeUpdate.class);


//            System.out.println("+ < " + tu.startNum + tu.tu + " seq:" + tu.seq + " id: " + tu.tradeId + " time: " + tu.timestamp + " price: " + tu.price + " amt: " + tu.amount);


//            System.out.print("trade:");
//            int amt = 0;
//
//            for (int i = 0; i < trades.size(); i++) {
//
//
            TradeUni t = new TradeUni();
            t.setExchangeName("bitfinex");
            t.setInstrument("btcusd");
            t.setSize(Math.abs(tu.amount) * tu.price);
            t.setSide(tu.amount > 0 ? "buy" : "sell");
            t.setPrice(tu.price);
            t.setTimestamp(String.valueOf(tu.timestamp));
            t.setId(String.valueOf(tu.tradeId));

            buncher.addToBuncher(t);


//
////                System.out.print(" trade- " + trades.get(i).get(1) + " amt: " + (message.contains("future") ? Double.valueOf(trades.get(i).get(2))*100 : Double.valueOf(trades.get(i).get(2))*Double.valueOf(trades.get(i).get(1))) + " type: " + trades.get(i).get(4));
//
//                System.out.printf(" %.1f", message.contains("future") ? Double.valueOf(trades.get(i).get(2))*100 : Double.valueOf(trades.get(i).get(2))*Double.valueOf(trades.get(i).get(1)));
//

//            System.out.println();


        } catch (Exception ee) {
            System.out.println(ee.getLocalizedMessage());
            System.out.println(message);
        }

    }


    private void onMessageOther(String message) {
        System.out.println(message);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("bitfinex onOpen()");
        super.onOpen(handshakedata);
    }
}
