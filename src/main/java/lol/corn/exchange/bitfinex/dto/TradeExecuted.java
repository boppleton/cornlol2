package lol.corn.exchange.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

//using tu for now because te is returning differing types for id

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class TradeExecuted {

    public Integer startNum;
    public String te;

//    public String seq;

//    public long tradeId;
    public long timestamp;
    public BigDecimal price;
    public BigDecimal amount;

}
