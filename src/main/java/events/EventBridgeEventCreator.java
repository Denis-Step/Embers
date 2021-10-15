package events;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

/**
 * Generalized EB Client essential methods.
 * Needs bus name to be provided for explicitness.
 */
public interface EventBridgeEventCreator {

    String getEventBusName();
    ObjectMapper getObjectMapper();
    EventBridgeClient getEventBridgeClient();

}
