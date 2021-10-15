package lambda.processors.transactions;

import dynamo.TransactionDAO;
import events.impl.TransactionsEbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import external.plaid.entities.Transaction;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class ReceiveTransactionsProcessor {
    private final TransactionDAO transactionDAO;
    private final TransactionsEbClient transactionsEbClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveTransactionsProcessor.class);

    @Inject
    public ReceiveTransactionsProcessor(TransactionDAO transactionDAO, TransactionsEbClient transactionsEbClient) {
        this.transactionDAO = transactionDAO;
        this.transactionsEbClient = transactionsEbClient;
    }

    /**
     * Filter for new transactions and save.
     * @param transactions : incoming transactions, from polling or from webhook push.
     * @return New Transactions saved to DDB.
     */
    public List<Transaction> saveAndReturnNewTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .filter(tx -> !(transactionExistsInDdb(tx)))
                .map(newTransaction -> {
                    transactionDAO.save(newTransaction);
                    this.transactionsEbClient.createNewTransactionEvent(newTransaction);
                    return newTransaction;
                })
                .collect(Collectors.toList());
    }

    private boolean transactionExistsInDdb(Transaction transaction) {
        List<Transaction> queryResult = transactionDAO.query(transaction.getUser(),
                transaction.getInstitutionName(),
                transaction.getAccountId(),
                transaction.getTransactionId());
        LOGGER.info(transaction.toString());
        LOGGER.info(queryResult.toString());
        return !queryResult.isEmpty();
    }

}
