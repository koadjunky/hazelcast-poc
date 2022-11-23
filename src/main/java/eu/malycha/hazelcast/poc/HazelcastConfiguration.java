package eu.malycha.hazelcast.poc;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.protobuf.ProtobufSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    private ClientConfig clientConfig() {
        ClientConfig clientConfig = new ClientConfig();

        ClientConnectionStrategyConfig connectionStrategyConfig = clientConfig.getConnectionStrategyConfig();
        connectionStrategyConfig.getConnectionRetryConfig().setMaxBackoffMillis(500);

        ClientNetworkConfig clientNetworkConfig = clientConfig.getNetworkConfig();
        clientNetworkConfig.addAddress("localhost");
        clientNetworkConfig.setSmartRouting(true);
        clientNetworkConfig.setRedoOperation(true);
        clientNetworkConfig.setConnectionTimeout(500);

        SerializerConfig serializerConfig = new SerializerConfig();
        serializerConfig.setTypeClass(Trade.class);
        serializerConfig.setImplementation(new TradeSerializer());
        clientConfig.getSerializationConfig().addSerializerConfig(serializerConfig);

        return clientConfig;
    }

    @Bean
    public HazelcastInstance hazelcast() {
        return HazelcastClient.newHazelcastClient(clientConfig());
    }
}

class TradeSerializer extends ProtobufSerializer<Trade> {

    private static final int TYPE_ID = 1;

    TradeSerializer() {
        super(Trade.class, TYPE_ID);
    }
}