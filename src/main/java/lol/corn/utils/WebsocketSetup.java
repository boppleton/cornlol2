package lol.corn.utils;

import lol.corn.exchange.bitmex.BitmexClient;
import java.net.URISyntaxException;

public class WebsocketSetup {

    private static BitmexClient bitmexclient;

    public WebsocketSetup() {
    }

    public static void bitmexConnect() throws URISyntaxException, InterruptedException {
        bitmexclient = new BitmexClient();
        bitmexclient.connectBlocking();
    }

    public static void bitmexSubscribe(String topic, String pair, boolean connect) {
        bitmexclient.subscribe(connect, topic, pair);
    }

}
