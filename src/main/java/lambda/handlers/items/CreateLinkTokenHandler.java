package lambda.handlers.items;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerProcessorComponent;
import lambda.processors.items.CreateLinkTokenProcessor;
import lambda.requests.items.CreateLinkTokenRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CreateLinkTokenHandler implements RequestHandler<CreateLinkTokenRequest, String> {
    private final CreateLinkTokenProcessor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateLinkTokenHandler.class);

    public CreateLinkTokenHandler() {this.processor = DaggerProcessorComponent.create().buildLinkTokenProcessor();}

    public CreateLinkTokenHandler(CreateLinkTokenProcessor processor) {
        this.processor = processor;
    }

    @Override
    public String handleRequest(CreateLinkTokenRequest event, Context context) {
        LOGGER.info("Getting link token for " + event.getUser() +
                " products " + event.getProducts() + " and webhook?: " + event.getWebhookEnabled());

        try {
            return processor.createLinkToken(event);
        } catch (IOException e) {
            // Rethrow Exception to prevent Lambda from succeeding.
            LOGGER.error("Exception" + e.getMessage() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.getMessage()));
        }
    }


}
