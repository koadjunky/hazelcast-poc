package eu.malycha.hazelcast.poc.loading;

import java.util.UUID;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import eu.malycha.hazelcast.poc.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/load")
public class LoaderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoaderController.class);

    private final HazelcastInstance hz;

    public LoaderController(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    @PostMapping("/{number}")
    public void load(int number) {
        long start = System.currentTimeMillis();
        IMap<String, Trade> data = hz.getMap("hazelcast-poc-map");
        for (int i = 0; i < number; i++) {
            Trade record = Trade.newBuilder()
                .setTradeId(UUID.randomUUID().toString())
                .build();
            data.put(record.getTradeId(), record);
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Loaded {} records in {} ms", number, stop - start);
    }
}
