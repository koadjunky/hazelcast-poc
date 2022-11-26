package eu.malycha.hazelcast.poc.domain;

import java.io.Serializable;

public class TradePojo implements Serializable {

    public String tradeId;
    public String orderId;

    public String sender;
    public String counterpart;
    public String ticker;

    public String quantity;
    public String baseCurrency;
    public String quoteCurrency;
    public String price;

    public String getTradeId() {
        return tradeId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSender() {
        return sender;
    }

    public String getCounterpart() {
        return counterpart;
    }

    public String getTicker() {
        return ticker;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public String getPrice() {
        return price;
    }

    private TradePojo(Builder builder) {
        tradeId = builder.tradeId;
        orderId = builder.orderId;
        sender = builder.sender;
        counterpart = builder.counterpart;
        ticker = builder.ticker;
        quantity = builder.quantity;
        baseCurrency = builder.baseCurrency;
        quoteCurrency = builder.quoteCurrency;
        price = builder.price;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(TradePojo copy) {
        Builder builder = new Builder();
        builder.tradeId = copy.getTradeId();
        builder.orderId = copy.getOrderId();
        builder.sender = copy.getSender();
        builder.counterpart = copy.getCounterpart();
        builder.ticker = copy.getTicker();
        builder.quantity = copy.getQuantity();
        builder.baseCurrency = copy.getBaseCurrency();
        builder.quoteCurrency = copy.getQuoteCurrency();
        builder.price = copy.getPrice();
        return builder;
    }


    public static final class Builder {
        private String tradeId;
        private String orderId;
        private String sender;
        private String counterpart;
        private String ticker;
        private String quantity;
        private String baseCurrency;
        private String quoteCurrency;
        private String price;

        private Builder() {
        }

        public Builder setTradeId(String val) {
            tradeId = val;
            return this;
        }

        public Builder setOrderId(String val) {
            orderId = val;
            return this;
        }

        public Builder setSender(String val) {
            sender = val;
            return this;
        }

        public Builder setCounterpart(String val) {
            counterpart = val;
            return this;
        }

        public Builder setTicker(String val) {
            ticker = val;
            return this;
        }

        public Builder setQuantity(String val) {
            quantity = val;
            return this;
        }

        public Builder setBaseCurrency(String val) {
            baseCurrency = val;
            return this;
        }

        public Builder setQuoteCurrency(String val) {
            quoteCurrency = val;
            return this;
        }

        public Builder setPrice(String val) {
            price = val;
            return this;
        }

        public TradePojo build() {
            return new TradePojo(this);
        }
    }
}
