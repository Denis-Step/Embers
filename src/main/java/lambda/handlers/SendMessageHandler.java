package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerTwilioComponent;
import lambda.processors.messages.MessageProcessor;
import lambda.requests.SendMessageRequest;

public class SendMessageHandler implements RequestHandler<SendMessageRequest, String> {
    private MessageProcessor processor;

    public SendMessageHandler() {
        this.processor = DaggerTwilioComponent.create().buildMessageProcessor();
    }

    @Override
    public String handleRequest(SendMessageRequest request, Context context) {
        context.getLogger().log(request.toString());
        return this.processor.sendMessage(request.getReceiverNumber(), request.getMessage());
    }

}
