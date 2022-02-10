package dynamo;

import dynamo.setup.PlaidItemsTableUtils;
import external.plaid.entities.PlaidItem;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import dynamo.setup.LocalDynamoDbClient;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NewPlaidItemDAOTest {

    private DynamoDbTable<PlaidItem> itemsTable;
    private PlaidItemsTableUtils plaidItemsTableUtils;
    private NewPlaidItemDAO newPlaidItemDAO;

    public NewPlaidItemDAOTest() {
        DynamoDbEnhancedClient enhancedDynamoClient = LocalDynamoDbClient.getEnhancedDynamoClient();
        itemsTable = enhancedDynamoClient.table("PlaidItems",
                DynamoTableSchemas.PLAID_ITEM_SCHEMA);

        plaidItemsTableUtils = new PlaidItemsTableUtils(LocalDynamoDbClient.getDynamoClient());
        newPlaidItemDAO = new NewPlaidItemDAO(itemsTable);
    }

    @BeforeAll
    public void deleteAndRecreateTable() {
        plaidItemsTableUtils.setupPlaidItemsTable();
    }

    @Test
    public void test_queryWithPartitionAndSortKey() {
        PlaidItem item = plaidItemsTableUtils.createItem();
        // Don't rely on DAO save method.
        itemsTable.putItem(item);

        List<PlaidItem> pLaidItemList = newPlaidItemDAO.query(item.getUser(), item.getInstitutionId());
        assert (pLaidItemList.size() == 1);
        assert (pLaidItemList.get(0).equals(item));

        itemsTable.deleteItem(item);
    }

    @Test
    public void test_queryWithPartitionKeyOnly() {
        List<PlaidItem> sampleItems = plaidItemsTableUtils.createItems();
        // Don't rely on DAO save method.
        sampleItems.forEach(item -> itemsTable.putItem(item));

        List<PlaidItem> plaidItemList = newPlaidItemDAO.query(sampleItems.get(0).getUser());
        assert (plaidItemList.size() == sampleItems.size());
    }

    @Test
    public void test_save() {
        PlaidItem item = plaidItemsTableUtils.createItem();
        newPlaidItemDAO.save(item);

        // Don't rely on DAO query method. BRITTLE.
        List<PlaidItem> plaidItemsList = itemsTable.query(r -> r.queryConditional(QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(item.getUser())
                        .sortValue(item.getInstitutionId() + "#" + item.getAccessToken())
                        .build()))).items().stream().collect(Collectors.toList());

        assert (plaidItemsList.get(0).equals(item));

        itemsTable.deleteItem(item);
    }

    @Test
    public void test_saveQueryAndDelete() {
        PlaidItem item = plaidItemsTableUtils.createItem();
        newPlaidItemDAO.save(item);

        List<PlaidItem> plaidItemsList = newPlaidItemDAO.query(item.getUser(),
                item.getInstitutionId() + "#" + item.getAccessToken());
        assert (plaidItemsList.get(0).equals(item));

        newPlaidItemDAO.delete(item);
        plaidItemsList = newPlaidItemDAO.query(item.getUser(),
                item.getInstitutionId() + "#" + item.getAccessToken());
        assert (plaidItemsList.size() == 0);
    }

    @Test
    public void test_GetSuccessfully() {
        PlaidItem item = plaidItemsTableUtils.createItem();
        itemsTable.putItem(item);

        try {
            Optional<PlaidItem> plaidItemOptional = newPlaidItemDAO.get(item.getUser(),
                    item.getInstitutionId() + "#" + item.getAccessToken());
            assert plaidItemOptional.get().equals(item);
            itemsTable.deleteItem(item);
        } catch (NewPlaidItemDAO.MultipleItemsFoundException e) {
            assert false;
        }
    }

    @Test
    public void test_GetWithNoneFound() throws NewPlaidItemDAO.MultipleItemsFoundException {
        Optional<PlaidItem> plaidItemOptional = newPlaidItemDAO.get("User", "InstId");
        assert !plaidItemOptional.isPresent();
    }

    @Test
    public void test_GetWithMultipleFound() {
        List<PlaidItem> sampleItems = plaidItemsTableUtils.createItems();
        sampleItems.forEach(item -> itemsTable.putItem(item));

        NewPlaidItemDAO.MultipleItemsFoundException thrownException = Assertions.assertThrows(
                NewPlaidItemDAO.MultipleItemsFoundException.class, () -> {
                    Optional<PlaidItem> plaidItemOptional = newPlaidItemDAO.get(sampleItems.get(0).getUser(),
                            sampleItems.get(0).getInstitutionId());;});

        assert thrownException.getItems().size() == sampleItems.size();

        // Put list of PlaidItems into Set to remove ordering considerations.
        Set<PlaidItem> originalItems = new HashSet<>();
        originalItems.addAll(sampleItems);
        Set<PlaidItem> queriedItems = new HashSet<>();
        queriedItems.addAll(thrownException.getItems());
        assert originalItems.equals(queriedItems);

        sampleItems.forEach(item -> itemsTable.deleteItem(item));
    }

}