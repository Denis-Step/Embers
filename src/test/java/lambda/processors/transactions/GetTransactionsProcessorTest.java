package lambda.processors.transactions;

import dynamo.TransactionDAO;
import external.plaid.entities.ImmutableTransaction;
import external.plaid.entities.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GetTransactionsProcessorTest {
    private final TransactionDAO transactionDAO;
    private final GetTransactionsProcessor getTransactionsProcessor;


    private static final String USER = "USER";
    private static final String INSTITUTION = "INSTITUTION";
    private static final String ACCESS_TOKEN = "1234456STOKEN";

    private static final Double AMOUNT = 49.99;
    private static final String DESCRIPTION = "SAMPLE_TRANSACTION";
    private static final String ORIGINAL_DESCRIPTION = "SAMPLE_DESCRIPTION";
    private static final String MERCHANT_NAME = "SAMPLE_MERCHANT";
    private static final String DATE = "2020-02-01";
    private static final String ACCOUNT_ID = "1233456789";
    private static final String TRANSACTION_ID = "TX-123456789";

    public GetTransactionsProcessorTest(GetTransactionsProcessor getTransactionsProcessor) {
        this.transactionDAO = new TransactionDAO();
        this.getTransactionsProcessor = new GetTransactionsProcessor(transactionDAO);
    }

    @Test
    public void test_getTransactionsWithoutStartDate() {
        List<Transaction> sampleTransactions = createAndSaveNewTransactions();
        List<Transaction> queriedTransactions = getTransactionsProcessor.getTransactions(USER);
        assert (sampleTransactions.equals(queriedTransactions));
        cleanup_transactions(sampleTransactions);
    }

    @Test
    public void test_getTransactionsWithStartDate() {
        List<Transaction> sampleTransactions = createAndSaveNewTransactions();
        List<Transaction> queriedTransactions = getTransactionsProcessor.getTransactions(USER,
                DATE.replaceFirst("01", "24"));
        assert (sampleTransactions.size() == 2);
        cleanup_transactions(sampleTransactions);
    }

    private void cleanup_transactions(List<Transaction> transactions) {
        transactions.forEach(tx -> transactionDAO.delete(tx));
    }

    private List<Transaction> createAndSaveNewTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 25; i++) {

            Transaction transaction = ImmutableTransaction.builder()
                    .transactionId(TRANSACTION_ID + String.valueOf(i))
                    .institutionName(INSTITUTION)
                    .amount(AMOUNT)
                    .date(DATE)
                    .accountId(ACCOUNT_ID)
                    .description(DESCRIPTION)
                    .merchantName(MERCHANT_NAME)
                    .accountId(ACCOUNT_ID)
                    .user(USER)
                    .originalDescription(ORIGINAL_DESCRIPTION)
                    .build();

            transactionDAO.save(transaction);
        }
        return transactions;
    }
}
