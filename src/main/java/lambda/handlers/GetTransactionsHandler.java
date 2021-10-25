package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import dagger.DaggerPlaidComponent;
import lambda.processors.transactions.GetTransactionsProcessor;
import lambda.requests.transactions.GetTransactionsRequest;
import external.plaid.entities.Transaction;

import java.util.List;


// Params: Link --> User, InstitutionId,
// Transactions --> StartDate, EndDate?, InstitutionId, AccountName
public class GetTransactionsHandler implements RequestHandler<GetTransactionsRequest, List<Transaction>> {
    private final GetTransactionsProcessor processor;

    public GetTransactionsHandler() {this.processor = DaggerPlaidComponent.create().buildGetTransactionsProcessor(); }

    public GetTransactionsHandler(GetTransactionsProcessor processor) {
        this.processor = processor;
    }

    @Override
    public List<Transaction> handleRequest(GetTransactionsRequest request, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Getting Transactions for user: " +
                request.getUser() +
                "\n starting on " +
                request.getStartDate()
        );

        List<Transaction> transactions;
        if (request.getStartDate() != null) {
            transactions = processor.getTransactions(request.user, request.startDate);
        } else {
            transactions = processor.getTransactions(request.user);
        }

        logger.log("Returning cached " +
                transactions.size() +
                "\n transactions for " +
                request.getUser() +
                " \n starting on: " +
                request.getStartDate()
        );
        return transactions;

    }
}