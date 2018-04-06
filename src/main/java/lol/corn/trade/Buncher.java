package lol.corn.trade;

import lol.corn.utils.Broadcaster;

public class Buncher {


    //trade stream variables
    private static TradeUni bunch = null;
    private static boolean addedThisOne = false;

    private static int minimumTrade = 9000;

    private static long systime;

//todo: get first and last price to show price movement range with an arrow up or down
    public void addToBuncher(TradeUni trade) {

        //if no bunch start new one
        if (bunch == null) {
            System.out.println("bunch null, new bunch with this trade");
            newBunch(trade);

        }
        //if there is a bunch, and this trade has same timestamp/type
        //todo: maybe check if ids are within 5 range
        else if (trade.getTimestamp().equals(bunch.getTimestamp()) && trade.getSide().equals(bunch.getSide()) && exchangeSpecificCondition(trade, bunch)) {



            //add this trade to current bunch
            bunch.setSize(bunch.getSize() + trade.getSize());

            System.out.println("+ we have a bunch, and this trade has same time/side to add to bunch. current size: " + bunch.getSize());


            //if over min trade
            if (bunch.getSize() >= minimumTrade) {

                System.out.println("bunch over min trade");

                //check if existing panel for this timestamp, decides to add or update
                if (!addedThisOne) {
                    System.out.println("havent added this one, add it");
                    add(bunch, trade);
                    addedThisOne = true;
                } else {
                    System.out.println("already a pan up, just update it");
                    update(bunch, trade);
                }
            }
        }

        //if there is a bunch but old timestamp or new type, new bunch with this trade
        else {

            System.out.println("there is a bunch but we have a new time or type, new bunch with this trade.  size:" + trade.getSize());
            newBunch(trade);
        }

    }

    private boolean exchangeSpecificCondition(TradeUni trade, TradeUni bunch) {
//
        if (trade.getExchangeName().equals("okex")) {


            if (Double.parseDouble(trade.getId()) < Double.parseDouble(bunch.getId()) + 10) {
//                System.out.println("passed id check!");
                return true;
            } else {
//                System.out.println("failed id check :<");
                return false;
            }
        }

        //put other if (exchange)'s here



        return true;

    }


    private void newBunch(TradeUni trade) {

        addedThisOne = false;

        //maybe just be able to set tradeuni from the trade without each .get?
        bunch = new TradeUni(trade.getExchangeName(), trade.getInstrument(), trade.getSize(), trade.getSide(), trade.getPrice(), trade.getTimestamp(), trade.getId());

        //if bunch over min, send it and clear
        if (bunch.getSize() >= minimumTrade) {
//            System.out.println("sending new bunch");
            add(bunch, trade);
//            bunch = null;
        }

    }

    private void update(TradeUni bunch, TradeUni lastTrade) {

        bunch.setId(lastTrade.getId());

        System.out.print("+++ new bunch total: " + (int) bunch.getSize());

        Broadcaster.broadcast("(bitfinex XBTUSDT)!" + bunch.getSide() + "!$" + bunch.getSize() + "$@" + bunch.getPrice() + "@");


    }

    private void add(TradeUni bunch, TradeUni lastTrade) {

        bunch.setId(lastTrade.getId());

        System.out.print("\n" + bunch.getExchangeName() + " bunch added. id: " + bunch.getId() + " time: " + bunch.getTimestamp() + " side: " + bunch.getSide() + " amt: " + (int) bunch.getSize() + "\n");

        Broadcaster.broadcast("(bitfinex XBTUSDT)!" + bunch.getSide() + "!$" + bunch.getSize() + "$@" + bunch.getPrice() + "@");




    }


}
