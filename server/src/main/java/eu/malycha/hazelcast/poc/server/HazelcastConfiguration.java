package eu.malycha.hazelcast.poc.server;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExternalDataStoreConfig;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.SplitBrainProtectionConfig;
import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.splitbrainprotection.SplitBrainProtectionOn;
import eu.malycha.hazelcast.poc.domain.Trade;
import eu.malycha.hazelcast.poc.domain.TradePojo;
import eu.malycha.hazelcast.poc.domain.TradeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    private static final String SPB_NAME = "recently-active-spb";
    private static final int SPB_MIN_NODES = 2;
    private static final int SPB_TOLERANCE = 60000;
    private Config serverConfig() {
        Config config = new Config();
        config.getJetConfig().setEnabled(true);

        UserCodeDeploymentConfig deploymentConfig = config.getUserCodeDeploymentConfig();
        deploymentConfig.setEnabled(true);

        ManagementCenterConfig managementCenterConfig = config.getManagementCenterConfig();
        managementCenterConfig.setConsoleEnabled(true);

        SplitBrainProtectionConfig splitBrainProtectionConfig = SplitBrainProtectionConfig.newRecentlyActiveSplitBrainProtectionConfigBuilder(SPB_NAME, SPB_MIN_NODES, SPB_TOLERANCE)
            .build();
        splitBrainProtectionConfig.setProtectOn(SplitBrainProtectionOn.READ_WRITE);
        splitBrainProtectionConfig.setEnabled(true);
        config.addSplitBrainProtectionConfig(splitBrainProtectionConfig);

        MapStoreConfig tradeMapStoreConfig = new MapStoreConfig();
        tradeMapStoreConfig.setWriteDelaySeconds(1); // Configures Write-Behind
        tradeMapStoreConfig.setClassName("eu.malycha.hazelcast.poc.server.TradeMapStore");

        MapConfig tradeMapConfig = config.getMapConfig("trade");
        tradeMapConfig.addIndexConfig(new IndexConfig(IndexType.HASH, "sender"));
        tradeMapConfig.addIndexConfig(new IndexConfig(IndexType.HASH, "counterpart"));
        tradeMapConfig.setSplitBrainProtectionName(SPB_NAME);
        // tradeMapConfig.setMapStoreConfig(tradeMapStoreConfig);

        MapStoreConfig tradePojoMapStoreConfig = new MapStoreConfig();
        tradePojoMapStoreConfig.setWriteDelaySeconds(1); // Configures Write-Behind
        tradePojoMapStoreConfig.setClassName("eu.malycha.hazelcast.poc.server.TradePojoMapStore");

        MapConfig tradePojoMapConfig = config.getMapConfig("trade_pojo");
        tradePojoMapConfig.addIndexConfig(new IndexConfig(IndexType.HASH, "sender"));
        tradePojoMapConfig.addIndexConfig(new IndexConfig(IndexType.HASH, "counterpart"));
        tradePojoMapConfig.setSplitBrainProtectionName(SPB_NAME);
        // tradePojoMapConfig.setMapStoreConfig(tradePojoMapStoreConfig);

        SerializerConfig serializerConfig = new SerializerConfig();
        serializerConfig.setTypeClass(Trade.class);
        serializerConfig.setImplementation(new TradeSerializer());
        config.getSerializationConfig().addSerializerConfig(serializerConfig);

        config.getSerializationConfig().getCompactSerializationConfig()
            .addClass(TradePojo.class);

        config.getDurableExecutorConfig("default")
            .setPoolSize(4)
            .setDurability(1)
            .setSplitBrainProtectionName(SPB_NAME)
            .setCapacity(8);

        return config;
    }

    @Bean
    public HazelcastInstance hazelcast() {
        return Hazelcast.newHazelcastInstance(serverConfig());
    }
}
