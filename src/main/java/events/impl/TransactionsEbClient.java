package events.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.AbstractEbClient;
import events.TransactionsEventPublisher;
import external.plaid.entities.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Main class to be used for generating EB events from {@link Transaction}s.
 * Business logic is in here.
 */
public class TransactionsEbClient extends AbstractEbClient implements TransactionsEventPublisher {

    private final ObjectMapper objectMapper;

    private static final String NEW_TRANSACTION_EVENT_DETAIL_TYPE = "newTransaction";
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionsEbClient.class);


    @Inject
    public TransactionsEbClient(EventBridgeClient eventBridgeClient,
                                String eventBusName,
                                ObjectMapper objectMapper) {

        super(eventBridgeClient, eventBusName);
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishNewTransactions(Collection<Transaction> newTransactions, String sourceName) {
        List<String> detailMessages = new ArrayList<>();

        for (Transaction newTransaction: newTransactions) {
            try {
                String detailMessage = createNewTransactionDetailMessage(newTransaction);
                detailMessages.add(detailMessage);
            } catch (JsonProcessingException e) {
                LOGGER.info("Couldn't create new transaction event for transaction {}, SKIPPING this transaction.",
                        newTransaction.getTransactionId());
            }
        }

        this.publishEvents(detailMessages, sourceName, NEW_TRANSACTION_EVENT_DETAIL_TYPE);
     }

    @Override
    public void publishNewTransaction(Transaction newTransaction, String sourceName) {
        this.publishNewTransactions(List.of(newTransaction), sourceName);
    }

    private String createNewTransactionDetailMessage(Transaction transaction) throws JsonProcessingException {
        return this.objectMapper.writeValueAsString(transaction);
    }

}
