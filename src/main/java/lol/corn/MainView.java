package lol.corn;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lol.corn.trade.TradeUni;
import lol.corn.utils.Broadcaster;
import org.atmosphere.cache.BroadcastMessage;

import java.util.List;


@Push
@BodySize(height = "100vh", width = "100vw")
@HtmlImport("styles/shared-styles.html")
@Route("")
@Theme(Lumo.class)
public class MainView extends SplitLayout implements Broadcaster.BroadcastListener {

    private static SplitLayout mainSplit = new SplitLayout();
    private static Grid<TradeUni> tradesGrid = new Grid<>();


    public MainView() {
        setupLayout();



        tradesGrid.addColumn(TradeUni::getPair).setHeader("pair").setResizable(true);
        tradesGrid.addColumn(TradeUni::getSize).setHeader("USD value").setResizable(true);
        tradesGrid.addColumn(TradeUni::getPrice).setHeader("BTC price").setResizable(true);




        Broadcaster.register(this);

        setClassName("main-layout");
    }


    private void setupLayout() {
        setSizeFull();
        setOrientation(Orientation.VERTICAL);
        addToPrimary(mainSplit);
        addToSecondary(new Button("bottom")); //put more for footer

        mainSplit.setSizeFull();
        mainSplit.addToPrimary(tradesGrid);
        mainSplit.addToSecondary(new ComboBox<String>());

        mainSplit.setSplitterPosition(15);

    }

    @Override
    public void receiveBroadcast(BroadcastMessage message) {

        UI.getCurrent().access(new Command() {
            @Override
            public void execute() {
                System.out.println(message);
            }
        });


    }
}
