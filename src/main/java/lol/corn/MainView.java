package lol.corn;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.*;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lol.corn.exchange.binance.BinanceClient;
import lol.corn.exchange.bitfinex.BitfinexClient;
import lol.corn.exchange.bitmex.dto.Trade;
import lol.corn.exchange.okex.OkexClient;
import lol.corn.trade.TradeUni;
import lol.corn.utils.Broadcaster;
import lol.corn.utils.WebsocketSetup;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Push
@BodySize(height = "100vh", width = "100vw")
@HtmlImport("styles/shared-styles.html")
@Route("")
@Theme(Lumo.class)
public class MainView extends SplitLayout implements Broadcaster.BroadcastListener {

    private static SplitLayout mainSplit = new SplitLayout();
    private static SplitLayout sideGridSplit = new SplitLayout();
    private static Grid<TradeUni> tradesGrid = new Grid<>();
    private static List<TradeUni> trades = new LinkedList<>();

    private static BinanceClient binanceClient;
    private static BitfinexClient bitfinexClient;
    private static OkexClient okexClient;

    public MainView() throws URISyntaxException, InterruptedException {
        setupLayout();

        setupTradesGrid();

        registerBroadcastListener();

        bitfinexClient = new BitfinexClient();
        bitfinexClient.connectBlocking();
        bitfinexClient.subscribe(true, "trades", "BTCUSD");

        okexClient = new OkexClient();
        okexClient.connectBlocking();
        okexClient.send("{'event':'addChannel','channel':'ok_sub_spot_btc_usdt_deals'}");
        okexClient.send("{'event':'addChannel','channel':'ok_sub_futureusd_btc_trade_this_week'}");
        okexClient.send("{'event':'addChannel','channel':'ok_sub_futureusd_btc_trade_next_week'}");
        okexClient.send("{'event':'addChannel','channel':'ok_sub_futureusd_btc_trade_quarter'}");

        WebsocketSetup.bitmexConnect();
        WebsocketSetup.bitmexSubscribe("trade", "XBTUSD", true);
        WebsocketSetup.bitmexSubscribe("trade", "XBTM18", true);
        WebsocketSetup.bitmexSubscribe("trade", "XBTU18", true);




        setClassName("main-layout");
    }

    private void setupLayout() {

        setSizeFull();
        setOrientation(Orientation.VERTICAL);
        addToPrimary(mainSplit);
        addToSecondary(new Button("bottom")); //put more for footer

        sideGridSplit.setSizeFull();
        sideGridSplit.setOrientation(Orientation.VERTICAL);
        sideGridSplit.addToPrimary(tradesGrid);
        sideGridSplit.addToSecondary(new Button("settings"));
        sideGridSplit.setSplitterPosition(90);

        mainSplit.setSizeFull();
        mainSplit.addToPrimary(sideGridSplit);
        mainSplit.addToSecondary(new ComboBox<String>());
        mainSplit.setSplitterPosition(17);
    }

    private void setupTradesGrid() {

        tradesGrid.setItems(trades);
        tradesGrid.addColumn(TradeUni::getSizeFormatted).setHeader("Amount").setResizable(true);
        tradesGrid.addColumn(TradeUni::getExchangeName).setHeader("Exchange").setResizable(true);
        tradesGrid.addColumn(TradeUni::getInstrument).setHeader("Instrument").setResizable(true);
        tradesGrid.addColumn(TradeUni::getPrice).setHeader("Price").setResizable(true);
        tradesGrid.addColumn(TradeUni::getTimestamp).setHeader("Time").setResizable(true);
        tradesGrid.addColumn(TradeUni::getFirstPrice).setHeader("firstprice").setResizable(true);
        tradesGrid.addColumn(TradeUni::getLastPrice).setHeader("lastprice").setResizable(true);
    }

    private void addTrade(String message, boolean update) {



        String exchangeName = message.substring(message.indexOf("%") + 1, message.lastIndexOf("%"));
        String instrument = message.substring(message.indexOf("<") + 1, message.lastIndexOf(">"));
        String side = message.substring(message.indexOf("!") + 1, message.lastIndexOf("!"));
        double size = Double.parseDouble(message.substring(message.indexOf("$") + 1, message.lastIndexOf("$")));
        double price = Double.parseDouble(message.substring(message.indexOf("@") + 1, message.lastIndexOf("@")));
        String timestamp = message.substring(message.indexOf("*") + 1, message.lastIndexOf("*"));

        double firstPrice = Double.parseDouble(message.substring(message.indexOf("^") + 1, message.lastIndexOf("^")));
        double lastPrice = Double.parseDouble(message.substring(message.indexOf("=") + 1, message.lastIndexOf("=")));



        TradeUni t = new TradeUni(exchangeName, instrument, size, side, price, timestamp, "id");
        t.setUpdate(update);
        t.setSizeFormatted(coolFormat(t.getSize(), 0));
        t.setFirstPrice(firstPrice);
        t.setLastPrice(lastPrice);

        sendTradeUni(t);
    }






    private void sendTradeUni(TradeUni t) {

        if (getUI().isPresent()) {

            getUI().get().access((Command) () -> {

                if (!t.isUpdate()) {

                    trades.add(0, t);
                    tradesGrid.getElement().getNode().markAsDirty();
                    tradesGrid.getDataProvider().refreshAll();

                } else {
                    System.out.println("removing index 0 trade");
                    trades.remove(0);
                    System.out.println("adding updated trade");
                    trades.add(0, t);

                    tradesGrid.getElement().getNode().markAsDirty();
                    tradesGrid.getDataProvider().refreshAll();
                }
            });
        }
    }

//    private void startWebsocket() {
//
//        try {
//            WebsocketSetup.bitmexStreamTrade("XBTUSD");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private static String coolFormat(double n, int iteration) {

        String[] c = new String[]{"k", "mil"};

        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration + 1));
    }

    @Override
    public void receiveBroadcast(String message) {

            addTrade(message, message.substring(0,1).equals("u"));

    }




    private void registerBroadcastListener() {
        Broadcaster.register(this);
    }
}
