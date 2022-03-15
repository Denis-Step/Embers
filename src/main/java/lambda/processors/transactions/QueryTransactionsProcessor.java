package lambda.processors.transactions;

import dynamo.TransactionDAO;
import external.plaid.entities.Transaction;

import javax.inject.Inject;
import java.util.List;

public class QueryTransactionsProcessor {
    private final TransactionDAO transactionDAO;

    @Inject
    public QueryTransactionsProcessor(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    /**
     * Default query.
     * @param user User for transaction.
     * @param startDate Inclusive start date for transactions.
     * @return {@link List} of {@link Transaction}s.
     */
    public List<Transaction> getTransactions(String user, String startDate) {
        return transactionDAO.query(user, startDate);
    }

    /**
     * Get all transactions for a user. Will stress DDB, AVOID.
     * @param user User is partition key for transactions.
     * @return {@link List} of {@link Transaction}s.
     */
    public List<Transaction> getTransactions(String user) {
        return transactionDAO.query(user);
    }
}
