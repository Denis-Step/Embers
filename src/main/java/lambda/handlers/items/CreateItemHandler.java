package lambda.handlers.items;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerProcessorComponent;
import lambda.processors.items.ItemProcessor;
import lambda.requests.items.CreateItemRequest;
import external.plaid.entities.PlaidItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class CreateItemHandler implements RequestHandler<CreateItemRequest, PlaidItem> {
    private final ItemProcessor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateItemHandler.class);


    public CreateItemHandler() {this.processor = DaggerProcessorComponent.create().buildItemProcessor();}

    @Inject
    public CreateItemHandler(ItemProcessor processor) {this.processor = processor;}

    @Override
    public PlaidItem handleRequest(CreateItemRequest event, Context context) {
        LOGGER.info("Getting access token for {}", event.getPublicToken());
        LOGGER.info(event.toString());

        try {
            PlaidItem item = processor.createPlaidItem(event);
            LOGGER.info("Created item:" + item.getId());
            return item;
        } catch (IOException e){
            // Rethrow Exception to prevent Lambda from succeeding.
            LOGGER.info("Exception" + e.getMessage() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.getMessage()));
        }
    }
}
