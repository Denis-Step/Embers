package lambda.processors.transactions;

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
public class PollTransactionsProcessorTest {
    private final TransactionsGrabber transactionsGrabber;
    private final PollTransactionsProcessor pollTransactionsProcessor;


    public PollTransactionsProcessorTest() {
        this.transactionsGrabber = mock(TransactionsGrabber.class);
        this.pollTransactionsProcessor = new PollTransactionsProcessor(transactionsGrabber);
    }

    @Test
    public void pollsTransactions() {
        PlaidItem plaidItem = PlaidItemsTableUtils.createItem();
        List<Transaction> expectedTransactions = TransactionsTableUtils.createTransactions(2);
        Date startDate = Date.from(Instant.now());
        Date endDate = Date.from(Instant.now());
        when(transactionsGrabber.requestTransactions(plaidItem.getUser(), plaidItem.getInstitutionId(),
                plaidItem.getAccessToken(), startDate, endDate)).thenReturn(expectedTransactions);

        List<Transaction> transactions = pollTransactionsProcessor.pollForTransactions(plaidItem, startDate, endDate);
        verify(transactionsGrabber.requestTransactions(plaidItem.getUser(), plaidItem.getInstitutionId(),
                plaidItem.getAccessToken(), startDate, endDate), times(1));

        assertEquals(expectedTransactions, transactions);
    }

}
