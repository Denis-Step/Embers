package events;

import messages.SmsMessage;

import java.util.Collection;

/**
 * Allow different implementations of clients.
 */
public interface SmsEventCreator {

    void createNewSmsEvent(
            SmsMessage smsMessage);

    void createNewSmsEvent(
            Collection<SmsMessage> smsMessages
    );

}
