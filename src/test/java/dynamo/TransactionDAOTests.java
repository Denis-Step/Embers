package dynamo;

import external.plaid.entities.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import dynamo.setup.DynamoDbClientSetup;
import dynamo.setup.TransactionsTableSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TransactionDAOTests {

    private static final String TRANSACTIONS_TABLE_NAME = "Transactions";
    private static final String USER = "USER";
    private static final String INSTITUTION = "INSTITUTION";

    private static final Double AMOUNT = 49.99;
    private static final String DESCRIPTION = "SAMPLE_TRANSACTION";
    private static final String ORIGINAL_DESCRIPTION = "SAMPLE_DESCRIPTION";
    private static final String MERCHANT_NAME = "SAMPLE_MERCHANT";
    private static final String DATE = "2020-01-01";
    private static final String ACCOUNT_ID = "1233456789";
    private static final String TRANSACTION_ID = "TX-123456789";

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDAOTests.class);
    private static final TransactionDAO transactionDao = new TransactionDAO();

    public TransactionDAOTests() {
    }

    @Test
    public void test_LoadQueryTransaction() {
        Transaction transaction = createTransaction();
        transactionDao.save(transaction);

        Transaction loadedTransaction = transactionDao.load(transaction);
        assert (loadedTransaction.equals(transaction));

        List<Transaction> queriedTransactions = transactionDao.query(transaction.getUser());
        assert (queriedTransactions.get(0).equals(transaction));

        queriedTransactions = transactionDao.query(transaction.getUser(), transaction.getDate());
        assert (queriedTransactions.get(0).equals(transaction));

        queriedTransactions = transactionDao.query(transaction.getUser(), transaction.getDate(),
                transaction.getAmount());
        assert (queriedTransactions.get(0).equals(transaction));

        queriedTransactions = transactionDao.query(transaction.getUser(), transaction.getDate(),
                transaction.getAmount(), transaction.getTransactionId());
        assert (queriedTransactions.get(0).equals(transaction));

        transactionDao.delete(transaction);
    }

    @Test
    public void test_deleteTransaction() {
        Transaction transaction = createTransaction();
        transactionDao.save(transaction);

        transactionDao.delete(transaction);

        List<Transaction> queriedTransaction = transactionDao.query(transaction.getUser());
        assert (queriedTransaction.size() == 0);

        transactionDao.save(transaction);
        transactionDao.delete(transaction.getUser(), transaction.getDate() +
                "#" +
                transaction.getAmount() +
                "#" +
                transaction.getTransactionId());
        assert (queriedTransaction.size() == 0);
    }

    @Test
    public void test_queryLsi() {
        Transaction transaction = createTransaction();
        transactionDao.save(transaction);

        List<Transaction> queriedTransaction = transactionDao.queryByInstitution(
                transaction.getUser(),
                transaction.getInstitutionName());
        assert (queriedTransaction.get(0).equals(transaction));

        transactionDao.delete(transaction);

    }

    //@BeforeAll
    public static void createTransactionsTable() {
        TransactionsTableSetup.createTable(DynamoDbClientSetup.getDefaultDdbClient());
    }

    private Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(TRANSACTION_ID);
        transaction.setInstitutionName(INSTITUTION);
        transaction.setAmount(AMOUNT);
        transaction.setDate(DATE);
        transaction.setAccountId(ACCOUNT_ID);
        transaction.setDescription(DESCRIPTION);
        transaction.setMerchantName(MERCHANT_NAME);
        transaction.setAccountId(ACCOUNT_ID);
        transaction.setUser(USER);
        transaction.setOriginalDescription(ORIGINAL_DESCRIPTION);
        return transaction;
    }

}
