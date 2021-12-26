package dynamo;

import dynamo.setup.PlaidItemsTableSetup;
import external.plaid.entities.PlaidItem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlaidItemDAOTests {

    private PlaidItemDAO plaidItemDAO;

    @BeforeAll
    public static void setUpPlaidItemsTable() {
        PlaidItemsTableSetup.setupPlaidItemsTable();
    }

    @AfterAll
    public static void deletePlaidItemsTable() {
        PlaidItemsTableSetup.deletePlaidItemsTable();
    }

    public PlaidItemDAOTests() {
        plaidItemDAO = new PlaidItemDAO();
    }

    @Test
    public void test_toAndFromItem() {
        PlaidItem item = PlaidItemsTableSetup.createItem();
        PlaidItemDAO itemDao = new PlaidItemDAO(item);

        assert (itemDao.createItem().equals(item));

        plaidItemDAO.delete(item);
    }

    @Test
    public void test_saveAndReturnItem() {
        PlaidItem item = PlaidItemsTableSetup.createItem();
        plaidItemDAO.save(item);

        List<PlaidItem> queriedItems = plaidItemDAO.query(item.getUser(), item.getInstitutionId());

        assert (queriedItems.size() == 1);
        assert (queriedItems.get(0).equals(item));

        plaidItemDAO.delete(item);
    }

    @Test
    public void test_saveAndQueryByUser() {
        List<PlaidItem> items = PlaidItemsTableSetup.createItems();
        items.forEach(i -> plaidItemDAO.save(i));
        List<PlaidItem> queriedItems = plaidItemDAO.query(items.get(0).getUser());

        assert(queriedItems.equals(items));
    }
}
