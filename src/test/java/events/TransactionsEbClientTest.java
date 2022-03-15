package events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dynamo.setup.TransactionsTableUtils;
import events.impl.TransactionsEbClient;
import external.plaid.entities.Transaction;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionsEbClientTest {

    private final TransactionsEbClient transactionsEbClient;
    private final String SOURCE_NAME = "transactions.receive";

    private final EventBridgeClient eventBridgeClient;
    private final String EVENT_BUS_NAME = "EVENTBUS";
    private final ObjectMapper objectMapper;

    public TransactionsEbClientTest() {
        this.eventBridgeClient = mock(EventBridgeClient.class);
        this.objectMapper = new ObjectMapper();
        this.transactionsEbClient = new TransactionsEbClient(eventBridgeClient, EVENT_BUS_NAME, objectMapper);
    }


    /**
     * Tests that message serializes properly. Tests internal private methods.
     * @throws JsonProcessingException json exception
     */
    @Test
    public void publishesNewTransaction() throws JsonProcessingException {
        Transaction transaction = TransactionsTableUtils.createTransaction();
        ArgumentCaptor<PutEventsRequest> argumentCaptor = ArgumentCaptor.forClass(PutEventsRequest.class);
        this.transactionsEbClient.publishNewTransaction(transaction, SOURCE_NAME);
        verify(eventBridgeClient).putEvents(argumentCaptor.capture());

        PutEventsRequest capturedRequest = argumentCaptor.getValue();
        assertEquals(1, capturedRequest.entries().size());
        assertEquals(objectMapper.writeValueAsString(transaction),
                capturedRequest.entries().get(0).detail());
    }

    @Test
    public void publishesNewMultipleTransactions() throws JsonProcessingException {
        List<Transaction> transactions = TransactionsTableUtils.createTransactions(5);
        ArgumentCaptor<PutEventsRequest> argumentCaptor = ArgumentCaptor.forClass(PutEventsRequest.class);
        this.transactionsEbClient.publishNewTransactions(transactions, SOURCE_NAME);
        verify(eventBridgeClient).putEvents(argumentCaptor.capture());

        PutEventsRequest capturedRequest = argumentCaptor.getValue();
        assertEquals(transactions.size(), capturedRequest.entries().size());
        assertEquals(objectMapper.writeValueAsString(transactions.get(0)),
                capturedRequest.entries().get(0).detail());
    }
}
