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
import java.util.List;
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
            LOGGER.info("Query returned: {}", queryResult.items().toString());
            return queryResult;
        } else {
            PageIterable<Transaction> queryResult = table.query(r -> r.queryConditional(
                    QueryConditional.keyEqualTo(
                            Key.builder()
                                    .partitionValue(user)
                                    .build()
                    )));
            LOGGER.info("Query returned: {}", queryResult.items().toString());
            return queryResult;
        }
    }
}
