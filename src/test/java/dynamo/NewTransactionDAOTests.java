package dynamo;

import dagger.DaggerAwsComponent;
import dynamo.NewTransactionDAO;
import external.plaid.entities.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import dynamo.setup.DynamoDbClientSetup;
import dynamo.setup.TransactionsTableSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class NewTransactionDAOTests {

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

    private static final Logger LOGGER = LoggerFactory.getLogger(NewTransactionDAOTests.class);

    private final DynamoDbEnhancedClient ddbClient;
    private final DynamoDbTable<NewTransactionDAO> txTable;
    private final NewTransactionDAO transactionDAO;

    public NewTransactionDAOTests() {

        this.ddbClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDbClientSetup.getDefaultDdbClient())
                .build();

        this.txTable = ddbClient.table(TRANSACTIONS_TABLE_NAME,
                TableSchema.fromBean(NewTransactionDAO.class));

        this.transactionDAO = new NewTransactionDAO(ddbClient, txTable);
        //createTransactionsTable();
    }

    @Test
    public void test_saveAndLoadTransaction() {
        Transaction transaction = createTransaction();
        NewTransactionDAO newTransactionDAO = NewTransactionDAO.fromTransaction(transaction);
        LOGGER.info(newTransactionDAO.getUser());
        newTransactionDAO.save();

        Transaction loadedTransaction = NewTransactionDAO.load(transaction);
        LOGGER.info(loadedTransaction.toString());
        assert (loadedTransaction.equals(transaction));
    }

    private void createTransactionsTable() {
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
