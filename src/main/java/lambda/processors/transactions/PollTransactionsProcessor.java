package lambda.processors.transactions;

import dynamo.NewTransactionDAO;
import external.plaid.clients.TransactionsGrabber;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * CRUD Transaction-Related Functions & Wraps Plaid Clients for getting new Transactions.
 * @implSpec: Ignore item-related issues. Builds a general contract.
 */
public class PollTransactionsProcessor {
    private final TransactionsGrabber transactionsGrabber;

    @Inject
    public PollTransactionsProcessor(TransactionsGrabber transactionsGrabber) {
        this.transactionsGrabber = transactionsGrabber;
    }

    /**
     * Queries Plaid API for transactions within a given date range for a given {@link PlaidItem}.
     * @param plaidItem the item to fetch transactions for
     * @param startDate start date to check, inclusive
     * @param endDate end date to check, exclusive
     * @return List of transactions from Plaid
     */
    public List<Transaction> pollForTransactions(PlaidItem plaidItem, Date startDate, Date endDate) {
        return transactionsGrabber.requestTransactions(plaidItem.getUser(), plaidItem.getInstitutionId(),
                plaidItem.getAccessToken(), startDate, endDate);
    }
}
