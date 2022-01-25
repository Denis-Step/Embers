package dynamo;

import dynamo.setup.PlaidItemsTableUtils;
import external.plaid.entities.PlaidItem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import testcontainers.LocalDynamoDbClient;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlaidItemDAOTests {

    private PlaidItemsTableUtils plaidItemsTableUtils;
    private PlaidItemDAO plaidItemDAO;

    @BeforeAll
    public void setUpPlaidItemsTable() {
        PlaidItemsTableUtils plaidItemsTableUtils = new PlaidItemsTableUtils(
                LocalDynamoDbClient.getDynamoClient());
        plaidItemsTableUtils.setupPlaidItemsTable();
    }

    @AfterAll
    public void deletePlaidItemsTable() {
         plaidItemsTableUtils.deletePlaidItemsTable();
    }

    public PlaidItemDAOTests() {
        plaidItemDAO = new PlaidItemDAO();
    }

    @Test
    public void test_toAndFromItem() {
        PlaidItem item = plaidItemsTableUtils.createItem();
        PlaidItemDAO itemDao = new PlaidItemDAO(item);

        assert (itemDao.createItem().equals(item));

        plaidItemDAO.delete(item);
    }

    @Test
    public void test_saveAndReturnItem() {
        PlaidItem item = plaidItemsTableUtils.createItem();
        plaidItemDAO.save(item);

        List<PlaidItem> queriedItems = plaidItemDAO.query(item.getUser(), item.getInstitutionId());

        assert (queriedItems.size() == 1);
        assert (queriedItems.get(0).equals(item));

        plaidItemDAO.delete(item);
    }

    @Test
    public void test_saveAndQueryByUser() {
        List<PlaidItem> items = plaidItemsTableUtils.createItems();
        items.forEach(i -> plaidItemDAO.save(i));
        List<PlaidItem> queriedItems = plaidItemDAO.query(items.get(0).getUser());

        assert(queriedItems.equals(items));
    }
}
