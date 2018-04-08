package lol.corn.trade;

import lol.corn.MainView;
import lol.corn.utils.Broadcaster;

public class Buncher {

    //todo: cleanup wow lol

    //trade stream variables
    private static TradeUni bunch = null;
    private static boolean addedThisOne = false;

    public void addToBuncher(TradeUni trade) {

        //if no bunch start new one
        if (bunch == null) {
            newBunch(trade);
        }

        //if there is a bunch, and this trade has same timestamp/type
        else if (trade.getTimestamp().equals(bunch.getTimestamp()) && trade.getSide().equals(bunch.getSide()) && exchangeSpecificCondition(trade, bunch)) {

            //add this trade to current bunch
            bunch.setSize(bunch.getSize() + trade.getSize());

            //if over min trade
            if (bunch.getSize() >= MainView.getMinAmount()) {

                add(bunch, trade, addedThisOne);

                if (!addedThisOne) { addedThisOne = true; }
            }
        }

        //if there is a bunch but old timestamp or new type, new bunch with this current incoming trade
        else {
            newBunch(trade);
        }
    }

    private boolean exchangeSpecificCondition(TradeUni trade, TradeUni bunch) {

        if (trade.getExchangeName().equals("okex")) {

            //okex id sequence check
            return Double.parseDouble(trade.getId()) < Double.parseDouble(bunch.getId()) + 10;
        }//put other if (exchange)'s here

        return true;
    }

    private void newBunch(TradeUni trade) {

        addedThisOne = false;

        bunch = trade;

        bunch.setFirstPrice(trade.getPrice());

        //if bunch over min, send it and clear
        if (bunch.getSize() >= MainView.getMinAmount()) {
            add(bunch, trade, false);

        }
    }

    private void add(TradeUni bunch, TradeUni lastTrade, boolean updateExisting)  {

        bunch.setId(lastTrade.getId());
//        bunch.setFirstPrice(lastTrade.getFirstPrice());
        bunch.setLastPrice(lastTrade.getPrice());

        Broadcaster.broadcast((updateExisting ? "u" : "") + "%" + bunch.getExchangeName() + "%<" + bunch.getInstrument() + ">!" + bunch.getSide() + "!#" + bunch.getSize() + "#@" + bunch.getPrice() + "@*" + bunch.getTimestamp() + "*~" + Buncher.bunch.getFirstPrice() + "~=" + bunch.getLastPrice() + "=");
    }
}
