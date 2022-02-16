package lambda.handlers.items;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerProcessorComponent;
import lambda.processors.items.ItemProcessor;
import lambda.requests.items.GetItemRequest;
import external.plaid.entities.PlaidItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public class GetItemHandler implements RequestHandler<GetItemRequest, List<PlaidItem>> {
    private final ItemProcessor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(GetItemHandler.class);

    public GetItemHandler() {this.processor = DaggerProcessorComponent.create().buildItemProcessor();};

    @Inject
    public GetItemHandler(ItemProcessor processor) {
        this.processor = processor;
    }

    @Override
    public List<PlaidItem> handleRequest(GetItemRequest event, Context context){
        LOGGER.info("Getting Item : {}", event.toString());
        List<PlaidItem> items = this.processor.getItems(event.getUser(), event.getInstitutionIdAccessToken());
        LOGGER.info("Retrieved item: {}",items.toString());
        return items;
    }
}
