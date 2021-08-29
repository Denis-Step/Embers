package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import dagger.DaggerPlaidComponent;
import lambda.processors.CreateItemProcessor;
import lambda.processors.CreateLinkTokenProcessor;
import lambda.requests.CreateLinkTokenRequest;
import plaid.clients.LinkGrabber;

import java.io.IOException;
import java.util.List;

public class CreateLinkTokenHandler implements RequestHandler<CreateLinkTokenRequest, String> {
    private final CreateLinkTokenProcessor processor;

    public CreateLinkTokenHandler() {
        this.processor = new CreateLinkTokenProcessor();
    }

    @Override
    public String handleRequest(CreateLinkTokenRequest event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Getting link token for " + event.getUser());

        try {
            String linkToken = processor.createLinkToken(event);
            return linkToken;
        } catch (IOException e) {
            // Rethrow Exception to prevent Lambda from succeeding.
            logger.log("Exception" + e.getMessage() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.toString()));
        }
    }


}
