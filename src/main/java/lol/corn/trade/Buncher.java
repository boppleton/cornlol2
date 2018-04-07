package lol.corn.trade;

import lol.corn.utils.Broadcaster;

public class Buncher {

    //trade stream variables
    private static TradeUni bunch = null;
    private static boolean addedThisOne = false;
    private static boolean inBunchTime = false;

    private static int minimumTrade = 1000;

    private static long systime;

    private static long addtime;

    private static long lasttime;

//todo: get first and last price to show price movement range with an arrow up or down
    public void addToBuncher(TradeUni trade) {

//        System.out.println("added to buncher: " + trade.getExchangeName() + trade.getSize() + trade.getSide() + trade.getTimestamp());

        //if no bunch start new one
        if (bunch == null) {
//            System.out.println("bunch null, new bunch with this trade");
            newBunch(trade);

        }
        //if there is a bunch, and this trade has same timestamp/type
        //todo: maybe check if ids are within 5 range
        else if (trade.getTimestamp().equals(bunch.getTimestamp()) && trade.getSide().equals(bunch.getSide()) && exchangeSpecificCondition(trade, bunch)) {



            //add this trade to current bunch
            bunch.setSize(bunch.getSize() + trade.getSize());

//            System.out.println("+ we have a bunch, and this trade has same time/side to add to bunch. current size: " + bunch.getSize());


            //if over min trade
            if (bunch.getSize() >= minimumTrade) {

//                System.out.println("bunch over min trade");

                //check if existing panel for this timestamp, decides to add or update
                if (!addedThisOne) {
//                    System.out.println("havent added this one, add it");
                    add(bunch, trade);
                    addedThisOne = true;
                } else {
//                    System.out.println("already a pan up, just update it");
                    update(bunch, trade);
                }
            }
        }

        //if there is a bunch but old timestamp or new type, new bunch with this trade
        else {

//            System.out.println("there is a bunch but we have a new time or type, new bunch with this trade.  size:" + trade.getSize());
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

        bunch.setFirstPrice(trade.getPrice());

        //if bunch over min, send it and clear
        if (bunch.getSize() >= minimumTrade) {
//            System.out.println("sending new bunch");
            add(bunch, trade);
//            bunch = null;
        }

    }

    private void update(TradeUni bunch, TradeUni lastTrade) {

        bunch.setId(lastTrade.getId());

        bunch.setLastPrice(lastTrade.getPrice());


//        System.out.print("+++ new bunch total: " + (int) bunch.getSize());

//        systime = System.currentTimeMillis();

//        System.out.println("systime: " + String.valueOf(systime));

//        System.out.println("lasttime: " + String.valueOf(lasttime));

//        lasttime = System.currentTimeMillis();

        Broadcaster.broadcast("u%" + bunch.getExchangeName() + "%<" + bunch.getInstrument() + ">!" + bunch.getSide() + "!$" + bunch.getSize() + "$@" + bunch.getPrice() + "@*" + bunch.getTimestamp() + "*^" + bunch.getFirstPrice() + "^=" + bunch.getLastPrice() + "=");


    }

    private void add(TradeUni bunch, TradeUni lastTrade)  {

        bunch.setId(lastTrade.getId());

        bunch.setLastPrice(lastTrade.getPrice());


        Broadcaster.broadcast("%" + bunch.getExchangeName() + "%<" + bunch.getInstrument() + ">!" + bunch.getSide() + "!$" + bunch.getSize() + "$@" + bunch.getPrice() + "@*" + bunch.getTimestamp() + "*^" + bunch.getFirstPrice() + "^=" + bunch.getLastPrice() + "=");




    }

    private void startAddCheck() {
    }

    private void checkTime() throws InterruptedException {

        Thread.sleep(20);



    }


}
