package lol.corn.exchange.bitmex;

import com.vaadin.flow.component.page.Push;
import lol.corn.exchange.Client;
import lol.corn.exchange.bitmex.dto.Trade;
import lol.corn.exchange.bitmex.dto.Trades;
import lol.corn.trade.Buncher;
import lol.corn.trade.TradeUni;
import lol.corn.utils.Broadcaster;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class BitmexClient extends Client {

    public static int minimumTrade;

    private static Buncher buncher = new Buncher();




    public BitmexClient() throws URISyntaxException {
        super(new URI("wss://www.bitmex.com/realtime/"));
    }

    public void subscribe(boolean connect, String topic, String pair) {
        send("{\"op\": \""   +(connect ? "" : "un")+   "subscribe\", \"args\": [\"" +topic+ ":" +pair+ "\"]}");
    }

    @Override
    public void onMessage(String message) {

        if (message.contains("\"table\":\"trade\",\"action\":\"insert\"")) {
            onMessageTrade(message);
        } else if (message.contains("orderBookL2")) {
//            onMessageOrderBook(message);
        } else if (message.contains("liquidation")) {
            onMessageLiq(message);
        } else {
            onMessageOther(message);
        }

    }

    private void onMessageTrade(String message) {

        Trades trades = null;

        try {

            //json mappin
            trades = mapper.readValue(message, Trades.class);

            List<Trade> tradeData = trades.getData();

//            calc total of trade bunch
            int total = 0;
//            for (int i = 0; i < tradeData.size(); i++) { total += tradeData.get(i).getSize(); }

            for (Trade trade : tradeData) {

                TradeUni t = new TradeUni();
                t.setExchangeName("bitmex");
                t.setInstrument("Perp XBTUSD");
                t.setSize(trade.getSize());
                t.setSide(trade.getSide());
                t.setPrice(trade.getPrice());
                t.setTimestamp(String.valueOf(trade.getTimestamp()));

                buncher.addToBuncher(t);

            }



            //sendd ittt
//            if (total >= 5000) {

//                System.out.println("broaccasting + " + total);
//                Broadcaster.broadcast("(bitmex XBTUSD)!" + tradeData.get(0).getSide() + "!$" + total + "$@" + tradeData.get(0).getPrice() + "@");
//            }

        } catch (Exception ee) {
            System.out.println(ee.getLocalizedMessage());
            System.out.println(message);
        }

    }

    private void onMessageLiq(String message) {
        System.out.println(message);
    }

    private void onMessageOther(String message) {
        System.out.println(message);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("bitmex onOpen");
        super.onOpen(handshakedata);
    }
}
