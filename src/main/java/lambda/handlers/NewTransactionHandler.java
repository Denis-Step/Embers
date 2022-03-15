package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerProcessorComponent;
import dynamo.PlaidItemDAO;
import lambda.processors.transactions.NewTransactionProcessor;
import external.plaid.entities.Transaction;

public class NewTransactionHandler implements RequestHandler<Transaction, String> {
    private final NewTransactionProcessor newTransactionProcessor;

    public NewTransactionHandler() {
        this.newTransactionProcessor = DaggerProcessorComponent.create().buildNewTransactionProcessor();
    }

    @Override
    public String handleRequest(Transaction transaction, Context context) {
        try {
            return newTransactionProcessor.process(transaction);
        } catch (PlaidItemDAO.ItemException e) {
            return e.getMessage();
        }
    }
}
