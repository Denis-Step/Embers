package events;

import external.plaid.entities.Transaction;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;

import java.util.Collection;

/**
 * Provides public API for events related to {@link Transaction} processing.
 */
public interface TransactionsEventPublisher {

    void publishNewTransaction(Transaction transaction, String sourceName);

    void publishNewTransactions(Collection<Transaction> transactions, String sourceName);

}
