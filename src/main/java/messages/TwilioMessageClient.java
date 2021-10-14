package messages;

import com.twilio.rest.api.v2010.account.Message;
import external.twilio.TwilioMessageSender;
import messages.responses.ImmutableMessageResponse;
import messages.responses.MessageResponse;

import javax.inject.Inject;

public class TwilioMessageClient implements MessageClient {
    private final TwilioMessageSender twilioMessageSender;

    @Inject
    public TwilioMessageClient(TwilioMessageSender twilioMessageSender) {
        this.twilioMessageSender = twilioMessageSender;
    }

    public String getSenderNumber() {return this.twilioMessageSender.getSenderNumber();}

    /**
     * @param receiverNumber the number to send an SMS to.
     * @param messageText the text to send.
     * @return
     */
    public MessageResponse sendMessage(String receiverNumber, String messageText) {
        try {
            Message twilioMessage = this.twilioMessageSender.sendMessage(receiverNumber, messageText);
            return ImmutableMessageResponse.builder()
                    .originalMessage(messageText)
                    .wasSuccessful(true)
                    .build();
        } catch (Exception e) {
            return ImmutableMessageResponse.builder()
                    .originalMessage(messageText)
                    .wasSuccessful(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}
