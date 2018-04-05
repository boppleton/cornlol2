package lol.corn.utils;

import lol.corn.exchange.bitmex.BitmexClient;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class WebsocketSetup {

    private static BitmexClient bitmexclient;

    public WebsocketSetup() {

    }

    public static void startStream() throws URISyntaxException, InterruptedException {
        bitmexclient = new BitmexClient();
        bitmexclient.connectBlocking();
        bitmexclient.subscribe(true, "trade", "XBTUSD");
    }
}
