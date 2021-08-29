package lambda.handlers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerAwsComponent;
import dagger.DaggerPlaidComponent;
import dynamo.PlaidItemDAO;
import lambda.processors.CreateItemProcessor;
import lambda.requests.CreateItemRequest;
import plaid.clients.ItemRequester;
import plaid.entities.PlaidItem;
import plaid.responses.PublicTokenExchangeResponse;

import java.io.IOException;

public class CreateItemHandler implements RequestHandler<CreateItemRequest, String> {
    private final CreateItemProcessor processor;

    public CreateItemHandler() {
        this.processor = new CreateItemProcessor();
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
            throw new RuntimeException(String.format("Exception: %s", e.toString()));
        }
    }
}
