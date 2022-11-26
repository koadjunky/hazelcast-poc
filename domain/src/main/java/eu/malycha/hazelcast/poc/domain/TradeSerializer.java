package eu.malycha.hazelcast.poc.domain;

import com.hazelcast.jet.protobuf.ProtobufSerializer;

public class TradeSerializer extends ProtobufSerializer<Trade> {

    private static final int TYPE_ID = 1;

    public TradeSerializer() {
        super(Trade.class, TYPE_ID);
    }
}
