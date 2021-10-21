package messages;

import org.immutables.value.Value;

@Value.Immutable
public interface SmsMessage {
    String getMessage();
    String getReceiverNumber();
}
