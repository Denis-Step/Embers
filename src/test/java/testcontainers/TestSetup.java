package testcontainers;

import dynamo.DynamoTableSchemas;
import dynamo.NewPlaidItemDAO;
import dynamo.setup.PlaidItemsTableSetup;
import external.plaid.entities.PlaidItem;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;


@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSetup {

    private DynamoDbEnhancedClient enhancedDynamoClient;
    private PlaidItemsTableSetup plaidItemsTableSetup;

    @BeforeAll
    public void test_client() {
        enhancedDynamoClient = LocalDynamoDbClient.getEnhancedDynamoClient();
        PlaidItemsTableSetup plaidItemsTableSetup = new PlaidItemsTableSetup(LocalDynamoDbClient.getDynamoClient());
        plaidItemsTableSetup.setupPlaidItemsTable();

        DynamoDbTable<PlaidItem> itemsTable = enhancedDynamoClient.table("PlaidItems",
                DynamoTableSchemas.PLAID_ITEM_SCHEMA);

        PlaidItem item = plaidItemsTableSetup.createItem();
        itemsTable.putItem(item);

        NewPlaidItemDAO newPlaidItemDAO = new NewPlaidItemDAO(itemsTable);
        List<PlaidItem> pLaidItemList = newPlaidItemDAO.query(item.getUser(), item.getInstitutionId());
        assert (pLaidItemList.size() == 1);
        System.out.println(pLaidItemList.get(0));
        System.out.println(item);
        assert (pLaidItemList.get(0).equals(item));
    }

    @Test
    public void test_sample() {

    }

}