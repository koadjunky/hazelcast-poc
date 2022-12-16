package eu.malycha.hazelcast.poc.client.transaction;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalMap;
import eu.malycha.hazelcast.poc.client.loading.LoaderService;
import eu.malycha.hazelcast.poc.domain.Trade;
import eu.malycha.hazelcast.poc.domain.TradePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    private final HazelcastInstance hz;

    public TransactionController(HazelcastInstance hazelcast) {
        this.hz = hazelcast;
    }

    @PostMapping("/onephase/{number}")
    @ResponseStatus(HttpStatus.CREATED)
    public void loadOnePhase(int number) {
        loadTransactional(number, TransactionOptions.TransactionType.ONE_PHASE);
    }

    @PostMapping("/twophase/{number}")
    @ResponseStatus(HttpStatus.CREATED)
    public void loadTwoPhase(int number) {
        loadTransactional(number, TransactionOptions.TransactionType.TWO_PHASE);
    }

    private void loadTransactional(int number, TransactionOptions.TransactionType transactionType) {

        TransactionOptions options = new TransactionOptions().setTransactionType(transactionType);
        TransactionContext context = hz.newTransactionContext(options);

        long start = System.currentTimeMillis();

        for (int i = 0; i < number; i++) {
            context.beginTransaction();
            try {
                TransactionalMap<String, Trade> protoMap = context.getMap("trade");
                TransactionalMap<String, TradePojo> pojoMap = context.getMap("trade_pojo");

                String id = "%010d".formatted(i);
                String sender = LoaderService.getRandomAccount();
                String counterpart = LoaderService.getRandomAccount();
                String quantity = LoaderService.getRandomQuantity();
                Trade protoRecord = Trade.newBuilder()
                    .setTradeId(id)
                    .setSender(sender)
                    .setCounterpart(counterpart)
                    .setQuantity(quantity)
                    .build();
                TradePojo pojoRecord = TradePojo.newBuilder()
                    .setTradeId(id)
                    .setSender(sender)
                    .setCounterpart(counterpart)
                    .setQuantity(quantity)
                    .build();
                protoMap.put(id, protoRecord);
                pojoMap.put(id, pojoRecord);

                context.commitTransaction();
            } catch (Throwable t) {
                context.rollbackTransaction();
            }
        }

        long stop = System.currentTimeMillis();
        LOGGER.info("Loaded {} records in {} ms", number, stop - start);
    }
}
