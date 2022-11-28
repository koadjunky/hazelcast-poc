package eu.malycha.hazelcast.poc.client.predicate;

import java.util.Collection;

import com.hazelcast.core.HazelcastInstance;
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
        SqlPredicate predicate = new SqlPredicate("sender = '%s'".formatted(name));
        Collection<TradePojo> trades = hz.getMap("trade_pojo").values(predicate);
        LOGGER.info("Returned {} rows", trades.size());
    }

    @GetMapping("/protobuf/sender/{name}")
    public void protobufSenderCount(String name) {
        SqlPredicate predicate = new SqlPredicate("sender = '%s'".formatted(name));
        Collection<Trade> trades = hz.getMap("trade").values(predicate);
        LOGGER.info("Returned {} rows", trades.size());
    }
}
