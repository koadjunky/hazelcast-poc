package eu.malycha.hazelcast.poc.client.sql;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sql")
public class SqlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlController.class);

    private final HazelcastInstance hz;

    public SqlController(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    @GetMapping("/pojo/sender/{name}")
    public int pojoSenderCount(String name) {
        long start = System.currentTimeMillis();
        try (SqlResult result = hz.getSql().execute("SELECT * FROM trade_pojo WHERE sender = '%s'".formatted(name))) {
            int count = 0;
            int sum = 0;
            for (SqlRow row : result) {
                count++;
                String quantity = row.getObject("quantity");
                sum += Integer.parseInt(quantity);
            }
            LOGGER.info("Returned {} rows", count);
            LOGGER.info("Total: {}", sum);
            long stop = System.currentTimeMillis();
            LOGGER.info("Calculation executed in {} ms", stop - start);
            return count;
        }
    }

    @GetMapping("/protobuf/sender/{name}")
    public int protobufSenderCount(String name) {
        long start = System.currentTimeMillis();
        try (SqlResult result = hz.getSql().execute("SELECT * FROM trade WHERE sender = '%s'".formatted(name))) {
            int count = 0;
            int sum = 0;
            for (SqlRow row : result) {
                count++;
                String quantity = row.getObject("quantity");
                sum += Integer.parseInt(quantity);
            }
            LOGGER.info("Returned {} rows", count);
            LOGGER.info("Total: {}", sum);
            long stop = System.currentTimeMillis();
            LOGGER.info("Calculation executed in {} ms", stop - start);
            return count;
        }
    }
}
