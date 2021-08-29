package lambda.processors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.DaggerAwsComponent;
import dagger.DaggerPlaidComponent;
import dynamo.PlaidItemDAO;
import lambda.requests.CreateItemRequest;
import plaid.clients.ItemRequester;
import plaid.entities.PlaidItem;
import plaid.responses.PublicTokenExchangeResponse;

import java.io.IOException;

public class CreateItemProcessor {

    private final ItemRequester itemRequester;
    private final PlaidItemDAO plaidItemDAO;

    public CreateItemProcessor() {
        this.itemRequester = DaggerPlaidComponent.create().buildItemRequestor();
        this.plaidItemDAO = new PlaidItemDAO();
    }

    // Calls Plaid client to request a new Item and uses info from incoming request
    // to build PlaidItem.
    public PlaidItem createPlaidItem (CreateItemRequest createItemRequest) throws IOException {
        PublicTokenExchangeResponse itemInfo = itemRequester.requestItem(createItemRequest.getPublicToken());

        PlaidItem item =  PlaidItem.getBuilder()
                .setID(itemInfo.getID())
                .setAccessToken(itemInfo.getAccessToken())
                .setUser(createItemRequest.getUser())
                .setDateCreated(createItemRequest.getDateCreated())
                .setAvailableProducts(createItemRequest.getAvailableProducts())
                .setAccounts(createItemRequest.getAccounts())
                .setInstitutionId(createItemRequest.getInstitutionId())
                .setMetaData(createItemRequest.getMetaData())
                .build();

        plaidItemDAO.save(item);
        return item;
    }
}
