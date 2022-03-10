package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import dagger.DaggerProcessorComponent;
import external.plaid.entities.PlaidItem;
import lambda.processors.transactions.PollTransactionsProcessor;
import lambda.requests.items.ImmutableGetItemRequest;
import lambda.requests.transactions.ImmutablePollTransactionsRequest;
import lambda.requests.transactions.PollTransactionsRequest;
import org.jetbrains.annotations.Nullable;
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
public class PollTransactionsHandler implements
        RequestHandler<PollTransactionsHandler.LambdaPollTransactionsRequest, List<Transaction>> {
    private final PollTransactionsProcessor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(PollTransactionsHandler.class);
    private static final int DEFAULT_DATE_RANGE_DAYS = 30;

    public PollTransactionsHandler() {this.processor = DaggerProcessorComponent.create().buildPollTransactionsProcessor(); }

    @Inject
    public PollTransactionsHandler(PollTransactionsProcessor processor) {
        this.processor = processor;
    }

    public List<Transaction> handleRequest(LambdaPollTransactionsRequest request, Context context) {
        return handleRequest(request.build(), context);
    }

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

    public static class LambdaPollTransactionsRequest {
        private final ImmutablePollTransactionsRequest.Builder builder;
        private final ObjectMapper objectMapper;

        private PlaidItem item;
        @Nullable private String accountId;
        private String startDate;
        private String endDate;

        public LambdaPollTransactionsRequest() {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Jdk8Module());
            this.builder = ImmutablePollTransactionsRequest.builder();
        }

        public ImmutablePollTransactionsRequest build () {
            return this.builder.build();
        }

        public PlaidItem getPlaidItem() {
            return item;
        }

        public void setPlaidItem(Object itemJson) {
            PlaidItem plaidItem = objectMapper.convertValue(itemJson, PlaidItem.class);
            this.builder.plaidItem(plaidItem);
            this.item = plaidItem;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.builder.accountId(accountId);
            this.accountId = accountId;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.builder.startDate(startDate);
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.builder.endDate(endDate);
            this.endDate = endDate;
        }

    }

}
