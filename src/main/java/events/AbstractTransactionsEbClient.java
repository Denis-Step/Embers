package events;

import com.fasterxml.jackson.databind.ObjectMapper;
import external.plaid.entities.Transaction;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import java.util.Collection;

/**
 * Minimum functionality for EB to push transactions as events.
 */
public abstract class AbstractTransactionsEbClient implements EventBridgeEventCreator, TransactionsEventCreator {

    protected final EventBridgeClient eventBridgeClient;
    protected final String eventBusName;
    protected final ObjectMapper objectMapper;


    public AbstractTransactionsEbClient(EventBridgeClient eventBridgeClient,
                                        String eventBusName,
                                        ObjectMapper objectMapper) {

        this.eventBridgeClient = eventBridgeClient;
        this.eventBusName = eventBusName;
        this.objectMapper = objectMapper;
    }

    public abstract void createNewTransactionEvent(Transaction transaction);

    public void createNewTransactionEvent(Collection<Transaction> transactions) {
        transactions.stream().forEach(this::createNewTransactionEvent);
    };

    @Override
    public EventBridgeClient getEventBridgeClient() {
        return eventBridgeClient;
    }

    @Override
    public String getEventBusName() {
        return eventBusName;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
