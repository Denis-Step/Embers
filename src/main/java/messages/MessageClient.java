package messages;

import messages.responses.MessageResponse;

/**
 * Allow different implementations of clients (such as SNS-sms and Twilio).
 */
public interface MessageClient {

    String getSenderNumber();
    MessageResponse sendMessage(String receiverNumber, String messageText);

    public static class MessageException extends Exception{
        public MessageException(String errorMessage) {super(errorMessage);}
    }

}
