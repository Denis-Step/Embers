package messages;

import external.plaid.entities.Transaction;
import messages.responses.MessageResponse;

import javax.inject.Inject;

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
