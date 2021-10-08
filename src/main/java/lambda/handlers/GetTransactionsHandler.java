package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import dagger.DaggerPlaidComponent;
import lambda.processors.TransactionProcessor;
import lambda.requests.transactions.GetTransactionsRequest;
import plaid.entities.Transaction;

import java.util.List;


// Params: Link --> User, InstitutionId,
// Transactions --> StartDate, EndDate?, InstitutionId, AccountName
public class GetTransactionsHandler implements RequestHandler<GetTransactionsRequest, List<Transaction>> {
    private final TransactionProcessor processor;

    public GetTransactionsHandler() {this.processor = DaggerPlaidComponent.create().buildTransactionProcessor(); }

    public GetTransactionsHandler(TransactionProcessor processor) {
        this.processor = processor;
    }

    @Override
    public List<Transaction> handleRequest(GetTransactionsRequest event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Getting Transactions for user: " +
                event.getUser() +
                "\n and institution: " +
                event.getInstitutionName()
        );
        List<Transaction> transactions = processor.getTransactions(event);
        logger.log("Returning cached " +
                transactions.size() +
                "\n transactions for " +
                event.getUser() +
                " \n and institution: " +
                event.getInstitutionName()
        );
        return transactions;
    }
}