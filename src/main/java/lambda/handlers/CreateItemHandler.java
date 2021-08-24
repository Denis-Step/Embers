package lambda.handlers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerAwsComponent;
import dagger.DaggerPlaidComponent;
import dynamo.PlaidItemDAO;
import lambda.requests.CreateItemRequest;
import plaid.clients.ItemRequester;
import plaid.entities.PlaidItem;
import plaid.responses.PublicTokenExchangeResponse;

import java.io.IOException;

public class CreateItemHandler implements RequestHandler<CreateItemRequest, String> {
    DynamoDBMapper dynamoDBMapper;
    ItemRequester itemRequester;

    public CreateItemHandler() {
        dynamoDBMapper = DaggerAwsComponent.create().buildDynamo();
        itemRequester = DaggerPlaidComponent.create().buildItemRequestor();
    }

    @Override
    public String handleRequest(CreateItemRequest event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Getting access token for" + event.getPublicToken());

        try {
            PlaidItem plaidItem = createPlaidItem(event);
            logger.log("Received item: " + plaidItem.toString());
            dynamoDBMapper.save(createItemsDao(plaidItem));
            logger.log("Saved item:" + plaidItem.getID());
            return plaidItem.toString();
        } catch (IOException e){
            // Rethrow Exception to prevent Lambda from succeeding.
            logger.log("Exception" + e.toString() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.toString()));
        }

    }

    // Calls Plaid client to request a new Item and uses info from incoming request
    // to build PlaidItem.
    private PlaidItem createPlaidItem (CreateItemRequest createItemRequest) throws IOException {
        PublicTokenExchangeResponse itemInfo = itemRequester.requestItem(createItemRequest.getPublicToken());

        return PlaidItem.getBuilder()
                .setID(itemInfo.getID())
                .setAccessToken(itemInfo.getAccessToken())
                .setUser(createItemRequest.getUser())
                .setDateCreated(createItemRequest.getDateCreated())
                .setAvailableProducts(createItemRequest.getAvailableProducts())
                .setAccounts(createItemRequest.getAccounts())
                .setInstitutionId(createItemRequest.getInstitutionId())
                .setMetaData(createItemRequest.getMetaData())
                .build();
    }

    private PlaidItemDAO createItemsDao(PlaidItem plaidItem) {
        PlaidItemDAO plaidItemDAO = new PlaidItemDAO();

        plaidItemDAO.setUser(plaidItem.getUser()); // Set partition key.
        plaidItemDAO.setInstitutionId(plaidItem.getInstitutionId()); // Set sort key.
        plaidItemDAO.setID(plaidItem.getID());
        plaidItemDAO.setAccessToken(plaidItem.getAccessToken());
        plaidItemDAO.setAvailableProducts(plaidItem.getAvailableProducts());
        plaidItemDAO.setAccounts(plaidItem.getAccounts());
        plaidItemDAO.setDateCreated(plaidItem.getDateCreated());
        plaidItemDAO.setMetaData(plaidItem.getMetaData());

        return plaidItemDAO;
    }
}
