package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import lambda.processors.CreateLinkTokenProcessor;
import lambda.requests.CreateLinkTokenRequest;

import java.io.IOException;

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
            return processor.createLinkToken(event);
        } catch (IOException e) {
            // Rethrow Exception to prevent Lambda from succeeding.
            logger.log("Exception" + e.getMessage() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.getMessage()));
        }
    }


}
