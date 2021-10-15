package events;

import external.plaid.entities.Transaction;

import java.util.Collection;

/**
 * Allow different implementations of clients (such as SNS & EventBridge)
 */
public interface TransactionsEventCreator {

    void createNewTransactionEvent(
            Transaction transaction);

    void createNewTransactionEvent(
            Collection<Transaction> transactions
    );

}
