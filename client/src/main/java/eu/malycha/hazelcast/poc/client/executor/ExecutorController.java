package eu.malycha.hazelcast.poc.client.executor;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import eu.malycha.hazelcast.poc.domain.tasks.SumTask;
import eu.malycha.hazelcast.poc.domain.tasks.SumTaskPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// TODO: Pipelines
// TODO: Compare SQL and executor service performance
// TODO: Split brain protection

@RestController
@RequestMapping("/api/execute/sum/quantity")
public class ExecutorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorController.class);

    private final HazelcastInstance hz;

    public ExecutorController(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    @GetMapping("/pojo/sender/{name}")
    public void pojoSenderSum(String name) throws ExecutionException, InterruptedException {
        IExecutorService executor = hz.getExecutorService("default");
        Map<Member, Future<Integer>> results = executor.submitToAllMembers(new SumTaskPojo(name));
        int result = 0;
        for (Map.Entry<Member, Future<Integer>> entry : results.entrySet()) {
            Integer value = get(entry.getValue()).orElse(0);
            LOGGER.info("Result from member {}: {}", entry.getKey().getAddress(), value);
            result += value;
        }
        LOGGER.info("Total: {}", result);
    }

    @GetMapping("/protobuf/sender/{name}")
    public void protobufSenderSum(String name) throws ExecutionException, InterruptedException {
        IExecutorService executor = hz.getExecutorService("default");
        Map<Member, Future<Integer>> results = executor.submitToAllMembers(new SumTask(name));
        int result = 0;
        for (Map.Entry<Member, Future<Integer>> entry : results.entrySet()) {
            Integer value = get(entry.getValue()).orElse(0);
            LOGGER.info("Result from member {}: {}", entry.getKey().getAddress(), value);
            result += value;
        }
        LOGGER.info("Total: {}", result);
    }

    private <T> Optional<T> get(Future<T> future) throws ExecutionException, InterruptedException {
        try {
            return Optional.of(future.get(10000, TimeUnit.MILLISECONDS));
        } catch (TimeoutException ex) {
            LOGGER.warn("Timeout!");
            future.cancel(true);
            return Optional.empty();
        }
    }
}
