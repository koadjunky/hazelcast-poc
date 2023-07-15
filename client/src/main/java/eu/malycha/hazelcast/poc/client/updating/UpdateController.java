package eu.malycha.hazelcast.poc.client.updating;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import eu.malycha.hazelcast.poc.client.loading.LoaderService;
import eu.malycha.hazelcast.poc.domain.Trade;
import eu.malycha.hazelcast.poc.domain.TradePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

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
            TradePojo updated = clone(trade);
            data.set(key, updated);
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
                .build();
            data.set(key, updated);
        }
        long stop = System.currentTimeMillis();
        LOGGER.info("Updated {} records in {} ms", number, stop - start);
    }

    @GetMapping("/protobuf/{number}/{ttl}/{delay}")
    public void protobufUpdateTtl(int number, int ttl, int delay) {
        IMap<String, Trade> data  = hz.getMap("trade_ttl");
        for (int i = 0; i < number; i++) {
            String key = "%010d".formatted(i);
            Trade trade = data.get(key);
            Trade updated = trade.toBuilder()
                .build();
            data.putAsync(key, updated, ttl, TimeUnit.MILLISECONDS);
            LOGGER.info("Writing record {} with ttl={}ms", i, ttl);
            LoaderService.delay(delay);
        }
    }

    public TradePojo clone(TradePojo source) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(source);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (TradePojo) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.error("Cannot clone", ex);
            return source;
        }
    }
}
