package lambda.processors.transactions;


import dynamo.TransactionDAO;
import external.plaid.entities.Transaction;
import lambda.processors.items.ItemProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import dynamo.setup.DynamoDbClientSetup;
import dynamo.setup.TransactionsTableSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class LoadTransactionsProcessorTest {

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

    @Mock
    private final ItemProcessor itemProcessor;

    private final TransactionDAO transactionDAO;

    private final LoadTransactionsProcessor loadTransactionsProcessor;


    public LoadTransactionsProcessorTest() {
        itemProcessor = mock(ItemProcessor.class);
        transactionDAO = new TransactionDAO();
        loadTransactionsProcessor = new LoadTransactionsProcessor(
                itemProcessor
        );
    }

    /**
     * @return Set up by creating 25 transactions with diff ID's.
     */
    private List<Transaction> createTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 25; i++) {

            Transaction transaction = new Transaction();
            transaction.setTransactionId(TRANSACTION_ID + String.valueOf(i));
            transaction.setInstitutionName(INSTITUTION);
            transaction.setAmount(AMOUNT);
            transaction.setDate(DATE);
            transaction.setAccountId(ACCOUNT_ID);
            transaction.setDescription(DESCRIPTION);
            transaction.setMerchantName(MERCHANT_NAME);
            transaction.setAccountId(ACCOUNT_ID);
            transaction.setUser(USER);
            transaction.setOriginalDescription(ORIGINAL_DESCRIPTION);

            transactionDAO.save(transaction);
        }
    }
}
