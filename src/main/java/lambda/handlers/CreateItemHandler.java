package lambda.handlers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerAwsComponent;
import dagger.DaggerPlaidComponent;
import dynamo.ItemsDAO;
import lambda.requests.ExchangePublicTokenRequest;
import plaid.ItemRequester;
import plaid.PlaidItem;

import java.io.IOException;
import java.time.Instant;

public class CreateItemHandler implements RequestHandler<ExchangePublicTokenRequest, String> {
    DynamoDBMapper dynamoDBMapper;
    ItemRequester itemRequester;

    public CreateItemHandler() {
        dynamoDBMapper = DaggerAwsComponent.create().buildDynamo();
        itemRequester = DaggerPlaidComponent.create().buildItemRequestor();
    }

    @Override
    public String handleRequest(ExchangePublicTokenRequest event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Getting access token for" + event.getPublicToken());

        try {
            PlaidItem plaidItem = itemRequester.requestItem(event.getPublicToken());
            logger.log("Received item: " + plaidItem.getItemId() + "with access token: " + plaidItem.getAccessToken());
            ItemsDAO itemsDAO = createItemsDao(event.getUser(), plaidItem);
            dynamoDBMapper.save(itemsDAO);
            return plaidItem.getItemId();
        } catch (IOException e) {
            logger.log("Exception" + e.toString() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.toString()));
        }
    }

    // Time now and empty transactions list stored.
    // @TODO: Change time implementation.
    private ItemsDAO createItemsDao(String user, PlaidItem plaidItem) {
        String timeNow = Instant.now().toString();
        ItemsDAO itemsDAO = new ItemsDAO();

        // Set sort key
        String itemAccessToken = plaidItem.getItemId()+ "#" + plaidItem.getAccessToken();
        itemsDAO.setItemAccessToken(itemAccessToken);
        itemsDAO.setUser(user);
        itemsDAO.setDate(timeNow);
        return itemsDAO;
    }
}
