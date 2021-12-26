package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerProcessorComponent;
import lambda.processors.items.ItemProcessor;
import lambda.requests.items.CreateItemRequest;
import external.plaid.entities.PlaidItem;

import javax.inject.Inject;
import java.io.IOException;

public class CreateItemHandler implements RequestHandler<CreateItemRequest, String> {
    private final ItemProcessor processor;

    public CreateItemHandler() {this.processor = DaggerProcessorComponent.create().buildItemProcessor();}

    @Inject
    public CreateItemHandler(ItemProcessor processor) {this.processor = processor;}

    @Override
    public String handleRequest(CreateItemRequest event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Getting access token for" + event.getPublicToken());
        logger.log(event.toString());

        try {
            PlaidItem item = processor.createPlaidItem(event);
            logger.log("Created item:" + item.ID());
            return item.toString();
        } catch (IOException e){
            // Rethrow Exception to prevent Lambda from succeeding.
            logger.log("Exception" + e.getMessage() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.getMessage()));
        }
    }
}
