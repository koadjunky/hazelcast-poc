package eu.malycha.hazelcast.poc.server;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import eu.malycha.hazelcast.poc.domain.Trade;
import eu.malycha.hazelcast.poc.domain.TradePojo;

import java.math.BigDecimal;
import java.time.Instant;

@Measurement(name = "trade")
public class TradeDto {

    @Column(timestamp = true)
    private final Instant time;

    @Column(tag = true)
    private final String tradeId;
    @Column(tag = true)
    private final String orderId;

    @Column(tag = true)
    private final String sender;
    @Column(tag = true)
    private final String counterpart;
    @Column(tag = true)
    private final String ticker;

    @Column
    private final BigDecimal quantity;
    @Column(tag = true)
    private final String baseCurrency;
    @Column(tag = true)
    private final String quoteCurrency;
    @Column
    private final BigDecimal price;

    public TradeDto(String tradeId, String orderId, String sender, String counterpart, String ticker, String quantity, String baseCurrency, String quoteCurrency, String price) {
        this.time = Instant.now();
        this.tradeId = tradeId;
        this.orderId = orderId;
        this.sender = sender;
        this.counterpart = counterpart;
        this.ticker = ticker;
        this.quantity = new BigDecimal(quantity);
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.price = new BigDecimal(price);
    }

    public static TradeDto fromTrade(Trade trade) {
        return new TradeDto(
            trade.getTradeId(),
            trade.getOrderId(),
            trade.getSender(),
            trade.getCounterpart(),
            trade.getTicker(),
            trade.getQuantity(),
            trade.getBaseCurrency(),
            trade.getQuoteCurrency(),
            trade.getPrice()
        );
    }

    public static TradeDto fromTradePojo(TradePojo trade) {
        return new TradeDto(
            trade.getTradeId(),
            trade.getOrderId(),
            trade.getSender(),
            trade.getCounterpart(),
            trade.getTicker(),
            trade.getQuantity(),
            trade.getBaseCurrency(),
            trade.getQuoteCurrency(),
            trade.getPrice()
        );
    }

    public Trade toTrade() {
        return Trade.newBuilder()
            .setTradeId(tradeId)
            .setOrderId(orderId)
            .setSender(sender)
            .setCounterpart(counterpart)
            .setTicker(ticker)
            .setQuantity(quantity.toPlainString())
            .setBaseCurrency(baseCurrency)
            .setQuoteCurrency(quoteCurrency)
            .setPrice(price.toPlainString())
            .build();
    }

    public TradePojo toTradePojo() {
        return TradePojo.newBuilder()
            .setTradeId(tradeId)
            .setOrderId(orderId)
            .setSender(sender)
            .setCounterpart(counterpart)
            .setTicker(ticker)
            .setQuantity(quantity.toPlainString())
            .setBaseCurrency(baseCurrency)
            .setQuoteCurrency(quoteCurrency)
            .setPrice(price.toPlainString())
            .build();
    }
}
