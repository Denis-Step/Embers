package lambda.handlers.transactions;

import com.amazonaws.services.lambda.runtime.Context;
import dynamo.NewTransactionDAO;
import dynamo.setup.TransactionsTableUtils;
import events.impl.TransactionsEbClient;
import external.plaid.entities.Transaction;
import lambda.handlers.ReceiveTransactionsHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReceiveTransactionsHandlerTest {

    private final ReceiveTransactionsHandler handler;

    private final NewTransactionDAO transactionDAO;
    private final TransactionsEbClient transactionsEbClient;

    public ReceiveTransactionsHandlerTest() {
        this.transactionDAO = mock(NewTransactionDAO.class);
        this.transactionsEbClient = mock(TransactionsEbClient.class);
        this.handler = new ReceiveTransactionsHandler(transactionDAO, transactionsEbClient);
    }

    @Test
    public void savesAllTransactionsAndPublishesOnlyNewTransactions() {
        List<Transaction> transactionList = TransactionsTableUtils.createTransactions(10);
        List<Transaction> newTransactions = transactionList.subList(0, 5);
        when(transactionDAO.saveWithResponse(argThat(newTransactions::contains))).thenReturn(false);
        when(transactionDAO.saveWithResponse(argThat(tx -> !newTransactions.contains(tx)))).thenReturn(true);
        this.handler.handleRequest(transactionList, mock(Context.class));

        ArgumentCaptor<Transaction> savedTransactions = ArgumentCaptor.forClass(Transaction.class);
        ArgumentCaptor<List<Transaction>> publishedTransactions = ArgumentCaptor.forClass(List.class);
        verify(transactionDAO, times(transactionList.size())).saveWithResponse(savedTransactions.capture());
        verify(transactionsEbClient).publishNewTransactions(publishedTransactions.capture(), anyString());

        assertEquals(transactionList, savedTransactions.getAllValues());
        assertEquals(newTransactions, publishedTransactions.getValue());

    }
}
