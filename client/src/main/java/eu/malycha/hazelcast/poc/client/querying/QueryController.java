package eu.malycha.hazelcast.poc.client.querying;

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
@RequestMapping("/api/query")
public class QueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryController.class);

    private final HazelcastInstance hz;

    public QueryController(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    @GetMapping("/pojo/{number}")
    public void pojoQuery(int number) {
        IMap<String, TradePojo> data = hz.getMap("trade_pojo");
        long start = System.currentTimeMillis();
        for (int i = 0; i < number; i++) {
            data.get("%010d".formatted(i));
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Queried {} records in {} ms", number, stop - start);
    }

    @GetMapping("/protobuf/{number}")
    public void protobufQuery(int number) {
        IMap<String, Trade> data = hz.getMap("trade");
        long start = System.currentTimeMillis();
        for (int i = 0; i < number; i++) {
            data.get("%010d".formatted(i));
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Queried {} records in {} ms", number, stop - start);
    }
}
