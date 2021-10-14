package lambda.processors.messages;

import messages.MessageClient;

import javax.inject.Inject;

public class MessageProcessor {

    private final MessageClient messageClient;
    private final String DEFAULT_NUMBER = "+19175478272";

    @Inject
    public MessageProcessor(MessageClient messageClient) {
        this.messageClient = messageClient;
    }

    public String sendMessage(String receiverNumber, String message) {
        return messageClient.sendMessage(receiverNumber, message).toString();
    }

    public String sendMessage(String message) {
        return this.sendMessage(DEFAULT_NUMBER, message);
    }
}
