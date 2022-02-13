package dynamo;

import dynamo.setup.TransactionsTableUtils;
import dynamo.setup.client.LocalDynamoDbClient;
import external.plaid.entities.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NewTransactionDAOTest {

    private DynamoDbTable<Transaction> transactionsTable;
    private TransactionsTableUtils transactionsTableUtils;
    private NewTransactionDAO transactionDAO;

    public NewTransactionDAOTest() {
        DynamoDbEnhancedClient enhancedDynamoClient = LocalDynamoDbClient.getEnhancedDynamoClient();
        transactionsTable = enhancedDynamoClient.table("Transactions",
                DynamoTableSchemas.TRANSACTION_SCHEMA);

        transactionsTableUtils = new TransactionsTableUtils(LocalDynamoDbClient.getDynamoClient());
        transactionDAO = new NewTransactionDAO(transactionsTable);
    }

    @BeforeAll
    public void deleteAndRecreateTable() { transactionsTableUtils.setUpTransactionsTable(); }

    @Test
    public void queryByUser() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        // Don't rely on DAO save method.
        transactionsTable.putItem(transaction);

        List<Transaction> transactionList = transactionDAO.query(transaction.getUser());
        assertEquals(1,transactionList.size());
        assertEquals(transaction, transactionList.get(0));
    }
}
