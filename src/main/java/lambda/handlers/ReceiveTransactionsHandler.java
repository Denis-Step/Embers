package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerProcessorComponent;
import lambda.processors.transactions.ReceiveTransactionsProcessor;
import external.plaid.entities.Transaction;

import java.util.List;

public class ReceiveTransactionsHandler implements RequestHandler<List<Transaction>, List<Transaction>> {
    private final ReceiveTransactionsProcessor processor;

    public ReceiveTransactionsHandler(ReceiveTransactionsProcessor receiveTransactionsProcessor) {
        this.processor = receiveTransactionsProcessor;
    }

    public ReceiveTransactionsHandler() {
        this.processor = DaggerProcessorComponent.create().buildReceiveTransactionsProcessor();
    }

    @Override
    public List<Transaction> handleRequest(List<Transaction> incomingTransactions, Context context) {
        return this.processor.saveAndReturnNewTransactions(incomingTransactions);
    }
}
