package lambda.processors.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.DaggerPlaidComponent;
import dynamo.PlaidItemDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plaid.entities.PlaidItem;
import plaid.entities.Transaction;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;

import javax.inject.Inject;


public class NewTransactionProcessor {
    private final PlaidItemDAO plaidItemDAO;
    private final EventBridgeClient eventBridge;
    private static final String EVENT_BUS_NAME = "SmsEvents";
    private static final String EVENT_SOURCE_NAME = "transactions.new";
    private static final String EVENT_DETAIL_NAME = "newMessage";
    private static final Logger LOGGER = LoggerFactory.getLogger(NewTransactionProcessor.class);

    @Inject
    public NewTransactionProcessor(PlaidItemDAO plaidItemDAO, EventBridgeClient eventBridge) {
        this.plaidItemDAO = plaidItemDAO;
        this.eventBridge = eventBridge;
    }

    public String process(Transaction transaction) throws PlaidItemDAO.ItemException {
        PlaidItem item = plaidItemDAO.getItem(transaction.getUser(), transaction.getInstitutionName());
        if (!item.receiverNumber().isPresent()) {
            return "No receiver number set up for this user";
        }

        String receiverNumber = item.receiverNumber().get();
        LOGGER.info("Sending send message event for {} to {}", item.user(), receiverNumber);

        PutEventsRequestEntry putEventsRequestEntry = messagePutEvent(createMessage(transaction));
        PutEventsRequest putEventsRequest =  PutEventsRequest.builder().entries(putEventsRequestEntry).build();
        PutEventsResponse response = eventBridge.putEvents(putEventsRequest);
        LOGGER.info("PutEvents Response: {}", response);
        return response.toString();

    }

    private String createMessage(Transaction transaction) {
        return "{\"Message\": \"" +
                "New Transaction: " +
                transaction.description + " " +
                "for " +
                transaction.amount + " " +
                "at " +
                transaction.merchantName +
                "on " +
                transaction.institutionName +
                "\"}";
    }

    private PutEventsRequestEntry messagePutEvent(String message) {
        return PutEventsRequestEntry.builder()
                .eventBusName(EVENT_BUS_NAME)
                .source(EVENT_SOURCE_NAME)
                .detailType(EVENT_DETAIL_NAME)
                .detail(message)
                .build();
    }
}
