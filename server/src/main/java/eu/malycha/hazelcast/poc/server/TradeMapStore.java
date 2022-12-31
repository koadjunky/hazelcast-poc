package eu.malycha.hazelcast.poc.server;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.MapLoaderLifecycleSupport;
import com.hazelcast.map.MapStore;
import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxQLQueryApi;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.InfluxQLQuery;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.InfluxQLQueryResult;
import eu.malycha.hazelcast.poc.domain.Trade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class TradeMapStore implements MapStore<String, Trade>, MapLoaderLifecycleSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeMapStore.class);

    private static final String serverUrl = "http://influxdb:8086";
    private static final String serverToken = "admin-secret-token";
    private static final String org = "dev";
    private static final String bucket = "db0";

    private InfluxDBClient influxDB;

    @Override
    public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
        influxDB = InfluxDBClientFactory.create(serverUrl, serverToken.toCharArray(), org, bucket);
    }

    @Override
    public void destroy() {
        influxDB.close();
    }

    @Override
    public void store(String key, Trade value) {
        WriteApiBlocking writeApi = influxDB.getWriteApiBlocking();
        writeApi.writeMeasurement(WritePrecision.MS, TradeDto.fromTrade(value));
    }

    @Override
    public void storeAll(Map<String, Trade> map) {
        map.forEach(this::store);
    }

    @Override
    public void delete(String key) {
        DeleteApi deleteApi = influxDB.getDeleteApi();

        OffsetDateTime start = OffsetDateTime.now().minus(1, ChronoUnit.YEARS);
        OffsetDateTime stop = OffsetDateTime.now();

        deleteApi.delete(start, stop, "tradeId=\"%s\"".formatted(key), bucket, org);
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        keys.forEach(this::delete);
    }

    @Override
    public Trade load(String key) {
        String flux = "from(bucket: \"db0\") |> range(start:0) |> filter(fn: (r) => r.tradeId == \"%s\")".formatted(key);

        QueryApi queryApi = influxDB.getQueryApi();

        List<TradeDto> trades = queryApi.query(flux, TradeDto.class);
        return trades.stream()
            .findFirst()
            .map(TradeDto::toTrade)
            .orElse(null);
    }

    @Override
    public Map<String, Trade> loadAll(Collection<String> keys) {
        return keys.stream()
            .map(this::load)
            .collect(Collectors.toMap(Trade::getTradeId, v -> v));
    }

    @Override
    public Iterable<String> loadAllKeys() {
        String queryString = "show tag values from \"exposure\" with key=\"key\"";

        InfluxQLQuery query = new InfluxQLQuery(queryString, bucket).setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS);

        InfluxQLQueryApi queryApi = influxDB.getInfluxQLQueryApi();

        InfluxQLQueryResult result = queryApi.query(query);

        List<String> iterable = new LinkedList<>();
        if (result == null) {
            return iterable;
        }
        for (InfluxQLQueryResult.Result resultResult : result.getResults()) {
            for (InfluxQLQueryResult.Series series : resultResult.getSeries()) {
                for (InfluxQLQueryResult.Series.Record record : series.getValues()) {
                    LOGGER.info("Values: {}", record.getValueByKey("value"));
                    String value = Optional.ofNullable(record.getValueByKey("value"))
                        .map(Object::toString)
                        .orElse(StringUtils.EMPTY);
                    iterable.add(value);
                }
            }
        }
        return iterable;
    }
}
