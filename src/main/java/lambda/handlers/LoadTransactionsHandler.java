package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerPlaidComponent;
import lambda.processors.ItemProcessor;
import lambda.processors.LoadTransactionsProcessor;
import lambda.requests.transactions.PullNewTransactionsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plaid.entities.Transaction;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;


// Params: Link --> User, InstitutionId,
// Transactions --> StartDate, EndDate?, InstitutionId, AccountName
public class LoadTransactionsHandler implements RequestHandler<PullNewTransactionsRequest, List<Transaction>> {
    private final LoadTransactionsProcessor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadTransactionsHandler.class);
    private static final int DEFAULT_DATE_RANGE_DAYS = 30;

    public LoadTransactionsHandler() {this.processor = DaggerPlaidComponent.create().buildLoadTransactionsProcessor(); }

    public LoadTransactionsHandler(LoadTransactionsProcessor processor) {
        this.processor = processor;
    }

    @Override
    public List<Transaction> handleRequest(PullNewTransactionsRequest event, Context context) {
        LOGGER.info("Loading Transactions for user: " +
                event.getUser() +
                "\n and institution: " +
                event.getInstitutionName()
                );

        Date startDate;
        Date endDate;
        if (event.endDate == null) {
            endDate = new Date(System.currentTimeMillis());
        } else {
            endDate = Date.from(Instant.parse(event.endDate));
        }

        if (event.startDate == null) {
            startDate = Date.from(endDate.toInstant().minus(30, ChronoUnit.DAYS));
        } else {
            startDate = Date.from(Instant.parse(event.startDate));
        }

        try {
            List<Transaction> transactions = processor.pullNewTransactions(event.getUser(), event.institutionName,
                    startDate, endDate);
            LOGGER.info("Loaded " +
                    transactions.size() +
                    "\n transactions for " +
                    event.getUser() +
                    " \n and institution: " +
                    event.getInstitutionName()
            );
            return transactions;
        } catch (ItemProcessor.ItemNotFoundException e) {
            // Rethrow exception to prevent lambda from succeeding.
            LOGGER.info("ItemException: " + e.getMessage());
            throw new RuntimeException("No Item Found for User " + event.user);
        } catch (ItemProcessor.MultipleItemsFoundException e) {
            LOGGER.info("ItemException: " + e.getMessage());
            throw new RuntimeException("Multiple Items Found for User " + event.user);
        } catch (ItemProcessor.ItemException e) {
            LOGGER.info("ItemException: " + e.getMessage());
            throw new RuntimeException("Unexpected ItemException for User " + event.user);
        }
        catch (IOException e) {
            LOGGER.info("IOException: " + e.getMessage());
            throw new RuntimeException(String.format("Exception: %s", e.getStackTrace()));
        }
    }


}
