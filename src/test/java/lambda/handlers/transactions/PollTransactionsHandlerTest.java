package lambda.handlers.transactions;

import com.amazonaws.services.lambda.runtime.Context;
import dynamo.setup.PlaidItemsTableUtils;
import dynamo.setup.TransactionsTableUtils;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
import lambda.handlers.PollTransactionsHandler;
import lambda.processors.transactions.PollTransactionsProcessor;
import lambda.requests.transactions.ImmutablePollTransactionsRequest;
import lambda.requests.transactions.PollTransactionsRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PollTransactionsHandlerTest {

    private final PollTransactionsProcessor processor;
    private final PollTransactionsHandler handler;

    private static final String ACCOUNT_ID = "ACCOUNT";
    private static final String START_DATE = Instant.now().toString();

    public PollTransactionsHandlerTest() {
        this.processor = mock(PollTransactionsProcessor.class);
        this.handler = new PollTransactionsHandler(processor);
    }

    @Test
    public void callsTransactionProcessor() throws ParseException {
        PlaidItem item = PlaidItemsTableUtils.createItem();
        List<Transaction> sampleTransactions = TransactionsTableUtils.createTransactions(5);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        when(processor.pollForTransactions(item, dateFormatter.parse(START_DATE), dateFormatter.parse(START_DATE)))
                .thenReturn(sampleTransactions);

        List<Transaction> transactions = this.handler.handleRequest(createPollTransactionsRequest(item),
                mock(Context.class));
        assertEquals(sampleTransactions, transactions);
    }

    private PollTransactionsRequest createPollTransactionsRequest(PlaidItem item) {
        return ImmutablePollTransactionsRequest.builder()
                .plaidItem(item)
                .accountId(ACCOUNT_ID)
                .startDate(START_DATE)
                .endDate(START_DATE)
                .build();
    }

}
