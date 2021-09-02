package lambda.processors;

import twilio.MessageClient;

import javax.inject.Inject;

public class MessageProcessor {

    private final MessageClient messageClient;

    @Inject
    public MessageProcessor(MessageClient messageClient) {
        this.messageClient = messageClient;
    }

    public String sendMessage(String receiverNumber, String message) {
        return messageClient.sendMessage(receiverNumber, message).toString();
    }
}
