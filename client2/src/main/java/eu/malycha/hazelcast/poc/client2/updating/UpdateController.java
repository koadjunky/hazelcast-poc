package eu.malycha.hazelcast.poc.client2.updating;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import eu.malycha.hazelcast.poc.domain.Trade;
import eu.malycha.hazelcast.poc.domain.TradePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/update")
public class UpdateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateController.class);

    private final HazelcastInstance hz;

    public UpdateController(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    @GetMapping("/pojo/{number}")
    public void pojoUpdate(int number) {
        IMap<String, TradePojo> data  = hz.getMap("trade_pojo");
        long start = System.currentTimeMillis();
        for (int i = 0; i < number; i++) {
            String key = "%010d".formatted(i);
            TradePojo trade = data.get(key);
            trade.setNewField(trade.getTradeId());
            data.set(key, trade);
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Updated {} records in {} ms", number, stop - start);
    }

    @GetMapping("/protobuf/{number}")
    public void protobufUpdate(int number) {
        IMap<String, Trade> data  = hz.getMap("trade");
        long start = System.currentTimeMillis();
        for (int i = 0; i < number; i++) {
            String key = "%010d".formatted(i);
            Trade trade = data.get(key);
            Trade updated = trade.toBuilder()
                .setNewField(trade.getTradeId())
                .build();
            data.set(key, updated);
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Updated {} records in {} ms", number, stop - start);
    }
}
