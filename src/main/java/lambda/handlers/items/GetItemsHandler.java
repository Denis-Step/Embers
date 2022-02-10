package lambda.handlers.items;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerProcessorComponent;
import lambda.processors.items.ItemProcessor;
import lambda.requests.items.GetItemRequest;
import external.plaid.entities.PlaidItem;

import java.util.List;

public class GetItemsHandler implements RequestHandler<GetItemRequest, List<PlaidItem>> {
    private final ItemProcessor processor;

    public GetItemsHandler() {this.processor = DaggerProcessorComponent.create().buildItemProcessor();};

    @Override
    public List<PlaidItem> handleRequest(GetItemRequest event, Context context){
        LambdaLogger logger = context.getLogger();
        logger.log("Getting Item for:" +  event.toString());
        List<PlaidItem> items = this.processor.getItems(event.getUser(), event.getInstitutionIdAccessToken());
        logger.log(items.toString());
        return items;
    }
}
