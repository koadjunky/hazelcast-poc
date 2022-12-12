package eu.malycha.hazelcast.poc.client.loading;

import java.util.List;
import java.util.Random;
import java.util.UUID;

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

    private final Random rand = new Random();
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
                .setQuantity(Integer.toString(rand.nextInt(QTY_LIMIT)))
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
                .setQuantity(Integer.toString(rand.nextInt(QTY_LIMIT)))
                .build();
            data.put(record.getTradeId(), record);
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Loaded {} records in {} ms", number, stop - start);
    }

    private String getRandomAccount() {
        return ACCOUNTS.get(rand.nextInt(ACCOUNTS.size()));
    }
}
