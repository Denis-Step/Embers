package messages;

import external.plaid.entities.Transaction;

/**
 * Converts Transactions & related objects to SMS.
 * Not static for a reason, this may take configuration
 * in the constructor in the future.
 */
public class TransactionSmsMessageConverter {

    public String createNewTransactionMessage(Transaction transaction) {
        return
                "New Transaction: " +
                transaction.description + " " +
                "for " +
                transaction.amount + " " +
                "at " +
                transaction.merchantName +
                " on " +
                transaction.institutionName;
    }
}
