package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerPlaidComponent;
import lambda.processors.items.ItemProcessor;
import lambda.requests.items.GetItemRequest;
import external.plaid.entities.PlaidItem;

import java.util.List;

public class GetItemHandler implements RequestHandler<GetItemRequest, List<PlaidItem>> {
    private final ItemProcessor processor;

    public GetItemHandler() {this.processor = DaggerPlaidComponent.create().buildItemProcessor();};

    @Override
    public List<PlaidItem> handleRequest(GetItemRequest event, Context context){
        LambdaLogger logger = context.getLogger();
        logger.log("Getting Item for:" +  event.toString());
        return this.processor.getItems(event);
    }
}
