package eu.malycha.hazelcast.poc.domain.tasks;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import eu.malycha.hazelcast.poc.domain.Trade;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class SumTask implements Callable<Integer>, Serializable, HazelcastInstanceAware {

    private final String sender;
    private transient HazelcastInstance hz;

    public SumTask(String sender) {
        this.sender = sender;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    @Override
    public Integer call() throws Exception {
        Predicate<String, Trade> predicate = Predicates.sql("sender = '%s'".formatted(sender));
        IMap<String, Trade> map = hz.getMap("trade");
        int result = 0;
        for (String key : map.localKeySet(predicate)) {
            if (Thread.currentThread().isInterrupted()) {
                return -1;
            }
            Trade trade = map.get(key);
            result += Integer.parseInt(trade.getQuantity());
        }
        return result;
    }
}
