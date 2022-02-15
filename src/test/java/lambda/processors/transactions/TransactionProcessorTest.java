package lambda.processors.transactions;

import dynamo.NewPlaidItemDAO;
import dynamo.NewTransactionDAO;
import dynamo.setup.PlaidItemsTableUtils;
import dynamo.setup.TransactionsTableUtils;
import external.plaid.clients.TransactionsGrabber;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionProcessorTest {
    private final NewTransactionDAO transactionDAO;
    private final TransactionsGrabber transactionsGrabber;
    private final TransactionProcessor transactionProcessor;


    public TransactionProcessorTest() {
        this.transactionDAO = mock(NewTransactionDAO.class);
        this.transactionsGrabber = mock(TransactionsGrabber.class);
        this.transactionProcessor = new TransactionProcessor(transactionDAO, transactionsGrabber);
    }

    @Test
    public void pullsTransactions() {
        PlaidItem plaidItem = PlaidItemsTableUtils.createItem();
        List<Transaction> expectedTransactions = TransactionsTableUtils.createTransactions(2);
        Date startDate = Date.from(Instant.now());
        Date endDate = Date.from(Instant.now());
        when(transactionsGrabber.requestTransactions(plaidItem.getUser(), plaidItem.getInstitutionId(),
                plaidItem.getAccessToken(), startDate, endDate)).thenReturn(expectedTransactions);

        List<Transaction> transactions = transactionProcessor.pullNewTransactions(plaidItem, startDate, endDate);
        verify(transactionsGrabber.requestTransactions(plaidItem.getUser(), plaidItem.getInstitutionId(),
                plaidItem.getAccessToken(), startDate, endDate), times(1));

        assertEquals(expectedTransactions, transactions);
    }

}
