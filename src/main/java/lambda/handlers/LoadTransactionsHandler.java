package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerPlaidComponent;
import dynamo.PlaidItemDAO;
import lambda.processors.transactions.LoadTransactionsProcessor;
import lambda.requests.transactions.PullNewTransactionsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import external.plaid.entities.Transaction;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;


/**
 * Loads new Transactions by querying Plaid.
 * Params: Link --> User, InstitutionId,
 * Transactions --> StartDate, EndDate?, InstitutionId, AccountName
 */
public class LoadTransactionsHandler implements RequestHandler<PullNewTransactionsRequest, List<Transaction>> {
    private final LoadTransactionsProcessor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadTransactionsHandler.class);
    private static final int DEFAULT_DATE_RANGE_DAYS = 30;

    public LoadTransactionsHandler() {this.processor = DaggerPlaidComponent.create().buildLoadTransactionsProcessor(); }

    public LoadTransactionsHandler(LoadTransactionsProcessor processor) {
        this.processor = processor;
    }

    /**
     * @param request Incoming lambda request.
     * @param context Lambda Context object.
     * @return List of new transactions from Plaid.
     */
    @Override
    public List<Transaction> handleRequest(PullNewTransactionsRequest request, Context context) {
        LOGGER.info("Loading Transactions for user: " +
                request.getUser() +
                "\n and institution: " +
                request.getInstitutionName()
                );

        // Validate dates.

        Date startDate;
        Date endDate;
        if (request.endDate == null) {
            endDate = new Date(System.currentTimeMillis());
        } else {
            endDate = Date.from(Instant.parse(request.endDate));
        }

        if (request.startDate == null) {
            startDate = Date.from(endDate.toInstant().minus(30, ChronoUnit.DAYS));
        } else {
            startDate = Date.from(Instant.parse(request.startDate));
        }

        try {
            List<Transaction> transactions = processor.pullNewTransactions(request.getUser(), request.institutionName,
                    startDate, endDate);

            LOGGER.info("Loaded " + transactions.size() + "\n transactions for " + request.getUser() +
                    " \n and institution: " + request.getInstitutionName());
            return transactions;
        } catch (PlaidItemDAO.ItemNotFoundException e) {
            // Rethrow exception to prevent lambda from succeeding.
            LOGGER.info("ItemException: " + e.getMessage());
            throw new RuntimeException("No Item Found for User " + request.user);
        } catch (PlaidItemDAO.MultipleItemsFoundException e) {
            LOGGER.info("ItemException: " + e.getMessage());
            throw new RuntimeException("Multiple Items Found for User " + request.user);
        } catch (PlaidItemDAO.ItemException e) {
            LOGGER.info("ItemException: " + e.getMessage());
            throw new RuntimeException("Unexpected ItemException for User " + request.user);
        }
    }


}
