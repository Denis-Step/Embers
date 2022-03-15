package dynamo;

import external.plaid.entities.PlaidItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Add support for functional interface --> ScanExpression pattern.
 */
public class NewPlaidItemDAO {

    private final DynamoDbTable<PlaidItem> table;
    private static final Logger LOGGER = LoggerFactory.getLogger(NewPlaidItemDAO.class);

    @Inject
    public NewPlaidItemDAO(DynamoDbTable<PlaidItem> table) {
        this.table = table;
    }

    /**
     * @param user
     * @param institutionIdAccessToken
     * @return Optional containing whether or not the item was found.
     * @throws MultipleItemsFoundException
     * This method stylistically throws in order to provide the items found inside the Exception.
     * This allows the caller to potentially recover by finding the intended invoice.
     * @TODO: Fix this to use a sortKeyEquals condition instead of regular query. THIS IS A BUG.
     */
    public Optional<PlaidItem> get(String user, String institutionIdAccessToken) throws MultipleItemsFoundException {
        List<PlaidItem> plaidItemList = query(user, institutionIdAccessToken);

        if (plaidItemList.size() == 0) {
            return Optional.empty();
        }

        if (plaidItemList.size() == 1) {
            return Optional.of(plaidItemList.get(0));
        } else {
            throw new MultipleItemsFoundException(String.format("%d Items Found", plaidItemList.size()),
                    plaidItemList);
        }
    }

    /**
     * @param user partition key
     * @param institutionIdAccessToken {INSTITUTION_ID#ACCESS_TOKEN} Prefer this query.
     * @return List of PlaidItems.
     */
    public List<PlaidItem> query(String user, String institutionIdAccessToken) {
        PageIterable<PlaidItem> plaidItemPages = this.paginatedQuery(user, institutionIdAccessToken);
        return plaidItemPages.items().stream().collect(Collectors.toList());
    }


    /**
     * Queries by partition key only.
     * @param user User
     * @return List of PlaidItem's.
     */
    public List<PlaidItem> query(String user) {
        PageIterable<PlaidItem> plaidItemPages = this.paginatedQuery(user, null);
        return plaidItemPages.items().stream().collect(Collectors.toList());
    }

    /**
     * @param plaidItem PlaidItem
     */
    public void save(PlaidItem plaidItem) {
        this.table.putItem(plaidItem);
    }

    /**
     * @param plaidItem
     */
    public void delete(PlaidItem plaidItem) {
        this.table.deleteItem(plaidItem);
    }

    /**
     * @param user
     * @param institutionIdAccessToken NULLABLE
     * @return Iterable from SDK.
     * Other queries rely on this method to allow logging and access to underlying pages.
     */
    private PageIterable<PlaidItem> paginatedQuery(String user, String institutionIdAccessToken) {
        LOGGER.info("Querying PlaidItems Table for user {} and institutionIdAccessToken {}",
                user, institutionIdAccessToken != null ? institutionIdAccessToken : "NONE" );

        if (institutionIdAccessToken != null) {
            PageIterable<PlaidItem> queryResult = table.query(r -> r.queryConditional(
                    QueryConditional.sortBeginsWith(
                            Key.builder()
                            .partitionValue(user)
                            .sortValue(institutionIdAccessToken)
                            .build()
                    )));
            return queryResult;
        } else {
            PageIterable<PlaidItem> queryResult = table.query(r -> r.queryConditional(
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
    public static class MultipleItemsFoundException extends PlaitItemDAOException {
        private final Collection<PlaidItem> items;

        /**
         * @param message Exception message.
         * @param items items found.
         */
        public MultipleItemsFoundException(String message, List<PlaidItem> items) { super(message); this.items = items;}

        public Collection<PlaidItem> getItems() { return this.items; }
    }

    private static class PlaitItemDAOException extends Exception {
        // Check out and follow https://docs.oracle.com/javase/7/docs/api/java/lang/Exception.html

        public PlaitItemDAOException(String message) { super(message); }

        public PlaitItemDAOException(String message, Throwable cause) { super(message, cause); }
    }


}
