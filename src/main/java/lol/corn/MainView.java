package lol.corn;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.ColumnGroup;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.*;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.*;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lol.corn.exchange.binance.BinanceClient;
import lol.corn.exchange.bitfinex.BitfinexClient;
import lol.corn.exchange.bitmex.dto.Trade;
import lol.corn.exchange.okex.OkexClient;
import lol.corn.trade.TradeUni;
import lol.corn.utils.AmountFormat;
import lol.corn.utils.Broadcaster;
import lol.corn.utils.WebsocketSetup;

import java.awt.*;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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

    private static TextField minAmountField;


    private static int minAmount = 25000;


    public MainView() throws URISyntaxException, InterruptedException {
        setupLayout();

        setupTradesGrid();

        registerBroadcastListener();

//        bitfinexClient = new BitfinexClient();
//        bitfinexClient.connectBlocking();
//        bitfinexClient.subscribe(true, "trades", "BTCUSD");

//        okexClient = new OkexClient();
//        okexClient.connectBlocking();
//        okexClient.send("{'event':'addChannel','channel':'ok_sub_spot_btc_usdt_deals'}");
//        okexClient.send("{'event':'addChannel','channel':'ok_sub_futureusd_btc_trade_this_week'}");
//        okexClient.send("{'event':'addChannel','channel':'ok_sub_futureusd_btc_trade_next_week'}");
//        okexClient.send("{'event':'addChannel','channel':'ok_sub_futureusd_btc_trade_quarter'}");

        WebsocketSetup.bitmexConnect();
        WebsocketSetup.bitmexSubscribe("trade", "XBTUSD", true);
//        WebsocketSetup.bitmexSubscribe("trade", "XBTM18", true);
//        WebsocketSetup.bitmexSubscribe("trade", "XBTU18", true);

//        binanceClient = new BinanceClient("btcusdt@aggTrade");
//        binanceClient.connectBlocking();


        setClassName("main-layout");
    }


    private void setupLayout() {

        setSizeFull();
        setOrientation(Orientation.VERTICAL);
        addToPrimary(mainSplit);
        addToSecondary(new Button("bottom")); //put more for footer

        HorizontalLayout underTickerSettings = new HorizontalLayout();


        minAmountField = new TextField("minimum trade:");
        minAmountField.setPreventInvalidInput(true);
        minAmountField.setErrorMessage("errorlol");
        minAmountField.setPlaceholder("> 1000");
        minAmountField.addValueChangeListener(e -> updateMinTrade());
        minAmountField.setAutofocus(true);
        minAmountField.setWidth("100px");
        minAmountField.setValue(String.valueOf(minAmount));
        minAmountField.setId("min-amount");
        minAmountField.setPattern("[0-9]*");
        minAmountField.setPreventInvalidInput(true);
        minAmountField.setPrefixComponent(new Span("$"));



        Button exchangesButton = new Button("Exchanges");
        exchangesButton.getElement().setAttribute("theme", "bordered");
        Button settingsButton = new Button("Settings");
        settingsButton.setSizeUndefined();
        underTickerSettings.add(minAmountField, exchangesButton, settingsButton);
        underTickerSettings.setVerticalComponentAlignment(FlexComponent.Alignment.END, exchangesButton);
        underTickerSettings.setVerticalComponentAlignment(FlexComponent.Alignment.END, settingsButton);

        sideGridSplit.setSizeFull();
        sideGridSplit.setOrientation(Orientation.VERTICAL);
        sideGridSplit.addToPrimary(tradesGrid);
        sideGridSplit.addToSecondary(underTickerSettings);
        sideGridSplit.setSplitterPosition(90);

        mainSplit.setSizeFull();
        mainSplit.addToPrimary(sideGridSplit);
        mainSplit.addToSecondary(new ComboBox<String>());
        mainSplit.setSplitterPosition(20);
    }

    private void updateMinTrade() {

        if (!minAmountField.getValue().isEmpty()) {
            double filtertxt = Double.parseDouble(minAmountField.getValue());
            if (filtertxt > 1000) {
                minAmount = Integer.parseInt(minAmountField.getValue());
            } else {
                minAmountField.setValue("1000");
                setMinAmount(1000);
            }
        } else {
            System.out.println("filtertext null?");
        }
    }


    public void setMinAmount(int minAmount) {
        MainView.minAmount = minAmount;
    }

    public static int getMinAmount() {
        return minAmount;
    }

    private void setupTradesGrid() {
        tradesGrid.setItems(trades);

        ValueProvider<TradeUni, String> cssClassProvider = (tradeUni) -> {
            String cssClass = "my-grid-cell";
            if (tradeUni.getSize() < 10000) {
                cssClass += " underten";
            } else if (tradeUni.getSize() >= 10000) {
                cssClass += " overten";
            }
            return cssClass;
        };


        tradesGrid.addColumn(TemplateRenderer.<TradeUni>
                of("<div class$=\"[[item.class]]\">[[item.size]]</div>")
                .withProperty("class", cssClassProvider)
                .withProperty("size", TradeUni::getSize));
//
//        tradesGrid.addColumn(TemplateRenderer.<TradeUni> of("<b>[[item.side]]</b><i>[[item.price]]</i>")
//                .withProperty("side", TradeUni::getSide)
//                .withProperty("price", TradeUni::getPrice))
//                .setHeader("Side");

//        Grid.Column<TradeUni> sideColumn = tradesGrid.addColumn(TradeUni::getSide).setResizable(true).setHeader("side").setFlexGrow(0);
//        Grid.Column<TradeUni> sizeColumn = tradesGrid.addColumn(TradeUni::getSizeFormatted).setResizable(true).setHeader("amount").setFlexGrow(0);
        Grid.Column<TradeUni> exchangeNameColumn = tradesGrid.addColumn(TradeUni::getExchangeName).setResizable(true).setHeader("exhange").setResizable(true).setFlexGrow(0);
        Grid.Column<TradeUni> priceColumn = tradesGrid.addColumn(TradeUni::getPriceWithGap).setResizable(true).setHeader("price").setResizable(true).setFlexGrow(0);
        Grid.Column<TradeUni> instrumentColumn = tradesGrid.addColumn(TradeUni::getInstrument).setResizable(true).setHeader("instrument").setFlexGrow(0);
//
//        tradesGrid.addColumn(new NumberRenderer<>(TradeUni::getPrice,
//                NumberFormat.getCurrencyInstance())).setHeader("Price");








//        tradesGrid.addColumn(TradeUni::getFirstPrice).setHeader("Trade Price").setResizable(true);
//        tradesGrid.addColumn(TradeUni::getFirstPrice).setHeader("firstprice").setResizable(true).setWidth("24%");
//        tradesGrid.addColumn(TradeUni::getLastPrice).setHeader("lastprice").setResizable(true).setWidth("24%");

//        tradesGrid.addColumn(TradeUni::getPriceGap).setHeader("Slippage").setResizable(true);
//        tradesGrid.addColumn(TradeUni::getTimestamp).setHeader("Time").setResizable(true);



        tradesGrid.setColumnReorderingAllowed(true);

        tradesGrid.setClassName("gridclass");

    }

    private void addTrade(String message, boolean update) {


        double size = Double.parseDouble(message.substring(message.indexOf("#") + 1, message.lastIndexOf("#")));
        String exchangeName = message.substring(message.indexOf("%") + 1, message.lastIndexOf("%"));
        String instrument = message.substring(message.indexOf("<") + 1, message.lastIndexOf(">"));
        String side = message.substring(message.indexOf("!") + 1, message.lastIndexOf("!"));
        double price = Double.parseDouble(message.substring(message.indexOf("@") + 1, message.lastIndexOf("@")));
        String timestamp = message.substring(message.indexOf("*") + 1, message.lastIndexOf("*"));

        double firstPrice = Double.parseDouble(message.substring(message.indexOf("~") + 1, message.lastIndexOf("~")));
        System.out.println(firstPrice);
        double lastPrice = Double.parseDouble(message.substring(message.indexOf("=") + 1, message.lastIndexOf("=")));
        System.out.println(lastPrice);

        TradeUni t = new TradeUni(exchangeName, instrument, size, side, price, timestamp, "id");
        t.setUpdate(update);
        t.setSizeFormatted(AmountFormat.coolFormat(t.getSize(), 0));
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

                if (trades.size() >= 250) {
                    trades.remove(trades.size()-1);
                    tradesGrid.getElement().getNode().markAsDirty();
                    tradesGrid.getDataProvider().refreshAll();
                }
            });
        }
    }

    @Override
    public void receiveBroadcast(String message) {
        addTrade(message, message.substring(0, 1).equals("u"));
    }

    private void registerBroadcastListener() {
        Broadcaster.register(this);
    }
}
