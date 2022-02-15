package dynamo;

import dynamo.setup.TransactionsTableUtils;
import dynamo.setup.client.LocalDynamoDbClient;
import external.plaid.entities.ImmutableTransaction;
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

import java.util.*;
import java.util.stream.Collectors;
import static org.junit.Assert.*;

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
    public void queryByUserAndAmount() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        // Don't rely on DAO save method.
        transactionsTable.putItem(transaction);

        List<Transaction> transactions = transactionDAO.queryByAmount(transaction.getUser(),
                transaction.getAmount() - 1.00);
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.get(0));

        // Cleanup
        transactionsTable.deleteItem(transaction);
    }

    @Test
    public void queryByUserAndDescription() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        // Don't rely on DAO save method.
        transactionsTable.putItem(transaction);

        List<Transaction> transactions = transactionDAO.queryByDescription(transaction.getUser(),
                transaction.getDescription().substring(0,3));
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.get(0));

        // Cleanup
        transactionsTable.deleteItem(transaction);
    }

    @Test
    public void queryByUserAndInstitution() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        // Don't rely on DAO save method.
        transactionsTable.putItem(transaction);

        List<Transaction> transactions = transactionDAO.queryByInstitutionName(transaction.getUser(),
                transaction.getInstitutionName());
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.get(0));

        // Cleanup
        transactionsTable.deleteItem(transaction);
    }

    @Test
    public void queryByAccount() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        // Don't rely on DAO save method.
        transactionsTable.putItem(transaction);

        List<Transaction> transactions = transactionDAO.queryByAccountId(transaction.getAccountId());
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.get(0));

        // Cleanup
        transactionsTable.deleteItem(transaction);
    }

    @Test
    public void queryByAccountAndDate() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        // Don't rely on DAO save method.
        transactionsTable.putItem(transaction);

        List<Transaction> transactions = transactionDAO.queryByAccountId(transaction.getAccountId(),
                transaction.getDate());
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.get(0));

        // Cleanup
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
    public void getByUserSuccessfully() {
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
    public void getByUserWithNoneFound() throws NewTransactionDAO.MultipleItemsFoundException {
        Optional<Transaction> transactionOptional = transactionDAO.get("User", "InstId");
        assert !transactionOptional.isPresent();
    }

    @Test
    public void getByUserWithMultipleFound() {
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

    @Test
    public void getByTransactionIdSuccessfully() {
        Transaction transaction = transactionsTableUtils.createTransaction();
        transactionsTable.putItem(transaction);

        try {
            Optional<Transaction> transactionOptional = transactionDAO.getByTransactionId(transaction.getTransactionId());
            assertEquals(transaction, transactionOptional.get());
            transactionsTable.deleteItem(transaction);
        } catch (NewTransactionDAO.MultipleItemsFoundException e) {
            assert false;
        }
    }

    @Test
    public void getByTransactionIdWithNoneFound() throws NewTransactionDAO.MultipleItemsFoundException {
        Optional<Transaction> transactionOptional = transactionDAO.getByTransactionId("NULL");
        assertTrue(!transactionOptional.isPresent());
    }

    @Test
    public void getByTransactionIdWithMultipleFound() {
        List<Transaction> sampleTransactions = transactionsTableUtils.createTransactions(25);

        // All transactions need same TX ID but cannot be identical otherwise.
        List<Transaction> modifiedTransactions = sampleTransactions.stream()
                .map(tx -> ImmutableTransaction.builder()
                        .from(tx)
                        .transactionId(sampleTransactions.get(0).getTransactionId())
                        .user(tx.getUser() + tx.getTransactionId())
                        .build())
                .collect(Collectors.toList());
        modifiedTransactions.forEach(tx -> transactionsTable.putItem(tx));

        NewTransactionDAO.MultipleItemsFoundException thrownException = Assertions.assertThrows(
                NewTransactionDAO.MultipleItemsFoundException.class, () -> {
                    Optional<Transaction> transactionOptional = transactionDAO.getByTransactionId(
                            modifiedTransactions.get(0).getTransactionId()
                    );
                });

        assertEquals(modifiedTransactions.size(), thrownException.getItems().size());

        // Put list of Transactions into Set to remove ordering considerations because DDB query does not preserve order
        // Of items added.
        Set<Transaction> originalTransactions = new HashSet<>();
        originalTransactions.addAll(modifiedTransactions);
        Set<Transaction> queriedTransactions = new HashSet<>();
        queriedTransactions.addAll(thrownException.getItems());
        assertEquals(originalTransactions, queriedTransactions);

        modifiedTransactions.forEach(tx -> transactionsTable.deleteItem(tx));
    }
}
