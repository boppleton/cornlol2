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
import lol.corn.exchange.bitmex.dto.Trade;
import lol.corn.trade.TradeUni;
import lol.corn.utils.Broadcaster;
import lol.corn.utils.WebsocketSetup;

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

    public MainView() {
        setupLayout();

        setupTradesGrid();

        registerBroadcastListener();

        startWebsocket();

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
        tradesGrid.addColumn(TradeUni::getSize).setHeader("Amount").setResizable(true).setWidth("27%");
        tradesGrid.addColumn(TradeUni::getPair).setHeader("Instrument").setResizable(true).setWidth("54%");
        tradesGrid.addColumn(TradeUni::getPrice).setHeader("Price").setResizable(true);
    }

    private void addTrade(String message) {

        double size = Double.parseDouble(message.substring(message.indexOf("$") + 1, message.lastIndexOf("$")));
        String exchangeName = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
        String side = message.substring(message.indexOf("!") + 1, message.lastIndexOf("!"));
        double price = Double.parseDouble(message.substring(message.indexOf("@") + 1, message.lastIndexOf("@")));

        TradeUni t = new TradeUni(exchangeName, side, size, size / price, price);

        sendTradeUni(t);
    }

    private void sendTradeUni(TradeUni t) {

        if (getUI().isPresent()) {

            getUI().get().access((Command) () -> {

                trades.add(0, t);
                tradesGrid.getElement().getNode().markAsDirty();
                tradesGrid.getDataProvider().refreshAll();
            });
        }
    }

    private void startWebsocket() {

        try {
            WebsocketSetup.startStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveBroadcast(String message) {
        addTrade(message);
    }

    private void registerBroadcastListener() {
        Broadcaster.register(this);
    }
}
