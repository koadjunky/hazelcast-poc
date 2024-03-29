package eu.malycha.hazelcast.poc.client.loading;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import eu.malycha.hazelcast.poc.domain.Trade;
import eu.malycha.hazelcast.poc.domain.TradePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoaderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoaderService.class);

    private static final int QTY_LIMIT = 5000;

    private static final List<String> ACCOUNTS = List.of("Alice", "Bob", "Carol", "David");

    private static final Random rand = new Random();
    private final HazelcastInstance hz;

    LoaderService(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    void load_pojo(int number) {
        long start = System.currentTimeMillis();
        IMap<String, TradePojo> data = hz.getMap("trade_pojo");
        for (int i = 0; i < number; i++) {
            TradePojo record = TradePojo.newBuilder()
                .setTradeId("%010d".formatted(i))
                .setSender(getRandomAccount())
                .setCounterpart(getRandomAccount())
                .setQuantity(getRandomQuantity())
                .setPrice("1.0")
                .build();
            data.put(record.getTradeId(), record);
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Loaded {} records in {} ms", number, stop - start);
    }

    void load_protobuf(int number) {
        long start = System.currentTimeMillis();
        IMap<String, Trade> data = hz.getMap("trade");
        for (int i = 0; i < number; i++) {
            Trade record = Trade.newBuilder()
                .setTradeId("%010d".formatted(i))
                .setSender(getRandomAccount())
                .setCounterpart(getRandomAccount())
                .setQuantity(getRandomQuantity())
                .setPrice("1.0")
                .build();
            data.put(record.getTradeId(), record);
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Loaded {} records in {} ms", number, stop - start);
    }

    void load_protobuf_ttl(int number, int ttl, int delay) {
        IMap<String, Trade> data = hz.getMap("trade_ttl");
        for (int i = 0; i < number; i++) {
            Trade record = Trade.newBuilder()
                .setTradeId("%010d".formatted(i))
                .setSender(getRandomAccount())
                .setCounterpart(getRandomAccount())
                .setQuantity(getRandomQuantity())
                .setPrice("1.0")
                .build();
            data.putAsync(record.getTradeId(), record, ttl, TimeUnit.MILLISECONDS);
            LOGGER.info("Writing record {} with ttl={}ms", i, ttl);
            delay(delay);
        }
    }

    public static String getRandomAccount() {
        return ACCOUNTS.get(rand.nextInt(ACCOUNTS.size()));
    }

    public static String getRandomQuantity() {
        return Integer.toString(rand.nextInt(QTY_LIMIT));
    }

    public static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
