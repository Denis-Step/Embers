package lambda.processors.transactions;

import dynamo.PlaidItemDAO;
import messages.TransactionSmsMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;

import javax.inject.Inject;


public class NewTransactionProcessor {
    private final PlaidItemDAO plaidItemDAO;
    private final EventBridgeClient eventBridge;
    private final TransactionSmsMessageConverter converter;
    private static final String EVENT_BUS_NAME = "SmsBus";
    private static final String EVENT_SOURCE_NAME = "transactions.new";
    private static final String EVENT_DETAIL_NAME = "newMessage";
    private static final Logger LOGGER = LoggerFactory.getLogger(NewTransactionProcessor.class);

    @Inject
    public NewTransactionProcessor(PlaidItemDAO plaidItemDAO,
                                   EventBridgeClient eventBridge,
                                   TransactionSmsMessageConverter converter) {
        this.plaidItemDAO = plaidItemDAO;
        this.eventBridge = eventBridge;
        this.converter = converter;
    }

    public String process(Transaction transaction) throws PlaidItemDAO.ItemException {
        PlaidItem item = plaidItemDAO.getItem(transaction.getUser(), transaction.getInstitutionName());
        if (!item.receiverNumber().isPresent()) {
            return "No receiver number set up for this user";
        }

        String receiverNumber = item.receiverNumber().get();
        String message = createMessage(transaction, receiverNumber);
        LOGGER.info("Sending message {} for {} to {}", message, item.user(), receiverNumber);

        PutEventsRequestEntry putEventsRequestEntry = messagePutEvent(message);
        PutEventsRequest putEventsRequest =  PutEventsRequest.builder().entries(putEventsRequestEntry).build();
        PutEventsResponse response = eventBridge.putEvents(putEventsRequest);
        LOGGER.info("PutEvents Response: {}", response);
        return response.toString();

    }

    private String createMessage(Transaction transaction, String receiverNumber) {
        return "{\"message\": \"" +
                converter.createNewTransactionMessage(transaction) + "\"" + "," +
                "\"receiverNumber\": " + "\"" + receiverNumber + "\"" +
                " }";
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
