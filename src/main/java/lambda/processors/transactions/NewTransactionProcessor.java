package lambda.processors.transactions;

import com.twilio.rest.api.v2010.account.Message;
import dynamo.PlaidItemDAO;
import events.impl.SmsEbClient;
import messages.ImmutableSmsMessage;
import messages.SmsMessage;
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


/**
 * Triggered by a new transaction.
 */
public class NewTransactionProcessor {
    private final PlaidItemDAO plaidItemDAO;
    private final SmsEbClient smsEbClient;
    private final TransactionSmsMessageConverter converter;
    private static final Logger LOGGER = LoggerFactory.getLogger(NewTransactionProcessor.class);

    @Inject
    public NewTransactionProcessor(PlaidItemDAO plaidItemDAO,
                                   SmsEbClient smsEbClient,
                                   TransactionSmsMessageConverter converter) {
        this.plaidItemDAO = plaidItemDAO;
        this.smsEbClient = smsEbClient;
        this.converter = converter;
    }

    public String process(Transaction transaction) throws PlaidItemDAO.ItemException {
        PlaidItem item = plaidItemDAO.getItem(transaction.getUser(), transaction.getInstitutionName());
        if (!item.receiverNumber().isPresent()) {
            throw new PlaidItemDAO.ItemException("No receiver number found for this user");
        }

        String receiverNumber = item.receiverNumber().get();
        SmsMessage smsMessage = createMessage(transaction, receiverNumber);
        LOGGER.info("Created message {} for {} to {}", smsMessage.message(), item.user(), receiverNumber);

        this.smsEbClient.createNewSmsEvent(smsMessage);
        return smsMessage.toString();

    }

    private SmsMessage createMessage(Transaction transaction, String receiverNumber) {
        String messageText =  this.converter.createNewTransactionMessage(transaction);
        return ImmutableSmsMessage.builder()
                .message(messageText)
                .receiverNumber(receiverNumber)
                .build();
    }
}
