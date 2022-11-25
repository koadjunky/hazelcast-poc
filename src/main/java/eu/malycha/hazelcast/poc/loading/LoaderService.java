package eu.malycha.hazelcast.poc.loading;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import eu.malycha.hazelcast.poc.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoaderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoaderService.class);

    private static final List<String> ACCOUNTS = List.of("Alice", "Bob", "Carol", "David");

    private final Random rand = new Random();
    private final HazelcastInstance hz;

    LoaderService(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    void load(int number) {
        long start = System.currentTimeMillis();
        IMap<String, Trade> data = hz.getMap("trade");
        for (int i = 0; i < number; i++) {
            Trade record = Trade.newBuilder()
                .setTradeId(UUID.randomUUID().toString())
                .setSender(getRandomAccount())
                .setCounterpart(getRandomAccount())
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
