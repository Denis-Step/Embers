package dynamo;

import dynamo.setup.TransactionsTableUtils;
import dynamo.setup.client.LocalDynamoDbClient;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        // Cleanup.
        transactionsTable.deleteItem(transaction);
    }

    @Test
    public void queryByUserAndDate() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        // Don't rely on DAO save method.
        transactionsTable.putItem(transaction);

        List<Transaction> transactionList = transactionDAO.query(transaction.getUser(), transaction.getDate());
        assertEquals(1,transactionList.size());
        assertEquals(transaction, transactionList.get(0));

        // Cleanup.
        transactionsTable.deleteItem(transaction);
    }

    @Test
    public void saveTransaction() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        transactionDAO.save(transaction);

        // Don't rely on DAO query method. BRITTLE.
        List<Transaction> transactionList = transactionsTable.query(r -> r.queryConditional(QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(transaction.getUser())
                        .sortValue(transaction.getDate() + "#" + transaction.getTransactionId())
                        .build()))).items().stream().collect(Collectors.toList());
        assertEquals(transaction, transactionList.get(0));

        //Cleanup.
        transactionsTable.deleteItem(transaction);
    }

    @Test
    public void saveQueryAndDelete() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        transactionDAO.save(transaction);

        List<Transaction> transactionList = transactionDAO.query(transaction.getUser(), transaction.getDate());
        assertEquals(transaction, transactionList.get(0));

        transactionDAO.delete(transaction);
        transactionList = transactionDAO.query(transaction.getUser(), transaction.getTransactionId());
        assertEquals(0, transactionList.size());
    }

    @Test
    public void getTransaction() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        transactionsTable.putItem(transaction);

        try {
            Optional<Transaction> transactionOptional = transactionDAO.get(transaction.getUser(),
                    transaction.getDate() + "#" + transaction.getTransactionId());
            assert transactionOptional.get().equals(transaction);
            transactionsTable.deleteItem(transaction);
        } catch (NewTransactionDAO.MultipleItemsFoundException e) {
            assert false;
        }
    }

    @Test
    public void getWithNoneFound() throws NewTransactionDAO.MultipleItemsFoundException {
        Optional<Transaction> transactionOptional = transactionDAO.get("User", "InstId");
        assert !transactionOptional.isPresent();
    }

    @Test
    public void getWithMultipleFound() {
        List<Transaction> sampleTransactions = transactionsTableUtils.createTransactions(25);
        sampleTransactions.forEach(tx -> transactionsTable.putItem(tx));

        NewTransactionDAO.MultipleItemsFoundException thrownException = Assertions.assertThrows(
                NewTransactionDAO.MultipleItemsFoundException.class, () -> {
                    Optional<Transaction> transactionOptional = transactionDAO.get(sampleTransactions.get(0).getUser(),
                            sampleTransactions.get(0).getDate());
                });

        assert thrownException.getItems().size() == sampleTransactions.size();

        // Put list of Transactions into Set to remove ordering considerations.
        Set<Transaction> originalTransactions = new HashSet<>();
        originalTransactions.addAll(sampleTransactions);
        Set<Transaction> queriedTransactions = new HashSet<>();
        queriedTransactions.addAll(thrownException.getItems());
        assert originalTransactions.equals(queriedTransactions);

        sampleTransactions.forEach(tx -> transactionsTable.deleteItem(tx));
    }
}
