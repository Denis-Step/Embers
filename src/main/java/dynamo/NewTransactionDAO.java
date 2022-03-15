package dynamo;

import external.plaid.entities.Transaction;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NewTransactionDAO {

    private final DynamoDbTable<Transaction> table;
    private static final String AMOUNT_INDEX = "amountIndex";
    private static final String DESCRIPTION_INDEX = "descriptionIndex";
    private static final String INSTITUTION_INDEX = "institutionNameIndex";
    private static final String ACCOUNT_INDEX = "accountIdIndex";
    private static final String TRANSACTION_ID_INDEX = "transactionIdIndex";
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

    public List<Transaction> queryByAmount(String user, double amount) {
        SdkIterable<Page<Transaction>> pageSdkIterable = paginatedQueryByAmount(user, amount);
        return pageSdkIterable.stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    public List<Transaction> queryByDescription(String user, String description) {
        SdkIterable<Page<Transaction>> pageSdkIterable = paginatedQueryByDescription(user, description);
        return pageSdkIterable.stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    public List<Transaction> queryByInstitutionName(String user, String institutionName) {
        SdkIterable<Page<Transaction>> pageSdkIterable = paginatedQueryByInstitution(user, institutionName);
        return pageSdkIterable.stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    public List<Transaction> queryByAccountId(String accountId) {
        SdkIterable<Page<Transaction>> pageSdkIterable = paginatedQueryByAccount(accountId, null);
        return pageSdkIterable.stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    public List<Transaction> queryByAccountId(String accountId, String dateTransactionId) {
        SdkIterable<Page<Transaction>> pageSdkIterable = paginatedQueryByAccount(accountId, dateTransactionId);
        return pageSdkIterable.stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    public void save(Transaction transaction) { this.table.putItem(transaction); }

    /**
     * Saves new {@link Transaction} or overwrites all attributes of existing item with provided item's attributes,
     * returns a boolean indicating whether the item previously existed or not.
     * @param transaction transaction to save
     * @return booleans indicating whether ot not an item with the same primary key already existed
     */
    public boolean saveWithResponse(Transaction transaction) {
        PutItemEnhancedResponse<Transaction> response = this.table.putItemWithResponse(
                PutItemEnhancedRequest.builder(Transaction.class)
                        .item(transaction)
                        .returnValues(ReturnValue.ALL_OLD)
                        .build());
        return response.attributes() != null;
    }

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

        return getOptionalTransactionOrThrow(transactionList);
    }

    public Optional<Transaction> getByTransactionId(String transactionId) throws MultipleItemsFoundException {
        SdkIterable<Page<Transaction>> pageSdkIterable = paginatedQueryByTransactionId(transactionId);
        List<Transaction> transactionList = pageSdkIterable.stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());

        return getOptionalTransactionOrThrow(transactionList);
    }

    private Optional<Transaction> getOptionalTransactionOrThrow(List<Transaction> transactionList) throws MultipleItemsFoundException {
        if (transactionList.size() == 0) {
            return Optional.empty();
        }

        if (transactionList.size() == 1) {
            return Optional.of(transactionList.get(0));
        } else {
            throw new MultipleItemsFoundException(String.format("%d Items Found",
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

    private SdkIterable<Page<Transaction>> paginatedQueryByAmount(String user, double amount) {
        LOGGER.info("Querying Transactions Table for user {} and amount {}", user, amount);

        return table.index(AMOUNT_INDEX).query(r -> r.queryConditional(
                QueryConditional.sortGreaterThanOrEqualTo(
                        Key.builder()
                                .partitionValue(user)
                                .sortValue(amount)
                                .build()
                )));
    }

    private SdkIterable<Page<Transaction>> paginatedQueryByDescription(String user, String description) {
        LOGGER.info("Querying Transactions Table for user {} and description {}", user, description);

        return table.index(DESCRIPTION_INDEX).query(
                QueryConditional.sortBeginsWith(
                        Key.builder()
                                .partitionValue(user)
                                .sortValue(description)
                                .build()
                ));
    }

    private SdkIterable<Page<Transaction>> paginatedQueryByInstitution(String user, String institutionName) {
        LOGGER.info("Querying Transactions Table for user {} and institutionName {}", user, institutionName);

        return table.index(INSTITUTION_INDEX).query(
                QueryConditional.keyEqualTo(
                        Key.builder()
                                .partitionValue(user)
                                .sortValue(institutionName)
                        .build()));
    }

    private SdkIterable<Page<Transaction>> paginatedQueryByAccount(String accountId, @Nullable String dateTransactionId) {
        LOGGER.info("Querying Transactions Table for accountId {} and dateTransactionId {}"
                , accountId, dateTransactionId);

        if (dateTransactionId != null) {
            return table.index(ACCOUNT_INDEX).query(
                    QueryConditional.sortGreaterThanOrEqualTo(
                            Key.builder()
                                    .partitionValue(accountId)
                                    .sortValue(dateTransactionId)
                                    .build()
                    ));
        } else {
            return table.index(ACCOUNT_INDEX).query(
                    QueryConditional.keyEqualTo(
                            Key.builder()
                                    .partitionValue(accountId)
                                    .build()
                    ));
        }
    }

    private SdkIterable<Page<Transaction>> paginatedQueryByTransactionId (String transactionId) {
        LOGGER.info("Querying Transactions Table for transactionId {}", transactionId);

        return table.index(TRANSACTION_ID_INDEX).query(
                QueryConditional.keyEqualTo(
                        Key.builder()
                                .partitionValue(transactionId)
                                .build())
        );
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
