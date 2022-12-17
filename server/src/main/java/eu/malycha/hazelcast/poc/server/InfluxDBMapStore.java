package eu.malycha.hazelcast.poc.server;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.MapLoaderLifecycleSupport;
import com.hazelcast.map.MapStore;
import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.DeletePredicateRequest;
import com.influxdb.client.domain.WritePrecision;
import eu.malycha.hazelcast.poc.domain.TradePojo;
import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class InfluxDBMapStore implements MapStore<String, TradePojo>, MapLoaderLifecycleSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBMapStore.class);

    private static final String serverUrl = "http://influxdb:8086";
    private static final String serverToken = "admin-secret-token";
    private static final String bucket = "db0";

    // TODO: Move to bean
    private InfluxDBClient influxDB;

    @Override
    public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
        influxDB = InfluxDBClientFactory.create(serverUrl, serverToken.toCharArray());
    }

    @Override
    public void destroy() {
        influxDB.close();
    }

    @Override
    public void store(String key, TradePojo value) {
        WriteApiBlocking writeApi = influxDB.getWriteApiBlocking();
        writeApi.writeMeasurement(WritePrecision.MS, TradeDto.fromTradePojo(value));
    }

    @Override
    public void storeAll(Map<String, TradePojo> map) {
        map.forEach(this::store);
    }

    @Override
    public void delete(String key) {
        // TODO: trade_pojo to const
        DeleteApi deleteApi = influxDB.getDeleteApi();

        influxDB.query(new Query("DELETE FROM trade_pojo WHERE tradeId='%s'".formatted(key)));
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        keys.forEach(this::delete);
    }

    // TODO: Prepared statement
    @Override
    public TradePojo load(String key) {
        Query query = new Query("SELECT * FROM trade_pojo WHERE tradeId='%s'".formatted(key));
        List<TradeDto> trades = influxDBMapper.query(query, TradeDto.class);
        return trades.stream()
            .findFirst()
            .map(TradeDto::toTradePojo)
            .orElse(null);
    }

    @Override
    public Map<String, TradePojo> loadAll(Collection<String> keys) {
        return keys.stream()
            .map(this::load)
            .collect(Collectors.toMap(TradePojo::getTradeId, v -> v));
    }

    @Override
    public Iterable<String> loadAllKeys() {
        Query query = new Query("SHOW TAG VALUES ON trade_pojo WITH KEY = 'tradeId'");
        QueryResult queryResult = influxDB.query(query);
        LOGGER.info("loadAllKeys: {}", queryResult.toString());
        return IteratorUtils.asIterable(IteratorUtils.emptyIterator());
    }
}
