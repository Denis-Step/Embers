package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerProcessorComponent;
import lambda.processors.transactions.PollTransactionsProcessor;
import lambda.requests.transactions.PollTransactionsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import external.plaid.entities.Transaction;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Loads new Transactions by querying Plaid.
 * Params: Link --> User, InstitutionId,
 * Transactions --> StartDate, EndDate?, InstitutionId, AccountName
 */
public class PollTransactionsHandler implements RequestHandler<PollTransactionsRequest, List<Transaction>> {
    private final PollTransactionsProcessor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(PollTransactionsHandler.class);
    private static final int DEFAULT_DATE_RANGE_DAYS = 30;

    public PollTransactionsHandler() {this.processor = DaggerProcessorComponent.create().buildPollTransactionsProcessor(); }

    /**
     * Takes PlaidItem and date range in format {yyyy-[m]m-[d]d}.
     * @param request request for new items
     * @param context
     * @return list of Transactions
     */
    public List<Transaction> handleRequest(PollTransactionsRequest request, Context context) {
        LOGGER.info("Polling for transactions for user {} between {} and {}",
                request.getPlaidItem().getUser(), request.getStartDate(), request.getEndDate());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        try {
            return processor.pollForTransactions(request.getPlaidItem(),
                    dateFormatter.parse(request.getStartDate()),
                    dateFormatter.parse((request.getEndDate())));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject
    public PollTransactionsHandler(PollTransactionsProcessor processor) {
        this.processor = processor;
    }

}
