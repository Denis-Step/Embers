package dynamo;

import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NewTransactionDAO {

    private final DynamoDbTable<Transaction> table;
    private static final Logger LOGGER = LoggerFactory.getLogger(NewTransactionDAO.class);

    @Inject
    public NewTransactionDAO(DynamoDbTable<Transaction> table) { this.table = table; }

    public List<Transaction> query(String user) {
        PageIterable<Transaction> transactionPages = this.paginatedQuery(user, null);
        return transactionPages.items().stream().collect(Collectors.toList());
    }

    public List<Transaction> query(String user, String dateTransactionId) {
        PageIterable<Transaction> transactionPages = this.paginatedQuery(user, dateTransactionId);
        return transactionPages.items().stream().collect(Collectors.toList());
    }

    public void save(Transaction transaction) { this.table.putItem(transaction); }

    public void delete(Transaction transaction) {this.table.deleteItem(transaction); }

    /**
     * This method stylistically throws in order to provide the items found inside the Exception.
     * This allows the caller to potentially recover by finding the intended invoice.
     * @param user
     * @param dateTransactionId
     * @return Optional containing whether or not the item was found.
     * @throws NewTransactionDAO.MultipleItemsFoundException
     */
    public Optional<Transaction> get(String user, String dateTransactionId) throws NewTransactionDAO.MultipleItemsFoundException {
        List<Transaction> transactionList = query(user, dateTransactionId);

        if (transactionList.size() == 0) {
            return Optional.empty();
        }

        if (transactionList.size() == 1) {
            return Optional.of(transactionList.get(0));
        } else {
            throw new NewTransactionDAO.MultipleItemsFoundException(String.format("%d Items Found",
                    transactionList.size()), transactionList);
        }
    }

    /**
     * @param user
     * @param date NULLABLE
     * @return Iterable from SDK.
     * Other queries rely on this method to allow logging and access to underlying pages.
     */
    private PageIterable<Transaction> paginatedQuery(String user, @Nullable String date) {
        LOGGER.info("Querying Transactions Table for user {} and dateTransactionId {}",
                user, date != null ? date : "NONE" );

        if (date != null) {
            PageIterable<Transaction> queryResult = table.query(r -> r.queryConditional(
                    QueryConditional.sortGreaterThanOrEqualTo(
                            Key.builder()
                                    .partitionValue(user)
                                    .sortValue(date)
                                    .build()
                    )));
            return queryResult;
        } else {
            PageIterable<Transaction> queryResult = table.query(r -> r.queryConditional(
                    QueryConditional.keyEqualTo(
                            Key.builder()
                                    .partitionValue(user)
                                    .build()
                    )));
            return queryResult;
        }
    }

    /**
     * Thrown from DAO methods that should only return 1 item but found more.
     */
    public static class MultipleItemsFoundException extends NewTransactionDAO.TransactionDAOException {
        private final Collection<Transaction> items;

        /**
         * @param message Exception message.
         * @param items items found.
         */
        public MultipleItemsFoundException(String message, List<Transaction> items) { super(message); this.items = items;}

        public Collection<Transaction> getItems() { return this.items; }
    }

    private static class TransactionDAOException extends Exception {
        // Check out and follow https://docs.oracle.com/javase/7/docs/api/java/lang/Exception.html

        public TransactionDAOException(String message) { super(message); }

        public TransactionDAOException(String message, Throwable cause) { super(message, cause); }
    }
}
