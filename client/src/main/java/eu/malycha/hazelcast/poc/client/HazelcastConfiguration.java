package eu.malycha.hazelcast.poc.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.client.config.ConnectionRetryConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import eu.malycha.hazelcast.poc.domain.TradePojo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import eu.malycha.hazelcast.poc.domain.Trade;
import eu.malycha.hazelcast.poc.domain.TradeSerializer;

@Configuration
public class HazelcastConfiguration {

    private ClientConfig clientConfig() {
        ClientConfig clientConfig = new ClientConfig();

        ConnectionRetryConfig connectionRetryConfig = clientConfig.getConnectionStrategyConfig().getConnectionRetryConfig();
        connectionRetryConfig.setMaxBackoffMillis(500);
        connectionRetryConfig.setClusterConnectTimeoutMillis(5000);

        ClientNetworkConfig clientNetworkConfig = clientConfig.getNetworkConfig();
        clientNetworkConfig.addAddress("localhost");
        clientNetworkConfig.setSmartRouting(true);
        clientNetworkConfig.setRedoOperation(true);
        clientNetworkConfig.setConnectionTimeout(500);

        SerializerConfig serializerConfig = new SerializerConfig();
        serializerConfig.setTypeClass(Trade.class);
        serializerConfig.setImplementation(new TradeSerializer());
        clientConfig.getSerializationConfig().addSerializerConfig(serializerConfig);

        clientConfig.addNearCacheConfig(nearCacheConfig("trade"));
        clientConfig.addNearCacheConfig(nearCacheConfig("trade_pojo"));

        return clientConfig;
    }

    private NearCacheConfig nearCacheConfig(String name) {
        EvictionConfig evictionConfig = new EvictionConfig()
            .setEvictionPolicy(EvictionPolicy.NONE)
            .setMaxSizePolicy(MaxSizePolicy.ENTRY_COUNT)
            .setSize(100000);

        return new NearCacheConfig()
            .setName(name)
            .setInMemoryFormat(InMemoryFormat.OBJECT)
            .setInvalidateOnChange(false)
            .setTimeToLiveSeconds(600)
            .setEvictionConfig(evictionConfig);
    }

    @Bean
    public HazelcastInstance hazelcast() {
        HazelcastInstance hz = HazelcastClient.newHazelcastClient(clientConfig());
        HazelcastUtils.configureMapping(hz, "trade_pojo", TradePojo.class);
        HazelcastUtils.configureMapping(hz, "trade", Trade.class);
        return hz;
    }
}
