package eu.malycha.hazelcast.poc;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.client.config.ConnectionRetryConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.protobuf.ProtobufSerializer;
import com.hazelcast.sql.SqlResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

        ClientUserCodeDeploymentConfig deploymentConfig = clientConfig.getUserCodeDeploymentConfig();
        deploymentConfig.addClass(TradeOrBuilder.class);
        deploymentConfig.addClass(Trade.class);
        deploymentConfig.addClass(Trade.Builder.class);
        deploymentConfig.addClass(TradeSerializer.class);
        deploymentConfig.setEnabled(true);

        return clientConfig;
    }

    private void dropMapping(HazelcastInstance hz) {
        String query = "DROP MAPPING IF EXISTS trade";
        SqlResult result = hz.getSql().execute(query);
        result.close();
    }

    private void configureMapping(HazelcastInstance hz) {
        // TODO: Replace with code block after upgrade to java-17
        String query =
            "CREATE MAPPING trade " +
            "TYPE IMap " +
            "OPTIONS ( " +
            "    'keyFormat' = 'java', " +
            "    'keyJavaClass' = 'java.lang.String', " +
            "    'valueFormat' = 'java', " +
            "    'valueJavaClass' = 'eu.malycha.hazelcast.poc.Trade' " +
            ")";
        dropMapping(hz);
        SqlResult result = hz.getSql().execute(query);
        result.close();
    }

    @Bean
    public HazelcastInstance hazelcast() {
        HazelcastInstance hz = HazelcastClient.newHazelcastClient(clientConfig());
        configureMapping(hz);
        return hz;
    }
}

class TradeSerializer extends ProtobufSerializer<Trade> {

    private static final int TYPE_ID = 1;

    TradeSerializer() {
        super(Trade.class, TYPE_ID);
    }
}