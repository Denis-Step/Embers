package lambda.processors.transactions;

import com.amazonaws.services.dynamodbv2.xspec.S;
import dynamo.TransactionDAO;
import dynamo.setup.TransactionsTableSetup;
import events.impl.TransactionsEbClient;
import external.plaid.entities.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReceiveTransactionsProcessorTest {

    private final TransactionDAO transactionDAO;

    @Mock
    private final TransactionsEbClient transactionsEbClient;

    private final ReceiveTransactionsProcessor receiveTransactionsProcessor;

    private static final String USER = "USER";
    private static final String INSTITUTION = "INSTITUTION";

    private static final Double AMOUNT = 49.99;
    private static final String DESCRIPTION = "SAMPLE_TRANSACTION";
    private static final String ORIGINAL_DESCRIPTION = "SAMPLE_DESCRIPTION";
    private static final String MERCHANT_NAME = "SAMPLE_MERCHANT";
    private static final String DATE = "2020-01-01";
    private static final String ACCOUNT_ID = "1233456789";
    private static final String TRANSACTION_ID = "TX-123456789";

    public ReceiveTransactionsProcessorTest() {
        transactionDAO = new TransactionDAO();
        transactionsEbClient = Mockito.mock(TransactionsEbClient.class);
        receiveTransactionsProcessor = new ReceiveTransactionsProcessor(transactionDAO, transactionsEbClient);

        TransactionsTableSetup.setUpTransactionsTable();
    }

    @AfterAll
    public void deleteTransactionsTable() {
        TransactionsTableSetup.deleteTransactionsTable();
    }

    @Test
    public void test_saveAndReturnNewTransactions() {
        List<Transaction> newTransactions = TransactionsTableSetup.createNewTransactions();
        //newTransactions.forEach(tx -> transactionDAO.save(tx));
        cleanup_Transactions(newTransactions);

        List<Transaction> receivedTransactions = this.receiveTransactionsProcessor
                .saveAndReturnNewTransactions(newTransactions);

        assert (newTransactions.equals(receivedTransactions));

        List<Transaction> unprocessedTransactions = this.receiveTransactionsProcessor.saveAndReturnNewTransactions(
                newTransactions
        );

        assert (unprocessedTransactions.size() == 0);

        cleanup_Transactions(newTransactions);

    }

    private void cleanup_Transactions(List<Transaction> transactions) {
        transactions.forEach(tx -> transactionDAO.delete(tx));
    }

}