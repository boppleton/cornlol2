package lol.corn.exchange.binance;

import lol.corn.exchange.Client;
import lol.corn.exchange.binance.dto.AggTrade;
import lol.corn.trade.Buncher;
import lol.corn.trade.TradeUni;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;

public class BinanceClient extends Client {

    private static Buncher buncher = new Buncher();



    public BinanceClient(String stream) throws URISyntaxException {
        super(new URI("wss://stream.binance.com:9443/ws/" + stream));
    }


    @Override
    public void onMessage(String message) {

        if (message.contains("\"e\":\"aggTrade\",")) {
            onMessageTrade(message);
//        } else if (message.contains("orderBookL2")) {
////            onMessageOrderBook(message);
//        } else if (message.contains("liquidation")) {
////            onMessageLiq(message);
        } else {
            onMessageOther(message);
        }

//            System.out.println(message);


    }


    private void onMessageTrade(String message) {

        AggTrade aggTrade = null;

        try {

//            System.out.println("\n" + message);

            aggTrade = mapper.readValue(message, AggTrade.class);

//            System.out.println(aggTrade.getQuantity());

//            double total = Double.parseDouble(aggTrade.getQuantity());


            TradeUni t = new TradeUni("binance", aggTrade.getSymbol(), (Double.parseDouble(aggTrade.getQuantity())*Double.parseDouble(aggTrade.getPrice())), aggTrade.getSide(), Double.parseDouble(aggTrade.getPrice()), aggTrade.getEventTime().toString(), "id");


            buncher.addToBuncher(t);

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
        System.out.println("binance onOpen");
        super.onOpen(handshakedata);
    }
}
