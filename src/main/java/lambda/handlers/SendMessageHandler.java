package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerTwilioComponent;
import lambda.processors.MessageProcessor;
import lambda.requests.SendTransactionsMessageRequest;

public class SendMessageHandler implements RequestHandler<SendTransactionsMessageRequest, String> {

    private final MessageProcessor messageProcessor;

    public SendMessageHandler() {
        this.messageProcessor = DaggerTwilioComponent.create().buildMessageProcessor();
    }

    @Override
    public String handleRequest(SendTransactionsMessageRequest request, Context context) {
        return this.messageProcessor.sendMessage(request);
    }

}
