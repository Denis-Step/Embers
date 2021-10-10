package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import plaid.entities.Transaction;

public class SendMessageHandler implements RequestHandler<Transaction, String> {

    @Override
    public String handleRequest(Transaction newTransaction, Context context) {
        context.getLogger().log(newTransaction.toString());
        return newTransaction.toString();
    }

}
