package eu.malycha.hazelcast.poc.domain.tasks;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import eu.malycha.hazelcast.poc.domain.TradePojo;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class SumTaskPojo implements Callable<Integer>, Serializable, HazelcastInstanceAware {

    private final String sender;
    private transient HazelcastInstance hz;

    public SumTaskPojo(String sender) {
        this.sender = sender;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hz = hazelcastInstance;
    }

    @Override
    public Integer call() throws Exception {
        Predicate<String, TradePojo> predicate = Predicates.sql("sender = '%s'".formatted(sender));
        IMap<String, TradePojo> map = hz.getMap("trade_pojo");
        int result = 0;
        for (String key : map.localKeySet(predicate)) {
            if (Thread.currentThread().isInterrupted()) {
                return -1;
            }
            TradePojo trade = map.get(key);
            result += Integer.parseInt(trade.getQuantity());
        }
        return result;
    }
}
