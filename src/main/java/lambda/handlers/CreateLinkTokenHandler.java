package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import dagger.DaggerPlaidComponent;
import lambda.processors.items.CreateLinkTokenProcessor;
import lambda.requests.items.CreateLinkTokenRequest;

import java.io.IOException;

public class CreateLinkTokenHandler implements RequestHandler<CreateLinkTokenRequest, String> {
    private final CreateLinkTokenProcessor processor;

    public CreateLinkTokenHandler() {this.processor = DaggerPlaidComponent.create().buildLinkTokenProcessor();}

    public CreateLinkTokenHandler(CreateLinkTokenProcessor processor) {
        this.processor = processor;
    }

    @Override
    public String handleRequest(CreateLinkTokenRequest event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Getting link token for " + event.getUser() + " products " + event.getProducts() + " and webhook?: " + event.webhook);

        try {
            return processor.createLinkToken(event);
        } catch (IOException e) {
            // Rethrow Exception to prevent Lambda from succeeding.
            logger.log("Exception" + e.getMessage() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.getMessage()));
        }
    }


}
