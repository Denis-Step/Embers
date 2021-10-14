package messages.responses;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * To be implemented by clients sending SMS.
 */
@Value.Immutable
public interface MessageResponse {
    boolean wasSuccessful();
    String originalMessage();
    Optional<String> errorMessage();
}
