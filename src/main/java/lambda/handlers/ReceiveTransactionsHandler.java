package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerAwsComponent;
import dynamo.NewTransactionDAO;
import events.impl.TransactionsEbClient;
import external.plaid.entities.Transaction;
import io.vavr.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class ReceiveTransactionsHandler implements RequestHandler<List<Transaction>, List<Transaction>> {
    private final NewTransactionDAO transactionDAO;
    private final TransactionsEbClient transactionsEbClient;

    private static final String SOURCE_NAME = "receiveTransactions";

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveTransactionsHandler.class);

    @Inject
    public ReceiveTransactionsHandler(NewTransactionDAO transactionDAO, TransactionsEbClient transactionsEbClient) {
        this.transactionDAO = transactionDAO;
        this.transactionsEbClient = transactionsEbClient;
    }

    public ReceiveTransactionsHandler() {
        this.transactionDAO = DaggerAwsComponent.create().buildTransactionDAO();
        this.transactionsEbClient = DaggerAwsComponent.create().buildTransactionsEbClient();
    }

    /**
     * Updates records of incoming {@link Transaction}s and emits event when a new transaction is encountered.
     * @param incomingTransactions transactions to save or update
     * @param context {@link Context} from AWS Lambda
     * @return A {@link List} of new transactions
     */
    @Override
    public List<Transaction> handleRequest(List<Transaction> incomingTransactions, Context context) {
        List<Transaction> newTransactions = saveAndReturnNewTransactions(incomingTransactions);
        this.transactionsEbClient.publishNewTransactions(newTransactions, SOURCE_NAME);
        return newTransactions;
    }

    private List<Transaction> saveAndReturnNewTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .map(transaction -> {
                    LOGGER.info("Updating record for transaction id {} ", transaction.getTransactionId());
                    boolean newTx = !transactionDAO.saveWithResponse(transaction);
                    LOGGER.info("Transaction id {} is new?: {} ", transaction.getTransactionId(), newTx);
                    return Tuple.of(transaction, newTx); })
                .filter(tuple -> tuple._2())
                .map(tuple -> tuple._1())
                .collect(Collectors.toList());
    }
}
