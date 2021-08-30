package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lambda.processors.ItemProcessor;
import lambda.requests.CreateItemRequest;
import plaid.entities.PlaidItem;

import java.io.IOException;

public class CreateItemHandler implements RequestHandler<CreateItemRequest, String> {
    private final ItemProcessor processor;

    public CreateItemHandler() {
        this.processor = new ItemProcessor();
    }

    @Override
    public String handleRequest(CreateItemRequest event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Getting access token for" + event.getPublicToken());

        try {
            PlaidItem item = processor.createPlaidItem(event);
            logger.log("Created item:" + item.getID());
            return item.toString();
        } catch (IOException e){
            // Rethrow Exception to prevent Lambda from succeeding.
            logger.log("Exception" + e.getMessage() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.getMessage()));
        }
    }
}
