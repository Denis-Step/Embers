package lambda.processors.transactions;

import dynamo.NewPlaidItemDAO;
import dynamo.NewPlaidItemDAO.MultipleItemsFoundException;
import dynamo.NewTransactionDAO;
import dynamo.PlaidItemDAO;
import external.plaid.clients.TransactionsGrabber;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * CRUD Transaction-Related Functions & Wraps Plaid Clients for getting new Transactions.
 * @implSpec: Ignore item-related issues. Builds a general contract.
 */
public class TransactionProcessor {
    private final NewTransactionDAO transactionDAO;
    private final TransactionsGrabber transactionsGrabber;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProcessor.class);

    @Inject
    public TransactionProcessor(NewTransactionDAO transactionDAO, TransactionsGrabber transactionsGrabber) {
        this.transactionDAO = transactionDAO;
        this.transactionsGrabber = transactionsGrabber;
    }

    /**
     * Queries Plaid API for transactions within a given date range for a given {@link PlaidItem}.
     * @param plaidItem the item to fetch transactions for
     * @param startDate start date to check, inclusive
     * @param endDate end date to check, exclusive
     * @return
     */
    public List<Transaction> pullNewTransactions(PlaidItem plaidItem, Date startDate, Date endDate) {
        return transactionsGrabber.requestTransactions(plaidItem.getUser(), plaidItem.getInstitutionId(),
                plaidItem.getAccessToken(), startDate, endDate);
    }

//    // Exceptions
//    public static class ItemNotFoundException extends Exception {
//        private final String user;
//        private final String institution;
//
//        public ItemNotFoundException(String user, String institution) {
//            super(createDetailMessage(user, institution));
//            this.user = user;
//            this.institution = institution;
//        }
//
//        public String getUser() {
//            return user;
//        }
//
//        public String getInstitution() {
//            return institution;
//        }
//
//        private static String createDetailMessage(String user, String institution) {
//            return String.format("Cannot find item for user %s at institution %s", user, institution);
//        }
//    }
}
