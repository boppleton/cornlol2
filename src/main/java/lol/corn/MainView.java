package lol.corn;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnGroup;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
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

import com.vaadin.flow.component.dialog.*;
import java.awt.*;

import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    public static double minSlipToShow = .5;


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

        binanceClient = new BinanceClient("btcusdt@aggTrade");
        binanceClient.connectBlocking();


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



        Button settingsButton = new Button("+");
        settingsButton.setSizeUndefined();
        Dialog dialog = new Dialog();

        ComboBox<String> combobox = new ComboBox<>();
        combobox.setItems("first", "sendonc", "third");
        dialog.add(combobox);


        settingsButton.addClickListener(event -> dialog.open());

        ComboBox<String> instrumentsComboBox = new ComboBox<>();
        instrumentsComboBox.setLabel("instruments:");


        underTickerSettings.add(minAmountField, instrumentsComboBox, settingsButton);
        underTickerSettings.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        sideGridSplit.setSizeFull();
        sideGridSplit.setOrientation(Orientation.VERTICAL);
        sideGridSplit.addToPrimary(tradesGrid);
        sideGridSplit.addToSecondary(underTickerSettings);
        sideGridSplit.setSplitterPosition(90);

        SplitLayout gridAndTop = new SplitLayout();
        gridAndTop.setOrientation(Orientation.VERTICAL);
        Button topTestVolBtn = new Button("volume here");

        gridAndTop.addToPrimary(topTestVolBtn);
        gridAndTop.addToSecondary(sideGridSplit);



        mainSplit.setSizeFull();
        mainSplit.addToPrimary(gridAndTop);
        mainSplit.addToSecondary(new Label("right side"));
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



    private void setupTradesGrid() {
        tradesGrid.setItems(trades);


        ValueProvider<TradeUni, String> cssClassProvider = (tradeUni) -> {
            String cssClass = "my-grid-cell";
            if (tradeUni.getSide()) {
                cssClass += " buy";
            } else if (!tradeUni.getSide()) {
                cssClass += " sell";
            }
            if (tradeUni.getSize() > 200000) {
                cssClass += " overthree";
                return cssClass;
            } else if  (tradeUni.getSize() > 30000) {
                cssClass += " overtwo";
                return cssClass;
            } else if  (tradeUni.getSize() > 10000) {
                cssClass += " overone";
                return cssClass;
            }
            return cssClass;
        };




        TemplateRenderer<TradeUni> sizee = TemplateRenderer.<TradeUni>
                of("<div class$='[[item.class]]'><img src='[[item.icon]]'> [[item.size]] <i><small>[[item.price]] <img src='[[item.slipicon]]'>[[item.slip]]</small></i></div>")
                .withProperty("class", cssClassProvider)
                .withProperty("icon", TradeUni::getIcon)
                .withProperty("size", TradeUni::getSizeFormatted)
                .withProperty("slipicon", TradeUni::getSlipIcon)
                .withProperty("price", TradeUni::getPrice)
                .withProperty("slip", TradeUni::getSlip);


        Grid.Column<TradeUni> iconAmountColumn = tradesGrid.addColumn(sizee);


        iconAmountColumn.setWidth("150px").setResizable(true).setFlexGrow(1).setWidth("30px");
        Grid.Column<TradeUni> instrumentColumn = tradesGrid.addColumn(TradeUni::getInstrument).setResizable(true).setWidth("150px").setFlexGrow(1);


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

        TradeUni t = new TradeUni(exchangeName, instrument, size, side.equals("true"), price, timestamp, "id");
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
                    tradesGrid.getDataProvider().refreshItem(t);

                } else {
                    System.out.println("removing index 0 trade");
                    trades.remove(0);
                    System.out.println("adding updated trade");
                    trades.add(0, t);

                    tradesGrid.getElement().getNode().markAsDirty();
                    tradesGrid.getDataProvider().refreshAll();
                }

                if (trades.size() >= 50) {
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




    public void setMinAmount(int minAmount) {
        MainView.minAmount = minAmount;
    }

    public static int getMinAmount() {
        return minAmount;
    }


}
