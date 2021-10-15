package events.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.AbstractTransactionsEbClient;
import external.plaid.entities.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Main class to be used for generating EB events from {@link Transaction}s.
 * Business logic is in here.
 */
public class TransactionsEbClient extends AbstractTransactionsEbClient {
    private static final String NEW_TRANSACTION_EVENT_SOURCE_NAME = "transactions.receive";
    private static final String NEW_TRANSACTION_EVENT_DETAIL_NAME = "newTransaction";
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionsEbClient.class);


    @Inject
    public TransactionsEbClient(EventBridgeClient eventBridgeClient,
                                        String eventBusName,
                                        ObjectMapper objectMapper) {

        super(eventBridgeClient, eventBusName, objectMapper);
    }

    public void createNewTransactionEvent(Transaction newTransaction) {
        try {
            PutEventsRequest eventsRequest = PutEventsRequest.builder()
                    .entries(newTransactionPutEvent(newTransaction))
                    .build();

            PutEventsResponse result = this.eventBridgeClient.putEvents(eventsRequest);
            LOGGER.info("Put Event {} with Result {}", eventsRequest.entries().toArray(), result);
        } catch (JsonProcessingException e) {
            LOGGER.info("Couldn't create new transaction event for transaction {} ", newTransaction, e);
        }
    }

    public void createNewTransactionEvent(Collection<Transaction> newTransactions) {
        newTransactions.stream().forEach(
                newTransaction -> createNewTransactionEvent(newTransaction));
    }


    private PutEventsRequestEntry newTransactionPutEvent(Transaction transaction) throws JsonProcessingException {
        return PutEventsRequestEntry.builder()
                .eventBusName(this.eventBusName)
                .source(NEW_TRANSACTION_EVENT_SOURCE_NAME)
                .detailType(NEW_TRANSACTION_EVENT_DETAIL_NAME)
                .detail(objectMapper.writeValueAsString(transaction))
                .build();
    }
}
