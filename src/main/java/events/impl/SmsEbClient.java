package events.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.AbstractSmsEbClient;
import messages.SmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Puts events to the SMS EB event bus.
 * The business logic is done here.
 */
public class SmsEbClient extends AbstractSmsEbClient {

    private static final String NEW_TRANSACTION_SMS_EVENT_SOURCE_NAME = "transactions.new";
    private static final String NEW_TRANSACTION_SMS_EVENT_DETAIL_NAME = "newMessage";
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsEbClient.class);

    @Inject
    public SmsEbClient(EventBridgeClient eventBridgeClient,
                                String eventBusName,
                                ObjectMapper objectMapper) {

        super(eventBridgeClient, eventBusName, objectMapper);
    }

    public void createNewSmsEvent(SmsMessage smsMessage) {
        try {
            PutEventsRequest eventsRequest = PutEventsRequest.builder()
                    .entries(newTransactionSmsPutEvent(smsMessage))
                    .build();

            PutEventsResponse result = this.eventBridgeClient.putEvents(eventsRequest);
            LOGGER.info("Put Event {} with Result {}", eventsRequest.entries().toArray(), result);
        } catch (JsonProcessingException e) {
            LOGGER.info("Couldn't create new SMS event for message {} ", smsMessage, e);
        }
    }

    public void createNewSmsEvent(Collection<SmsMessage> smsMessages) {
        smsMessages.stream().forEach(
                newSms -> createNewSmsEvent(newSms));
    }


    private PutEventsRequestEntry newTransactionSmsPutEvent(SmsMessage smsMessage) throws JsonProcessingException {
        return PutEventsRequestEntry.builder()
                .eventBusName(this.eventBusName)
                .source(NEW_TRANSACTION_SMS_EVENT_SOURCE_NAME)
                .detailType(NEW_TRANSACTION_SMS_EVENT_DETAIL_NAME)
                .detail(objectMapper.writeValueAsString(smsMessage))
                .build();
    }
}
