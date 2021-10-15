package events;

import com.fasterxml.jackson.databind.ObjectMapper;
import messages.SmsMessage;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import java.util.Collection;

public abstract class AbstractSmsEbClient implements SmsEventCreator, EventBridgeEventCreator {

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

    public abstract void createNewSmsEvent(Collection<SmsMessage> smsMessages);

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
