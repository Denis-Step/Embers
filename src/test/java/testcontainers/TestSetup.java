package testcontainers;

import dynamo.DynamoTableSchema;
import dynamo.setup.PlaidItemsTableSetup;
import external.plaid.entities.PlaidItem;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

//@Testcontainers
@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSetup {

    //@Container
    //private static final LocalDynamoDbContainer localDynamoDbContainer = LocalDynamoDbContainer.getInstance();

    private DynamoDbEnhancedClient enhancedDynamoClient;
    private PlaidItemsTableSetup plaidItemsTableSetup;

    @BeforeAll
    public void test_client() {
        enhancedDynamoClient = LocalDynamoDbClient.getEnhancedDynamoClient();
        PlaidItemsTableSetup plaidItemsTableSetup = new PlaidItemsTableSetup(LocalDynamoDbClient.getDynamoClient());
        plaidItemsTableSetup.setupPlaidItemsTable();

        DynamoDbTable<PlaidItem> itemsTable = (DynamoDbTable<PlaidItem>) enhancedDynamoClient.table("PlaidItems",
                DynamoTableSchema.PLAID_ITEM_SCHEMA.schema);

        itemsTable.putItem(plaidItemsTableSetup.createItem());
    }

    @Test
    public void test_sample() {

    }

}