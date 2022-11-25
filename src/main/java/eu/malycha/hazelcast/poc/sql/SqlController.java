package eu.malycha.hazelcast.poc.sql;

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

    @GetMapping("/sender/{name}")
    public int senderCount(String name) {
        try (SqlResult result = hz.getSql().execute("SELECT * FROM trade")) {
            int count = 0;
            for (SqlRow row : result) {
                count++;
            }
            LOGGER.info("Returned {} rows", count);
            return count;
        }
    }
}
