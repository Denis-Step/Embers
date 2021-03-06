package events;

import com.fasterxml.jackson.databind.ObjectMapper;
import messages.SmsMessage;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import java.util.Collection;

public abstract class AbstractSmsEbClient implements SmsEventCreator {

    protected final EventBridgeClient eventBridgeClient;
    protected final String eventBusName;
    protected final ObjectMapper objectMapper;


    public AbstractSmsEbClient(EventBridgeClient eventBridgeClient,
                                        String eventBusName,
                                        ObjectMapper objectMapper) {

        this.eventBridgeClient = eventBridgeClient;
        this.eventBusName = eventBusName;
        this.objectMapper = objectMapper;
    }

    public abstract void createNewSmsEvent(SmsMessage smsMessage);

    public void createNewSmsEvent(Collection<SmsMessage> smsMessages) {
        smsMessages.stream().forEach(sms -> createNewSmsEvent(sms));
    }

}
