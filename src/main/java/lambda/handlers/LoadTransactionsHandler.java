package lambda.handlers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import dagger.DaggerAwsComponent;
import dagger.DaggerPlaidComponent;
import lambda.processors.TransactionProcessor;
import lambda.requests.CreateLinkTokenRequest;
import lambda.requests.GetTransactionsRequest;
import plaid.clients.LinkGrabber;
import plaid.entities.Transaction;

import java.util.List;


// Params: Link --> User, InstitutionId,
// Transactions --> StartDate, EndDate?, InstitutionId, AccountName
public class LoadTransactionsHandler implements RequestHandler<GetTransactionsRequest, List<Transaction>> {
    private final TransactionProcessor processor;

    public LoadTransactionsHandler() {this.processor = DaggerPlaidComponent.create().buildTransactionProcessor(); }

    public LoadTransactionsHandler(TransactionProcessor processor) {
        this.processor = processor;
    }

    @Override
    public List<Transaction> handleRequest(GetTransactionsRequest event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Loading Transactions for user: " +
                event.getUser() +
                "\n and institution: " +
                event.getInstitutionName()
                );
        try {
            List<Transaction> transactions = processor.pullFromPlaid(event);
            logger.log("Loaded " +
                    transactions.size() +
                    "\n transactions for " +
                    event.getUser() +
                    " \n and institution: " +
                    event.getInstitutionName()
            );
            return transactions;
        } catch (Exception e) {
            // Rethrow exception to prevent lambda from succeeding.
            logger.log("Exception: " + e.getMessage());
            throw new RuntimeException(String.format("Exception: %s", e.toString()));
        }
    }


}
