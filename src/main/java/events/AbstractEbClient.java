package events;

import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Minimum functionality for EB to push events. Can only put events to a single event bus.
 */
public abstract class AbstractEbClient<T> {

    protected final EventBridgeClient eventBridgeClient;
    protected final String eventBusName;


    public AbstractEbClient(EventBridgeClient eventBridgeClient,
                            String eventBusName) {

        this.eventBridgeClient = eventBridgeClient;
        this.eventBusName = eventBusName;
    }

    public PutEventsResponse publishEvent(String detailMessage,
                                          String sourceName,
                                          String detailType) {
        return this.publishEvents(createPutEventsRequest(List.of(detailMessage), sourceName, detailType));
    }

    /**
     * For batching.
     */
    public PutEventsResponse publishEvents(List<String> detailMessages,
                                           String sourceName,
                                           String detailType) {
        return this.publishEvents(createPutEventsRequest(detailMessages, sourceName, detailType));
    }


    private PutEventsResponse publishEvents(PutEventsRequest putEventsRequest) {
        return this.eventBridgeClient.putEvents(putEventsRequest);
    }

    private PutEventsRequest createPutEventsRequest(List<String> detailMessages,
                                                    String sourceName,
                                                    String detailType) {
        List<PutEventsRequestEntry> putEventsRequestEntries = detailMessages.stream()
                .map(message -> createPutEventsRequestEntry(message, sourceName, detailType))
                .collect(Collectors.toList());

        return PutEventsRequest.builder()
                .entries(putEventsRequestEntries)
                .build();
    }

    private PutEventsRequestEntry createPutEventsRequestEntry(String detailMessage,
                                                              String sourceName,
                                                              String detailType) {
        return PutEventsRequestEntry.builder()
                .eventBusName(this.eventBusName)
                .source(sourceName)
                .detailType(detailType)
                .detail(detailMessage)
                .build();
    }





}
