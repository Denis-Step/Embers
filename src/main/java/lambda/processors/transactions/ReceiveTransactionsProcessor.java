package lambda.processors.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dynamo.TransactionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plaid.entities.Transaction;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReceiveTransactionsProcessor {
    private final TransactionDAO transactionDAO;
    private final EventBridgeClient eventBridge;
    private final ObjectMapper objectMapper;
    private static final String EVENT_SOURCE_NAME = "transactions.receive";
    private static final String EVENT_DETAIL_NAME = "newTransaction";
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveTransactionsProcessor.class);

    @Inject
    public ReceiveTransactionsProcessor(TransactionDAO transactionDAO, EventBridgeClient eventBridge, ObjectMapper objectMapper) {
        this.transactionDAO = transactionDAO;
        this.eventBridge = eventBridge;
        this.objectMapper = objectMapper;
    }

    /**
     * Filter for new transactions and save.
     * @param transactions : incoming transactions, from polling or from webhook push.
     * @return New Transactions saved to DDB.
     */
    public List<Transaction> saveAndReturnNewTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .filter(tx -> !(transactionExistsInDdb(tx)))
                .map(newTransaction -> {
                    transactionDAO.save(newTransaction);

                    try {
                        PutEventsRequest eventsRequest = PutEventsRequest.builder()
                                .entries(transactionPutEvent(newTransaction))
                                .build();

                        PutEventsResponse result = eventBridge.putEvents(eventsRequest);
                        LOGGER.info("Put Event with Result {}", result);
                    } catch (JsonProcessingException e) {
                        LOGGER.info("Couldn't create event for {}", newTransaction);
                    }

                    return newTransaction;
                })
                .collect(Collectors.toList());
    }

    private boolean transactionExistsInDdb(Transaction transaction) {
        List<Transaction> queryResult = transactionDAO.query(transaction.getUser(),
                transaction.getInstitutionName(),
                transaction.getAccountId(),
                transaction.getTransactionId());
        LOGGER.info(transaction.toString());
        LOGGER.info(queryResult.toString());
        return !queryResult.isEmpty();
    }

    private PutEventsRequestEntry transactionPutEvent(Transaction transaction) throws JsonProcessingException {
        return PutEventsRequestEntry.builder()
                .source(EVENT_SOURCE_NAME)
                .detailType(EVENT_DETAIL_NAME)
                .detail(objectMapper.writeValueAsString(transaction))
                .build();
    }

}
