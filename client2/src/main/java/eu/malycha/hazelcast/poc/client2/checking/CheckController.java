package eu.malycha.hazelcast.poc.client2.checking;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import eu.malycha.hazelcast.poc.domain.Trade;
import eu.malycha.hazelcast.poc.domain.TradePojo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check")
public class CheckController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckController.class);

    private final HazelcastInstance hz;

    public CheckController(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    @GetMapping("/pojo/{number}")
    public void pojoUpdate(int number) {
        IMap<String, TradePojo> data  = hz.getMap("trade_pojo");
        long start = System.currentTimeMillis();
        int matching = 0;
        for (int i = 0; i < number; i++) {
            String key = "%010d".formatted(i);
            TradePojo trade = data.get(key);
            matching += StringUtils.equals(trade.getTradeId(), trade.getNewField()) ? 1 : 0;
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Checked {} records in {} ms", number, stop - start);
        LOGGER.info("Matching records: {}", matching);
    }

    @GetMapping("/protobuf/{number}")
    public void protobufUpdate(int number) {
        IMap<String, Trade> data  = hz.getMap("trade");
        long start = System.currentTimeMillis();
        int matching = 0;
        for (int i = 0; i < number; i++) {
            String key = "%010d".formatted(i);
            Trade trade = data.get(key);
            matching += StringUtils.equals(trade.getTradeId(), trade.getNewField()) ? 1 : 0;
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Updated {} records in {} ms", number, stop - start);
        LOGGER.info("Matching records: {}", matching);
    }
}
