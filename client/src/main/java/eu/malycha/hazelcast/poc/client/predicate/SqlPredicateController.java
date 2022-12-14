package eu.malycha.hazelcast.poc.client.predicate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.impl.predicates.SqlPredicate;
import eu.malycha.hazelcast.poc.domain.Trade;
import eu.malycha.hazelcast.poc.domain.TradePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sqlpredicate")
public class SqlPredicateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlPredicateController.class);

    private final HazelcastInstance hz;

    public SqlPredicateController(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    @GetMapping("/pojo/sender/{name}")
    public void pojoSenderCount(String name) {
        long start = System.currentTimeMillis();
        Predicate<String, TradePojo> predicate = Predicates.sql("sender = '%s'".formatted(name));
        IMap<String, TradePojo> map = hz.getMap("trade_pojo");
        Collection<TradePojo> trades = map.values(predicate);
        Integer sum = trades.stream()
            .map(TradePojo::getQuantity)
            .map(Integer::parseInt)
            .reduce(0, Integer::sum);
        LOGGER.info("Returned {} rows", trades.size());
        LOGGER.info("Total: {}", sum);
        long stop = System.currentTimeMillis();
        LOGGER.info("Calculation executed in {} ms", stop - start);
    }

    @GetMapping("/protobuf/sender/{name}")
    public void protobufSenderCount(String name) {
        long start = System.currentTimeMillis();
        Predicate<String, Trade> predicate = Predicates.sql("sender = '%s'".formatted(name));
        IMap<String, Trade> map = hz.getMap("trade");
        Collection<Trade> trades = map.values(predicate);
        Integer sum = trades.stream()
            .map(Trade::getQuantity)
            .map(Integer::parseInt)
            .reduce(0, Integer::sum);
        LOGGER.info("Returned {} rows", trades.size());
        LOGGER.info("Total: {}", sum);
        long stop = System.currentTimeMillis();
        LOGGER.info("Calculation executed in {} ms", stop - start);
    }
}
