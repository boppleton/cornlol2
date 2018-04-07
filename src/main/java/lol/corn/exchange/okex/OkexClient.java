package lol.corn.exchange.okex;

import lol.corn.exchange.Client;
import lol.corn.exchange.okex.dto.Spots;
import lol.corn.trade.Buncher;
import lol.corn.trade.TradeUni;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class OkexClient extends Client {//todo: remove first pushes by checking for the .0 at the end of amt

    private static Buncher buncher = new Buncher();



    public OkexClient() throws URISyntaxException {
        super(new URI("wss://real.okex.com:10440/websocket/okexapi"));
    }


    public void subscribe(boolean connect, String topic, String pair) {
        send("{\"op\": \"" + (connect ? "" : "un") + "subscribe\", \"args\": [\"" + topic + ":" + pair + "\"]}");
    }


    @Override
    public void onMessage(String message) {


//        System.out.println(message);

//
        if (message.contains("deals\",\"data\":")) {

            onMessageTrade(message);

        } else if (message.contains("\"binary\":0,\"channel\":\"ok_sub_future")) {
            onMessageTrade(message);
        } else if (message.contains("\"addChannel\",\"data\":{\"result\":true")) {
            System.out.println("okex channel opened: " + message);
//            System.out.println(message);
//        } else if (message.contains("liquidation")) {
////            onMessageLiq(message);
        } else {
            onMessageOther(message);
        }



    }



    private void onMessageTrade(String message) {

        Spots[] spots = null;

        try {

//            System.out.println("\n\n" + message);

            spots = mapper.readValue(message, Spots[].class);

            List<List<String>> trades = spots[0].getData();



//            System.out.print("trade:");
//            int amt = 0;
//
            for (int i = 0; i < trades.size(); i++) {


                    TradeUni t = new TradeUni();
                    t.setSide(trades.get(i).get(4));
                    t.setPrice(Double.valueOf(trades.get(i).get(1)));
                    t.setSize(message.contains("future") ? (Double.parseDouble(trades.get(i).get(2))*100) : Double.parseDouble(trades.get(i).get(2))* t.getPrice());
                    t.setTimestamp(trades.get(i).get(3));
                    t.setId(trades.get(i).get(0));
                    t.setExchangeName("okex");
                    buncher.addToBuncher(t);



//
////                System.out.print(" trade- " + trades.get(i).get(1) + " amt: " + (message.contains("future") ? Double.valueOf(trades.get(i).get(2))*100 : Double.valueOf(trades.get(i).get(2))*Double.valueOf(trades.get(i).get(1))) + " type: " + trades.get(i).get(4));
//
//                System.out.printf(" %.1f", message.contains("future") ? Double.valueOf(trades.get(i).get(2))*100 : Double.valueOf(trades.get(i).get(2))*Double.valueOf(trades.get(i).get(1)));
//
            }
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
        System.out.println("okex onOpen()");
        super.onOpen(handshakedata);
    }
}
